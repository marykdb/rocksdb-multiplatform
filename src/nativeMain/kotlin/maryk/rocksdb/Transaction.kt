@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import cnames.structs.rocksdb_snapshot_t
import cnames.structs.rocksdb_transaction_t
import cnames.structs.rocksdb_pinnableslice_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pin
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import maryk.ByteBuffer
import maryk.asSizeT
import maryk.asUInt64
import maryk.sizeTToInt
import maryk.toByteArray
import maryk.toCheckedLong
import maryk.usePointer
import maryk.usePointers
import maryk.wrapWithErrorThrower
import maryk.wrapWithMultiErrorThrower
import platform.posix.size_tVar
import kotlin.concurrent.AtomicInt
import kotlin.concurrent.AtomicReference

private inline fun <T> CPointer<rocksdb_pinnableslice_t>.usePinnedSlice(block: (CPointer<rocksdb_pinnableslice_t>) -> T): T {
    try {
        return block(this)
    } finally {
        rocksdb.rocksdb_pinnableslice_destroy(this)
    }
}

private fun CPointer<rocksdb_pinnableslice_t>.toByteArray(): ByteArray = memScoped {
    val valueLength = alloc<size_tVar>()
    val value = rocksdb.rocksdb_pinnableslice_value(this@toByteArray, valueLength.ptr)
    if (valueLength.value == 0.asSizeT()) {
        ByteArray(0)
    } else {
        requireNotNull(value) {
            "RocksDB returned null pinned transaction value for ${valueLength.value} bytes."
        }.toByteArray(valueLength.value)
    }
}

private class SnapshotNotifierState(
    val transaction: Transaction,
    val notifier: AbstractTransactionNotifier,
) {
    private val disposed = AtomicInt(0)
    private val ref = StableRef.create(this)

    fun asCPointer(): COpaquePointer {
        return ref.asCPointer()
    }

    fun disposeOnce() {
        if (disposed.compareAndSet(0, 1)) {
            ref.dispose()
        }
    }
}

private val snapshotCreatedCallback = staticCFunction<COpaquePointer?, CPointer<rocksdb_snapshot_t>?, Unit> { state, snapshotPtr ->
    var notifierState: SnapshotNotifierState? = null
    try {
        notifierState = state?.asStableRef<SnapshotNotifierState>()?.get()
            ?: return@staticCFunction
        snapshotPtr
            ?.let { notifierState.transaction.borrowSnapshot(it, freeWrapperOnClose = false) }
            ?.let(notifierState.notifier::snapshotCreated)
    } catch (_: Throwable) {
    } finally {
        notifierState?.transaction?.clearPendingSnapshotNotifier(notifierState)
        notifierState?.disposeOnce()
    }
}

private fun copyResultToBuffer(result: ByteArray, value: ByteBuffer): GetStatus {
    val copyLength = minOf(result.size, value.remaining())
    if (copyLength > 0) {
        value.put(result, 0, copyLength)
    }
    return GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
}

private fun concatParts(parts: Array<ByteArray>, label: String): ByteArray {
    require(parts.isNotEmpty()) { "$label parts must not be empty" }
    var totalSize = 0
    for (part in parts) {
        require(part.size <= Int.MAX_VALUE - totalSize) { "$label parts are too large" }
        totalSize += part.size
    }

    val result = ByteArray(totalSize)
    var offset = 0
    for (part in parts) {
        part.copyInto(result, offset)
        offset += part.size
    }
    return result
}

private inline fun <T> usePinnedKeys(
    keys: List<ByteArray>,
    emptyKeyPointer: CPointer<ByteVar>,
    pointers: CPointer<CPointerVar<ByteVar>>,
    sizes: CPointer<size_tVar>,
    block: () -> T,
): T {
    val pinnedKeys = ArrayList<Pinned<ByteArray>>(keys.size)
    try {
        keys.forEachIndexed { index, bytes ->
            sizes[index] = bytes.size.asSizeT()
            if (bytes.isEmpty()) {
                pointers[index] = emptyKeyPointer
            } else {
                val pinned = bytes.pin()
                pinnedKeys += pinned
                pointers[index] = pinned.addressOf(0)
            }
        }
        return block()
    } finally {
        pinnedKeys.forEach { it.unpin() }
    }
}

