package maryk.rocksdb

import cnames.structs.rocksdb_transaction_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import maryk.ByteBuffer
import maryk.byteArrayToCPointer
import maryk.toByteArray
import maryk.wrapWithErrorThrower

actual class Transaction(
    internal val native: CPointer<rocksdb_transaction_t>,
): RocksObject() {
    private val defaultReadOptions = ReadOptions()

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_transaction_destroy(native)
            defaultReadOptions.close()
            super.close()
        }
    }

    actual fun setSnapshot() {
        rocksdb.rocksdb_transaction_set_snapshot(native)
    }

    actual fun setSnapshotOnNextOperation() {
        rocksdb.rocksdb_transaction_set_snapshot_on_next_operation(native, null ,null)
    }

    actual fun setSnapshotOnNextOperation(transactionNotifier: AbstractTransactionNotifier) {
        val notifierStableRef: StableRef<AbstractTransactionNotifier> = StableRef.create(transactionNotifier)

        rocksdb.rocksdb_transaction_set_snapshot_on_next_operation(native, notifierStableRef.asCPointer(), staticCFunction { state, snapshotPtr ->
            val ref = state?.asStableRef<AbstractTransactionNotifier>()
            val notifier = ref?.get()
            notifier?.snapshotCreated(snapshotPtr?.let { Snapshot(it) } ?: throw IllegalStateException("Snapshot is null"))
            ref?.dispose()
        })
    }

    actual fun getSnapshot(): Snapshot? {
        val snapshotPtr = rocksdb.rocksdb_transaction_get_snapshot(native)
        return snapshotPtr?.let { Snapshot(it) }
    }

    actual fun clearSnapshot() {
        rocksdb.rocksdb_transaction_clear_snapshot(native)
    }

    actual fun prepare() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_prepare(native, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun commit() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_commit(native, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun rollback() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_rollback(native, error)
        }
    }

    @Throws(RocksDBException::class)
    actual fun setSavePoint() {
        rocksdb.rocksdb_transaction_set_savepoint(native)
    }

    @Throws(RocksDBException::class)
    actual fun rollbackToSavePoint() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_rollback_to_savepoint(native, error)
        }
    }

    actual fun get(readOptions: ReadOptions, columnFamilyHandle: ColumnFamilyHandle, key: ByteArray): ByteArray? =
        memScoped {
            val errPtr = allocPointerTo<ByteVar>()
            val valueLen = alloc<ULongVar>()
            val valuePtr = rocksdb.rocksdb_transaction_get_cf(
                native,
                readOptions.native,
                columnFamilyHandle.native,
                byteArrayToCPointer(key, 0, key.size),
                key.size.convert(),
                valueLen.ptr,
                errPtr.ptr
            )
            if (errPtr.value != null) {
                throw RocksDBException(errPtr.value!!.toKString())
            }
            valuePtr?.readBytes(valueLen.value.convert())
        }

    actual fun get(readOptions: ReadOptions, key: ByteArray): ByteArray? =
        memScoped {
            val errPtr = allocPointerTo<ByteVar>()
            val valueLen = alloc<ULongVar>()
            val valuePtr = rocksdb.rocksdb_transaction_get(
                native,
                readOptions.native,
                byteArrayToCPointer(key, 0, key.size),
                key.size.convert(),
                valueLen.ptr,
                errPtr.ptr
            )
            if (errPtr.value != null) {
                throw RocksDBException(errPtr.value!!.toKString())
            }
            valuePtr?.readBytes(valueLen.value.convert())
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
            // Copy the full result into the destination ByteBuffer.
            value.put(result)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun get(opt: ReadOptions, key: ByteBuffer, value: ByteBuffer): GetStatus {
        val keyArray = ByteArray(key.remaining())
        key[keyArray]
        val result = get(opt, keyArray)
        return if (result == null) {
            GetStatus(Status(StatusCode.NotFound, StatusSubCode.None, "NotFound"), 0)
        } else {
            value.put(result)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun multiGetAsList(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> =
        keys.mapIndexed { index, key ->
            get(readOptions, columnFamilyHandles[index], key)
        }

    actual fun multiGetAsList(readOptions: ReadOptions, keys: List<ByteArray>): List<ByteArray?> =
        keys.map { key -> get(readOptions, key) }

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
        // Allocate error pointer and length holder.
        val errPtr = allocPointerTo<ByteVar>()
        val valueLen = alloc<ULongVar>()
        // Call the native function using the correct parameter ordering:
        // txn, options, column_family, key, klen, vlen, exclusive, errptr
        val valuePtr = rocksdb.rocksdb_transaction_get_for_update_cf(
            native,
            readOptions.native,
            columnFamilyHandle.native,
            byteArrayToCPointer(key, 0, key.size),
            key.size.convert(),
            valueLen.ptr,
            if (exclusive) 1.toUByte() else 0.toUByte(),
            errPtr.ptr
        )
        if (errPtr.value != null) {
            throw RocksDBException(errPtr.value!!.toKString())
        }
        valuePtr?.readBytes(valueLen.value.convert())
    }

    actual fun getForUpdate(
        readOptions: ReadOptions,
        key: ByteArray,
        exclusive: Boolean
    ): ByteArray? = memScoped {
        val errPtr = allocPointerTo<ByteVar>()
        val valueLen = alloc<ULongVar>()
        // Call the native function using the correct parameter ordering:
        // txn, options, key, klen, vlen, exclusive, errptr
        val valuePtr = rocksdb.rocksdb_transaction_get_for_update(
            native,
            readOptions.native,
            byteArrayToCPointer(key, 0, key.size),
            key.size.convert(),
            valueLen.ptr,
            if (exclusive) 1.toUByte() else 0.toUByte(),
            errPtr.ptr
        )
        if (errPtr.value != null) {
            throw RocksDBException(errPtr.value!!.toKString())
        }
        valuePtr?.readBytes(valueLen.value.convert())
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
            value.put(result)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
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
            value.put(result)
            GetStatus(Status(StatusCode.Ok, StatusSubCode.None, null), result.size)
        }
    }

    actual fun multiGetForUpdateAsList(
        readOptions: ReadOptions,
        columnFamilyHandles: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray?> =
        keys.mapIndexed { index, key ->
            getForUpdate(readOptions, columnFamilyHandles[index], key, exclusive = false)
        }

    actual fun multiGetForUpdateAsList(readOptions: ReadOptions, keys: List<ByteArray>): List<ByteArray?> =
        keys.map { key ->
            getForUpdate(readOptions, key, exclusive = false)
        }

    actual fun getIterator(): RocksIterator {
        return RocksIterator(rocksdb.rocksdb_transaction_create_iterator(native, defaultReadOptions.native)!!)
    }

    actual fun getIterator(readOptions: ReadOptions): RocksIterator {
        return RocksIterator(rocksdb.rocksdb_transaction_create_iterator(native, readOptions.native)!!)
    }

    actual fun getIterator(readOptions: ReadOptions, columnFamilyHandle: ColumnFamilyHandle): RocksIterator {
        return RocksIterator(rocksdb.rocksdb_transaction_create_iterator_cf(native, readOptions.native, columnFamilyHandle.native)!!)
    }

    actual fun getIterator(columnFamilyHandle: ColumnFamilyHandle): RocksIterator {
        return RocksIterator(rocksdb.rocksdb_transaction_create_iterator_cf(native, defaultReadOptions.native, columnFamilyHandle.native)!!)
    }


    actual fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_put_cf(
                    native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
                    error
                )
            }
        }
    }

    actual fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_put(
                    native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
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
        val key = keyParts.reduce { acc, bytes -> acc + bytes }
        val value = valueParts.reduce { acc, bytes -> acc + bytes }
        put(columnFamilyHandle, key, value)
    }

    actual fun put(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>) {
        val key = keyParts.reduce { acc, bytes -> acc + bytes }
        val value = valueParts.reduce { acc, bytes -> acc + bytes }
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
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_merge_cf(
                    native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
                    error
                )
            }
        }
    }

    actual fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_merge(
                    native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
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
        wrapWithErrorThrower { error ->
            memScoped {
               rocksdb.rocksdb_transaction_delete_cf(
                    native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    error
                )
            }
        }
    }

    actual fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_delete(
                    native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    error
                )
            }
        }
    }

    actual fun putUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_put_untracked_cf(
                    native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
                    error
                )
            }
        }
    }

    actual fun putUntracked(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_put_untracked(
                    native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
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
        val key = keyParts.reduce { acc, bytes -> acc + bytes }
        val value = valueParts.reduce { acc, bytes -> acc + bytes }
        putUntracked(columnFamilyHandle, key, value)
    }

    actual fun putUntracked(keyParts: Array<ByteArray>, valueParts: Array<ByteArray>) {
        val key = keyParts.reduce { acc, bytes -> acc + bytes }
        val value = valueParts.reduce { acc, bytes -> acc + bytes }
        putUntracked(key, value)
    }

    actual fun mergeUntracked(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_merge_untracked_cf(
                    native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
                    error
                )
            }
        }
    }

    actual fun mergeUntracked(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_merge_untracked(
                    native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    byteArrayToCPointer(value, 0, value.size),
                    value.size.convert(),
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
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_delete_untracked_cf(
                    native,
                    columnFamilyHandle.native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    error
                )
            }
        }
    }

    actual fun deleteUntracked(key: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_delete_untracked(
                    native,
                    byteArrayToCPointer(key, 0, key.size),
                    key.size.convert(),
                    error
                )
            }
        }
    }

    actual fun putLogData(logData: ByteArray) {
        wrapWithErrorThrower { error ->
            memScoped {
                rocksdb.rocksdb_transaction_put_log_data(
                    native,
                    byteArrayToCPointer(logData, 0, logData.size),
                    logData.size.convert()
                )
            }
        }
    }

    actual fun disableIndexing() {
        rocksdb.rocksdb_transaction_disable_indexing(native)
    }

    actual fun enableIndexing() {
        rocksdb.rocksdb_transaction_enable_indexing(native)
    }

    actual fun getNumKeys(): Long = rocksdb.rocksdb_transaction_get_num_keys(native).convert()

    actual fun getNumPuts(): Long = rocksdb.rocksdb_transaction_get_num_puts(native).convert()

    actual fun getNumDeletes(): Long = rocksdb.rocksdb_transaction_get_num_deletes(native).convert()

    actual fun getNumMerges(): Long {
        return rocksdb.rocksdb_transaction_get_num_merges(native).convert()
    }

    actual fun getElapsedTime(): Long {
        return rocksdb.rocksdb_transaction_get_elapsed_time(native).convert()
    }

    actual fun getWriteBatch(): WriteBatchWithIndex {
        val wbPtr = rocksdb.rocksdb_transaction_get_write_batch(native)
        return WriteBatchWithIndex(wbPtr!!).also {
            it.disownHandle()
        }
    }

    actual fun setLockTimeout(lockTimeout: Long) {
        rocksdb.rocksdb_transaction_set_lock_timeout(native, lockTimeout.convert())
    }

    actual fun getWriteOptions(): WriteOptions? {
        val optionsPtr = rocksdb.rocksdb_transaction_get_write_options(native) ?: return null
        return WriteOptions(optionsPtr)
    }

    actual fun setWriteOptions(writeOptions: WriteOptions) {
        rocksdb.rocksdb_transaction_set_write_options(native, writeOptions.native)
    }

    actual fun undoGetForUpdate(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        memScoped {
            rocksdb.rocksdb_transaction_undo_get_for_update_cf(
                native,
                columnFamilyHandle.native,
                byteArrayToCPointer(key, 0, key.size),
                key.size.convert(),
            )
        }
    }

    actual fun undoGetForUpdate(key: ByteArray) {
        memScoped {
            rocksdb.rocksdb_transaction_undo_get_for_update(
                native,
                byteArrayToCPointer(key, 0, key.size),
                key.size.convert(),
            )
        }
    }

    actual fun rebuildFromWriteBatch(writeBatch: WriteBatch) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_transaction_rebuild_from_writebatch(native, writeBatch.native, error)
        }
    }

    actual fun getCommitTimeWriteBatch(): WriteBatch? {
        val wbPtr = rocksdb.rocksdb_transaction_get_commit_time_writebatch(native) ?: return null
        return WriteBatch(wbPtr)
    }

    actual fun setLogNumber(logNumber: Long) {
        rocksdb.rocksdb_transaction_set_log_number(native, logNumber.convert())
    }

    actual fun getLogNumber(): Long {
        return rocksdb.rocksdb_transaction_get_log_number(native).convert()
    }

    actual fun setName(transactionName: String) {
        wrapWithErrorThrower { error ->
            transactionName.cstr.usePinned { pinned ->
                rocksdb.rocksdb_transaction_set_name(native, transactionName, transactionName.length.convert(), error)
            }
        }
    }

    @ExperimentalForeignApi
    actual fun getName(): String = memScoped {
        val nameLenVar = alloc<ULongVar>()
        val namePtr = rocksdb.rocksdb_transaction_get_name(native, nameLenVar.ptr)
        namePtr?.toByteArray(nameLenVar.value)?.decodeToString() ?: ""
    }

    actual fun getID(): Long {
        return rocksdb.rocksdb_transaction_get_id(native).convert()
    }

    actual fun getId(): Long {
        return rocksdb.rocksdb_transaction_get_global_id(native).convert()
    }

    actual fun isDeadlockDetect(): Boolean = rocksdb.rocksdb_transaction_is_deadlock_detect(native) != 0.toUByte()

    actual fun getWaitingTxns(): WaitingTransactions = memScoped {
        val cfIdVar = alloc<UIntVar>()

        val maxKeyLen = 256
        val keyBuffer = allocArray<ByteVar>(maxKeyLen)
        for (i in 0 until maxKeyLen) {
            keyBuffer[i] = 0
        }

        val numTxnsVar = alloc<ULongVar>()
        numTxnsVar.value = 0UL

        val txnIdsPtr = rocksdb.rocksdb_transaction_get_waiting_txns(
            native,
            cfIdVar.ptr,
            keyBuffer,
            maxKeyLen.convert(),
            numTxnsVar.ptr
        )

        val columnFamilyId = cfIdVar.value.toLong()
        val keyString = keyBuffer.toKString()
        val count = numTxnsVar.value.toInt()

        val txnIdsArray = if (txnIdsPtr != null && count > 0) {
            LongArray(count) { index ->
                txnIdsPtr[index].toLong()
            }
        } else {
            LongArray(0)
        }

        if (txnIdsPtr != null) {
            rocksdb.rocksdb_free(txnIdsPtr)
        }

        WaitingTransactions(
            columnFamilyId = columnFamilyId,
            key = keyString,
            transactionIds = txnIdsArray
        )
    }

    actual fun getState(): TransactionState {
        val state = rocksdb.rocksdb_transaction_get_state(native)
        return getTransactionState(state.convert())
    }
}