internal interface TransactionOwner {
    fun registerBorrowedTransaction(transaction: Transaction)
    fun unregisterBorrowedTransaction(transaction: Transaction)
}

actual class Transaction(
    internal val native: CPointer<rocksdb_transaction_t>,
): RocksObject() {
    private var owner: TransactionOwner? = null
    private val defaultReadOptions = ReadOptions()
    private val pendingSnapshotNotifier = AtomicReference<SnapshotNotifierState?>(null)
    private val borrowedSnapshots = mutableSetOf<Snapshot>()
    private val borrowedIterators = mutableSetOf<RocksIterator>()

    internal fun attachTo(owner: TransactionOwner): Transaction {
        checkOwningHandle()
        this.owner?.unregisterBorrowedTransaction(this)
        this.owner = owner
        owner.registerBorrowedTransaction(this)
        return this
    }

    private fun checkOpenForRead(readOptions: ReadOptions = defaultReadOptions) {
        checkOwningHandle()
        readOptions.checkOpenForRead()
    }

    private fun checkOpenColumnFamily(columnFamilyHandle: ColumnFamilyHandle) {
        columnFamilyHandle.checkOwningHandle()
    }

    override fun close() {
        val transactionOwner = owner
        owner = null
        transactionOwner?.unregisterBorrowedTransaction(this)
        if (tryClose()) {
            disposePendingSnapshotNotifier()
            invalidateBorrowedSnapshots()
            invalidateBorrowedIterators()
            rocksdb.rocksdb_transaction_destroy(native)
            defaultReadOptions.close()
            super.close()
        }
    }

    internal fun invalidateFromOwner() {
        owner = null
        if (tryClose()) {
            disposePendingSnapshotNotifier()
            invalidateBorrowedSnapshots()
            invalidateBorrowedIterators()
            rocksdb.rocksdb_transaction_destroy(native)
            defaultReadOptions.close()
            super.close()
        }
    }

    actual fun setSnapshot() {
        checkOwningHandle()
        invalidateBorrowedSnapshots()
        rocksdb.rocksdb_transaction_set_snapshot(native)
    }

    actual fun setSnapshotOnNextOperation() {
        checkOwningHandle()
        disposePendingSnapshotNotifier()
        invalidateBorrowedSnapshots()
        rocksdb.rocksdb_transaction_set_snapshot_on_next_operation(native, null, null)
    }

    actual fun setSnapshotOnNextOperation(transactionNotifier: AbstractTransactionNotifier) {
        checkOwningHandle()
        disposePendingSnapshotNotifier()
        invalidateBorrowedSnapshots()
        val notifierState = SnapshotNotifierState(this, transactionNotifier)
        pendingSnapshotNotifier.value = notifierState
        rocksdb.rocksdb_transaction_set_snapshot_on_next_operation(
            native,
            notifierState.asCPointer(),
            snapshotCreatedCallback
        )
    }

    internal fun clearPendingSnapshotNotifier(notifierState: Any) {
        val state = notifierState as? SnapshotNotifierState ?: return
        pendingSnapshotNotifier.compareAndSet(state, null)
    }

    internal fun prepareForReuse() {
        checkOwningHandle()
        disposePendingSnapshotNotifier()
        invalidateBorrowedSnapshots()
        invalidateBorrowedIterators()
    }

    private fun disposePendingSnapshotNotifier() {
        while (true) {
            val notifierState = pendingSnapshotNotifier.value ?: return
            if (pendingSnapshotNotifier.compareAndSet(notifierState, null)) {
                notifierState.disposeOnce()
                return
            }
        }
    }

    actual fun getSnapshot(): Snapshot? {
        checkOwningHandle()
        val snapshotPtr = rocksdb.rocksdb_transaction_get_snapshot(native)
        return snapshotPtr?.let {
            borrowSnapshot(it, freeWrapperOnClose = true)
        }
    }

    actual fun clearSnapshot() {
        checkOwningHandle()
        invalidateBorrowedSnapshots()
        rocksdb.rocksdb_transaction_clear_snapshot(native)
    }

    internal fun unregisterBorrowedSnapshot(snapshot: Snapshot) {
        borrowedSnapshots.remove(snapshot)
    }

    internal fun borrowSnapshot(
        native: CPointer<rocksdb_snapshot_t>,
        freeWrapperOnClose: Boolean,
    ): Snapshot =
        Snapshot(native, transactionOwner = this, freeWrapperOnClose = freeWrapperOnClose)
            .also(borrowedSnapshots::add)

    private fun invalidateBorrowedSnapshots() {
        if (borrowedSnapshots.isEmpty()) return
        val snapshots = borrowedSnapshots.toList()
        borrowedSnapshots.clear()
        snapshots.forEach { it.invalidateFromTransaction() }
    }

    internal fun unregisterBorrowedIterator(iterator: RocksIterator) {
        borrowedIterators.remove(iterator)
    }

    internal fun registerBorrowedIterator(iterator: RocksIterator) {
        borrowedIterators.add(iterator)
    }

    private fun borrowIterator(iterator: RocksIterator): RocksIterator =
        iterator.also(borrowedIterators::add)

    private fun invalidateBorrowedIterators() {
        if (borrowedIterators.isEmpty()) return
        val iterators = borrowedIterators.toList()
        borrowedIterators.clear()
        iterators.forEach { it.invalidateFromOwner() }
    }

    actual fun prepare() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_prepare(native, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun commit() {
        checkOwningHandle()
        invalidateBorrowedIterators()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_commit(native, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun rollback() {
        checkOwningHandle()
        invalidateBorrowedIterators()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_rollback(native, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun setSavePoint() {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_set_savepoint(native)
    }

    @Throws(RocksDBException::class)
    actual fun rollbackToSavePoint() {
        checkOwningHandle()
        invalidateBorrowedIterators()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_rollback_to_savepoint(native, error)
        }
    }

    actual fun get(readOptions: ReadOptions, columnFamilyHandle: ColumnFamilyHandle, key: ByteArray): ByteArray? =
        memScoped {
            checkOpenForRead(readOptions)
            checkOpenColumnFamily(columnFamilyHandle)
            val errPtr = allocPointerTo<ByteVar>()
            errPtr.value = null
            val value = key.usePointer { keyPointer ->
                rocksdb.rocksdb_transaction_get_pinned_cf(
                    native,
                    readOptions.native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    errPtr.ptr
                )
            }
            errPtr.value?.let { error ->
                val message = try {
                    error.toKString()
                } finally {
                    rocksdb.rocksdb_free(error)
                }
                throw RocksDBException(message)
            }
            value?.usePinnedSlice { it.toByteArray() }
        }

    actual fun get(readOptions: ReadOptions, key: ByteArray): ByteArray? =
        memScoped {
            checkOpenForRead(readOptions)
            val errPtr = allocPointerTo<ByteVar>()
            errPtr.value = null
            val value = key.usePointer { keyPointer ->
                rocksdb.rocksdb_transaction_get_pinned(
                    native,
                    readOptions.native,
                    keyPointer,
                    key.size.asSizeT(),
                    errPtr.ptr
                )
            }
            errPtr.value?.let { error ->
                val message = try {
                    error.toKString()
                } finally {
                    rocksdb.rocksdb_free(error)
                }
                throw RocksDBException(message)
            }
            value?.usePinnedSlice { it.toByteArray() }
        }

    actual fun get(opt: ReadOptions, key: ByteArray, value: ByteArray): GetStatus = memScoped {
        val result = get(opt, key)
        if (result == null) {
            // Not found: return status with NotFound and no required size.
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        } else {
            // Copy as many bytes as possible into value.
            val copyLength = minOf(result.size, value.size)
            result.copyInto(value, 0, 0, copyLength)
            // Return full length as required size along with Ok status.
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun get(
        opt: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ): GetStatus =
        memScoped {
            val result = get(opt, columnFamilyHandle, key)
            if (result == null) {
                GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
            } else {
                val copyLength = minOf(result.size, value.size)
                result.copyInto(value, 0, 0, copyLength)
                GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
            }
        }

    actual fun get(
        opt: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteBuffer,
        value: ByteBuffer
    ): GetStatus {
        // Extract key into a temporary array.
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val result = get(opt, columnFamilyHandle, keyArray)
        return if (result == null) {
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        } else {
            copyResultToBuffer(result, value)
        }
    }

    actual fun get(opt: ReadOptions, key: ByteBuffer, value: ByteBuffer): GetStatus {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val result = get(opt, keyArray)
        return if (result == null) {
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, "NotFound"), 0)
        } else {
            copyResultToBuffer(result, value)
        }
    }

    actual fun multiGetAsList(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> = memScoped {
        checkOpenForRead(readOptions)
        columnFamilyHandles.forEach(::checkOpenColumnFamily)
        if (keys.isEmpty()) return@memScoped emptyList()
        if (columnFamilyHandles.size != keys.size) {
            throw IllegalArgumentException("For each key there must be a related column family handle.")
        }

        wrapWithMultiErrorThrower(keys.size) { error ->
            val columnFamilies = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyHandles.size)
            columnFamilyHandles.forEachIndexed { index, handle ->
                columnFamilies[index] = handle.native
            }

            val keyList = allocArray<CPointerVar<ByteVar>>(keys.size)
            val keyListSizes = allocArray<size_tVar>(keys.size)
            val emptyKeyPointer = allocArray<ByteVar>(1)
            val valueList = allocArray<CPointerVar<ByteVar>>(keys.size)
            val valueListSizes = allocArray<size_tVar>(keys.size)
            for (index in keys.indices) {
                valueList[index] = null
                valueListSizes[index] = 0u
            }

            usePinnedKeys(keys, emptyKeyPointer, keyList, keyListSizes) {
                rocksdb.rocksdb_transaction_multi_get_cf(
                    txn = native,
                    options = readOptions.native,
                    column_families = columnFamilies,
                    num_keys = keys.size.asSizeT(),
                    keys_list = keyList,
                    keys_list_sizes = keyListSizes,
                    values_list = valueList,
                    values_list_sizes = valueListSizes,
                    errs = error,
                )
            }

            try {
                List(keys.size) { index ->
                    valueList[index]?.toByteArray(valueListSizes[index])
                        ?: get(readOptions, columnFamilyHandles[index], keys[index])
                }
            } finally {
                for (index in keys.indices) {
                    valueList[index]?.let { rocksdb.rocksdb_free(it) }
                }
            }
        }
    } ?: emptyList()

    actual fun multiGetAsList(readOptions: ReadOptions, keys: List<ByteArray>): List<ByteArray?> =
        memScoped {
            checkOpenForRead(readOptions)
            if (keys.isEmpty()) return@memScoped emptyList()

            wrapWithMultiErrorThrower(keys.size) { error ->
                val keyList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val keyListSizes = allocArray<size_tVar>(keys.size)
                val emptyKeyPointer = allocArray<ByteVar>(1)
                val valueList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val valueListSizes = allocArray<size_tVar>(keys.size)
                for (index in keys.indices) {
                    valueList[index] = null
                    valueListSizes[index] = 0u
                }

                usePinnedKeys(keys, emptyKeyPointer, keyList, keyListSizes) {
                    rocksdb.rocksdb_transaction_multi_get(
                        txn = native,
                        options = readOptions.native,
                        num_keys = keys.size.asSizeT(),
                        keys_list = keyList,
                        keys_list_sizes = keyListSizes,
                        values_list = valueList,
                        values_list_sizes = valueListSizes,
                        errs = error,
                    )
                }

                try {
                    List(keys.size) { index ->
                        valueList[index]?.toByteArray(valueListSizes[index]) ?: get(readOptions, keys[index])
                    }
                } finally {
                    for (index in keys.indices) {
                        valueList[index]?.let { rocksdb.rocksdb_free(it) }
                    }
                }
            } ?: emptyList()
        }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        exclusive: Boolean,
    ): ByteArray? = getForUpdate(readOptions, columnFamilyHandle, key, exclusive, doValidate = true)

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        exclusive: Boolean,
        doValidate: Boolean,
    ): ByteArray? = memScoped {
        checkOpenForRead(readOptions)
        checkOpenColumnFamily(columnFamilyHandle)
        // Allocate error pointer and length holder.
        val errPtr = allocPointerTo<ByteVar>()
        errPtr.value = null
        // Call the native function using the correct parameter ordering:
        // txn, options, column_family, key, klen, vlen, exclusive, errptr
        val value = key.usePointer { keyPointer ->
            rocksdb.rocksdb_transaction_get_pinned_for_update_cf(
                native,
                readOptions.native,
                columnFamilyHandle.native,
                keyPointer,
                key.size.asSizeT(),
                if (exclusive) 1.toUByte() else 0.toUByte(),
                errPtr.ptr
            )
        }
        errPtr.value?.let { error ->
            val message = try {
                error.toKString()
            } finally {
                rocksdb.rocksdb_free(error)
            }
            throw RocksDBException(message)
        }
        value?.usePinnedSlice { it.toByteArray() }
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        key: ByteArray,
        exclusive: Boolean
    ): ByteArray? = memScoped {
        checkOpenForRead(readOptions)
        val errPtr = allocPointerTo<ByteVar>()
        errPtr.value = null
        // Call the native function using the correct parameter ordering:
        // txn, options, key, klen, vlen, exclusive, errptr
        val value = key.usePointer { keyPointer ->
            rocksdb.rocksdb_transaction_get_pinned_for_update(
                native,
                readOptions.native,
                keyPointer,
                key.size.asSizeT(),
                if (exclusive) 1.toUByte() else 0.toUByte(),
                errPtr.ptr
            )
        }
        errPtr.value?.let { error ->
            val message = try {
                error.toKString()
            } finally {
                rocksdb.rocksdb_free(error)
            }
            throw RocksDBException(message)
        }
        value?.usePinnedSlice { it.toByteArray() }
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray,
        exclusive: Boolean
    ): GetStatus = memScoped {
        val result = getForUpdate(readOptions, columnFamilyHandle, key, exclusive, doValidate = false)
        if (result == null) {
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        } else {
            val copyLength = minOf(result.size, value.size)
            result.copyInto(value, 0, 0, copyLength)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray,
        exclusive: Boolean,
        doValidate: Boolean
    ): GetStatus = memScoped {
        val result = getForUpdate(readOptions, columnFamilyHandle, key, exclusive, doValidate)
        if (result == null)
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        else {
            val copyLength = minOf(result.size, value.size)
            result.copyInto(value, 0, 0, copyLength)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        key: ByteArray,
        value: ByteArray,
        exclusive: Boolean
    ): GetStatus = memScoped {
        val result = getForUpdate(readOptions, key, exclusive)
        if (result == null)
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        else {
            val copyLength = minOf(result.size, value.size)
            result.copyInto(value, 0, 0, copyLength)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteBuffer,
        value: ByteBuffer,
        exclusive: Boolean,
    ): GetStatus = getForUpdate(readOptions, columnFamilyHandle, key, value, exclusive, doValidate = false)

    actual fun getForUpdate(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteBuffer,
        value: ByteBuffer,
        exclusive: Boolean,
        doValidate: Boolean
    ): GetStatus {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val result = getForUpdate(readOptions, columnFamilyHandle, keyArray, exclusive, doValidate)
        return if (result == null)
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        else {
            copyResultToBuffer(result, value)
        }
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        key: ByteBuffer,
        value: ByteBuffer,
        exclusive: Boolean
    ): GetStatus {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val result = getForUpdate(readOptions, keyArray, exclusive)
        return if (result == null)
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, null), 0)
        else {
            copyResultToBuffer(result, value)
        }
    }

    actual fun multiGetForUpdateAsList(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> = memScoped {
        checkOpenForRead(readOptions)
        columnFamilyHandles.forEach(::checkOpenColumnFamily)
        if (keys.isEmpty()) return@memScoped emptyList()
        if (columnFamilyHandles.size != keys.size) {
            throw IllegalArgumentException("For each key there must be a related column family handle.")
        }

        wrapWithMultiErrorThrower(keys.size) { error ->
            val columnFamilies = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyHandles.size)
            columnFamilyHandles.forEachIndexed { index, handle ->
                columnFamilies[index] = handle.native
            }

            val keyList = allocArray<CPointerVar<ByteVar>>(keys.size)
            val keyListSizes = allocArray<size_tVar>(keys.size)
            val emptyKeyPointer = allocArray<ByteVar>(1)
            val valueList = allocArray<CPointerVar<ByteVar>>(keys.size)
            val valueListSizes = allocArray<size_tVar>(keys.size)
            for (index in keys.indices) {
                valueList[index] = null
                valueListSizes[index] = 0u
            }

            usePinnedKeys(keys, emptyKeyPointer, keyList, keyListSizes) {
                rocksdb.rocksdb_transaction_multi_get_for_update_cf(
                    txn = native,
                    options = readOptions.native,
                    column_families = columnFamilies,
                    num_keys = keys.size.asSizeT(),
                    keys_list = keyList,
                    keys_list_sizes = keyListSizes,
                    values_list = valueList,
                    values_list_sizes = valueListSizes,
                    errs = error,
                )
            }

            try {
                List(keys.size) { index ->
                    valueList[index]?.toByteArray(valueListSizes[index])
                        ?: getForUpdate(readOptions, columnFamilyHandles[index], keys[index], exclusive = false)
                }
            } finally {
                for (index in keys.indices) {
                    valueList[index]?.let { rocksdb.rocksdb_free(it) }
                }
            }
        } ?: emptyList()
    }

    actual fun multiGetForUpdateAsList(readOptions: ReadOptions, keys: List<ByteArray>): List<ByteArray?> =
        memScoped {
            checkOpenForRead(readOptions)
            if (keys.isEmpty()) return@memScoped emptyList()

            wrapWithMultiErrorThrower(keys.size) { error ->
                val keyList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val keyListSizes = allocArray<size_tVar>(keys.size)
                val emptyKeyPointer = allocArray<ByteVar>(1)
                val valueList = allocArray<CPointerVar<ByteVar>>(keys.size)
                val valueListSizes = allocArray<size_tVar>(keys.size)
                for (index in keys.indices) {
                    valueList[index] = null
                    valueListSizes[index] = 0u
                }

                usePinnedKeys(keys, emptyKeyPointer, keyList, keyListSizes) {
                    rocksdb.rocksdb_transaction_multi_get_for_update(
                        txn = native,
                        options = readOptions.native,
                        num_keys = keys.size.asSizeT(),
                        keys_list = keyList,
                        keys_list_sizes = keyListSizes,
                        values_list = valueList,
                        values_list_sizes = valueListSizes,
                        errs = error,
                    )
                }

                try {
                    List(keys.size) { index ->
                        valueList[index]?.toByteArray(valueListSizes[index])
                            ?: getForUpdate(readOptions, keys[index], exclusive = false)
                    }
                } finally {
                    for (index in keys.indices) {
                        valueList[index]?.let { rocksdb.rocksdb_free(it) }
                    }
                }
            } ?: emptyList()
        }

    actual fun getIterator(): RocksIterator {
        checkOpenForRead()
        return borrowIterator(RocksIterator(
            requireNotNull(rocksdb.rocksdb_transaction_create_iterator(native, defaultReadOptions.native)) {
                "RocksDB returned null transaction iterator"
            },
            transactionOwner = this,
        ))
    }

    actual fun getIterator(readOptions: ReadOptions): RocksIterator {
        checkOpenForRead(readOptions)
        return borrowIterator(RocksIterator(
            requireNotNull(rocksdb.rocksdb_transaction_create_iterator(native, readOptions.native)) {
                "RocksDB returned null transaction iterator"
            },
            transactionOwner = this,
        ))
    }

    actual fun getIterator(readOptions: ReadOptions, columnFamilyHandle: ColumnFamilyHandle): RocksIterator {
        checkOpenForRead(readOptions)
        checkOpenColumnFamily(columnFamilyHandle)
        return borrowIterator(RocksIterator(
            requireNotNull(rocksdb.rocksdb_transaction_create_iterator_cf(native, readOptions.native, columnFamilyHandle.native)) {
                "RocksDB returned null transaction column-family iterator"
            },
            transactionOwner = this,
        ))
    }

    actual fun getIterator(columnFamilyHandle: ColumnFamilyHandle): RocksIterator {
        checkOpenForRead()
        checkOpenColumnFamily(columnFamilyHandle)
        return borrowIterator(RocksIterator(
            requireNotNull(rocksdb.rocksdb_transaction_create_iterator_cf(native, defaultReadOptions.native, columnFamilyHandle.native)) {
                "RocksDB returned null transaction column-family iterator"
            },
            transactionOwner = this,
        ))
    }


    actual fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_put_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_put(
                    native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>,
        valueParts: Array<ByteArray>
    ) {
        val key = concatParts(keyParts, "key")
        val value = concatParts(valueParts, "value")
        put(columnFamilyHandle, key, value)
    }

    actual fun put(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>) {
        val key = concatParts(keyParts, "key")
        val value = concatParts(valueParts, "value")
        put(key, value)
    }

    actual fun put(key: ByteBuffer, value: ByteBuffer) {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val valueArray = ByteArray(value.remaining())
        value[valueArray]
        put(keyArray, valueArray)
    }

    actual fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteBuffer, value: ByteBuffer) {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val valueArray = ByteArray(value.remaining())
        value[valueArray]
        put(columnFamilyHandle, keyArray, valueArray)
    }

    actual fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_merge_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_merge(
                    native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun merge(key: ByteBuffer, value: ByteBuffer) {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val valueArray = ByteArray(value.remaining())
        value[valueArray]
        merge(keyArray, valueArray)
    }

    actual fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteBuffer, value: ByteBuffer) {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val valueArray = ByteArray(value.remaining())
        value[valueArray]
        merge(columnFamilyHandle, keyArray, valueArray)
    }

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
               rocksdb.rocksdb_transaction_delete_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun delete(key: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_transaction_delete(
                    native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun putUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_put_untracked_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun putUntracked(key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_put_untracked(
                    native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun putUntracked(
        columnFamilyHandle: ColumnFamilyHandle,
        keyParts: Array<ByteArray>,
        valueParts: Array<ByteArray>
    ) {
        val key = concatParts(keyParts, "key")
        val value = concatParts(valueParts, "value")
        putUntracked(columnFamilyHandle, key, value)
    }

    actual fun putUntracked(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>) {
        val key = concatParts(keyParts, "key")
        val value = concatParts(valueParts, "value")
        putUntracked(key, value)
    }

    actual fun mergeUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_merge_untracked_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun mergeUntracked(key: ByteArray, value: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_transaction_merge_untracked(
                    native,
                    keyPointer,
                    key.size.asSizeT(),
                    valuePointer,
                    value.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun mergeUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteBuffer, value: ByteBuffer) {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val valueArray = ByteArray(value.remaining())
        value[valueArray]
        mergeUntracked(columnFamilyHandle, keyArray, valueArray)
    }

    actual fun mergeUntracked(key: ByteBuffer, value: ByteBuffer) {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val valueArray = ByteArray(value.remaining())
        value[valueArray]
        mergeUntracked(keyArray, valueArray)
    }

    actual fun deleteUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_transaction_delete_untracked_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun deleteUntracked(key: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_transaction_delete_untracked(
                    native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    actual fun putLogData(logData: ByteArray) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            logData.usePointer { logDataPointer ->
                rocksdb.rocksdb_transaction_put_log_data(
                    native,
                    logDataPointer,
                    logData.size.asSizeT()
                )
            }
        }
    }

    actual fun disableIndexing() {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_disable_indexing(native)
    }

    actual fun enableIndexing() {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_enable_indexing(native)
    }

    actual fun getNumKeys(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_num_keys(native).toCheckedLong("transaction key count")
    }

    actual fun getNumPuts(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_num_puts(native).toCheckedLong("transaction put count")
    }

    actual fun getNumDeletes(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_num_deletes(native).toCheckedLong("transaction delete count")
    }

    actual fun getNumMerges(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_num_merges(native).toCheckedLong("transaction merge count")
    }

    actual fun getElapsedTime(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_elapsed_time(native).toCheckedLong("transaction elapsed time")
    }

    actual fun getWriteBatch(): WriteBatchWithIndex {
        checkOwningHandle()
        val wbPtr = rocksdb.rocksdb_transaction_get_write_batch(native)
        return WriteBatchWithIndex(
            requireNotNull(wbPtr) { "RocksDB returned null transaction write batch" },
            ownsNative = false,
            freeBorrowedWrapper = true,
            supportsBorrowedWriteBatch = false,
        )
    }

    actual fun setLockTimeout(lockTimeout: Long) {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_set_lock_timeout(native, lockTimeout.convert())
    }

    actual fun getWriteOptions(): WriteOptions? {
        checkOwningHandle()
        val optionsPtr = rocksdb.rocksdb_transaction_get_write_options(native) ?: return null
        return WriteOptions(optionsPtr)
    }

    actual fun setWriteOptions(writeOptions: WriteOptions) {
        checkOwningHandle()
        writeOptions.checkOpenHandle()
        rocksdb.rocksdb_transaction_set_write_options(native, writeOptions.native)
    }

    actual fun undoGetForUpdate(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOwningHandle()
        checkOpenColumnFamily(columnFamilyHandle)
        key.usePointer { keyPointer ->
            rocksdb.rocksdb_transaction_undo_get_for_update_cf(
                native,
                columnFamilyHandle.native,
                keyPointer,
                key.size.asSizeT(),
            )
        }
    }

    actual fun undoGetForUpdate(key: ByteArray) {
        checkOwningHandle()
        key.usePointer { keyPointer ->
            rocksdb.rocksdb_transaction_undo_get_for_update(
                native,
                keyPointer,
                key.size.asSizeT(),
            )
        }
    }

    actual fun rebuildFromWriteBatch(writeBatch: WriteBatch) {
        checkOwningHandle()
        writeBatch.checkOpenHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_rebuild_from_writebatch(native, writeBatch.native, error)
        }
    }

    actual fun getCommitTimeWriteBatch(): WriteBatch? {
        checkOwningHandle()
        val wbPtr = rocksdb.rocksdb_transaction_get_commit_time_writebatch(native) ?: return null
        return WriteBatch(wbPtr)
    }

    actual fun setLogNumber(logNumber: Long) {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_set_log_number(native, logNumber.asUInt64())
    }

    actual fun getLogNumber(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_log_number(native).toCheckedLong("transaction log number")
    }

    actual fun setName(transactionName: String) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            val nameBytes = transactionName.encodeToByteArray()
            nameBytes.usePointer { namePtr ->
                rocksdb.rocksdb_transaction_set_name(
                    native,
                    namePtr,
                    nameBytes.size.asSizeT(),
                    error,
                )
            }
        }
    }

    @ExperimentalForeignApi
    actual fun getName(): String = memScoped {
        checkOwningHandle()
        val nameLenVar = alloc<size_tVar>()
        val namePtr = rocksdb.rocksdb_transaction_get_name(native, nameLenVar.ptr)
        val nameLen = sizeTToInt(nameLenVar.value, "transaction name length")
        namePtr?.let {
            try {
                if (nameLen == 0) "" else it.toByteArray(nameLenVar.value).decodeToString()
            } finally {
                rocksdb.rocksdb_free(it)
            }
        } ?: if (nameLen == 0) "" else error("RocksDB returned null transaction name with length $nameLen")
    }

    actual fun getID(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_id(native).toCheckedLong("transaction id")
    }

    actual fun getId(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_get_global_id(native).toCheckedLong("transaction global id")
    }

    actual fun isDeadlockDetect(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_is_deadlock_detect(native) != 0.toUByte()
    }

    actual fun getWaitingTxns(): WaitingTransactions = memScoped {
        checkOwningHandle()
        val cfIdVar = alloc<UIntVar>()

        val maxKeyLen = 256
        val keyBuffer = allocArray<ByteVar>(maxKeyLen)
        for (i in 0 until maxKeyLen) {
            keyBuffer[i] = 0
        }

        val numTxnsVar = alloc<size_tVar>()
        numTxnsVar.value = 0.asSizeT()

        val txnIdsPtr = rocksdb.rocksdb_transaction_get_waiting_txns(
            native,
            cfIdVar.ptr,
            keyBuffer,
            maxKeyLen.asSizeT(),
            numTxnsVar.ptr
        )

        val columnFamilyId = cfIdVar.value.toLong()
        val keyBytes = keyBuffer.readBytes(maxKeyLen)
        val keyLength = keyBytes.indexOf(0).takeIf { it >= 0 } ?: keyBytes.size
        val keyString = keyBytes.decodeToString(endIndex = keyLength)
        val count = sizeTToInt(numTxnsVar.value, "waiting transaction count")

        val txnIdsArray = try {
            if (count > 0) {
                val txnIds = requireNotNull(txnIdsPtr) {
                    "RocksDB returned null waiting transaction ids for $count transactions"
                }
                LongArray(count) { index ->
                    txnIds[index].toCheckedLong("waiting transaction id")
                }
            } else {
                LongArray(0)
            }
        } finally {
            if (txnIdsPtr != null) {
                rocksdb.rocksdb_free(txnIdsPtr)
            }
        }

        WaitingTransactions(
            columnFamilyId = columnFamilyId,
            key = keyString,
            transactionIds = txnIdsArray
        )
    }

    actual fun getState(): TransactionState {
        checkOwningHandle()
        val state = rocksdb.rocksdb_transaction_get_state(native)
        return getTransactionState(state.convert())
    }
}
