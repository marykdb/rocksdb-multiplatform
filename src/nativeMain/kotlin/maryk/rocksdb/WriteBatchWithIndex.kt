@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_wi_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.sizeTToInt
import maryk.toByteArray
import maryk.usePointer
import maryk.usePointers
import maryk.wrapWithErrorThrower
import platform.posix.free
import platform.posix.size_t
import platform.posix.size_tVar
import rocksdb.rocksdb_free
import rocksdb.rocksdb_writebatch_wi_create

private class WriteBatchWithIndexLookupState(
    private val key: ByteArray
) {
    var found = false
        private set
    var value: ByteArray? = null
        private set
    var failure: Throwable? = null
        private set

    fun putColumnFamilyIfMatches(
        columnFamilyId: UInt,
        expectedColumnFamilyId: UInt,
        keyPointer: CPointer<ByteVar>?,
        keyLength: size_t,
        valuePointer: CPointer<ByteVar>?,
        valueLength: size_t
    ) {
        if (columnFamilyId != expectedColumnFamilyId) return
        putIfMatches(keyPointer, keyLength, valuePointer, valueLength)
    }

    fun deleteColumnFamilyIfMatches(
        columnFamilyId: UInt,
        expectedColumnFamilyId: UInt,
        keyPointer: CPointer<ByteVar>?,
        keyLength: size_t
    ) {
        if (columnFamilyId != expectedColumnFamilyId) return
        deleteIfMatches(keyPointer, keyLength)
    }

    fun putIfMatches(keyPointer: CPointer<ByteVar>?, keyLength: size_t, valuePointer: CPointer<ByteVar>?, valueLength: size_t) {
        if (!matches(keyPointer, keyLength)) return
        found = true
        value = if (valueLength == 0.asSizeT()) {
            ByteArray(0)
        } else {
            val pointer = valuePointer
            if (pointer == null) {
                failure = IllegalStateException("RocksDB reported a write batch value with $valueLength bytes but returned null.")
                null
            } else {
                try {
                    pointer.toByteArray(valueLength)
                } catch (throwable: Throwable) {
                    failure = throwable
                    null
                }
            }
        }
    }

    fun deleteIfMatches(keyPointer: CPointer<ByteVar>?, keyLength: size_t) {
        if (!matches(keyPointer, keyLength)) return
        found = false
        value = null
    }

    private fun matches(keyPointer: CPointer<ByteVar>?, keyLength: size_t): Boolean {
        val length = try {
            sizeTToInt(keyLength, "write batch key")
        } catch (throwable: Throwable) {
            failure = throwable
            return false
        }
        if (length != key.size) return false
        if (length == 0) return true
        val pointer = keyPointer
        if (pointer == null) {
            failure = IllegalStateException("RocksDB reported a write batch key with $keyLength bytes but returned null.")
            return false
        }
        return try {
            pointer.readBytes(length).contentEquals(key)
        } catch (throwable: Throwable) {
            failure = throwable
            false
        }
    }
}

private val writeBatchWithIndexPutCallback = staticCFunction<COpaquePointer?, CPointer<ByteVar>?, size_t, CPointer<ByteVar>?, size_t, Unit> { state, key, keyLength, value, valueLength ->
    try {
        state?.asStableRef<WriteBatchWithIndexLookupState>()?.get()
            ?.putIfMatches(key, keyLength, value, valueLength)
    } catch (_: Throwable) {
    }
}

private val writeBatchWithIndexDeleteCallback = staticCFunction<COpaquePointer?, CPointer<ByteVar>?, size_t, Unit> { state, key, keyLength ->
    try {
        state?.asStableRef<WriteBatchWithIndexLookupState>()?.get()
            ?.deleteIfMatches(key, keyLength)
    } catch (_: Throwable) {
    }
}

private class WriteBatchWithIndexColumnFamilyLookupState(
    key: ByteArray,
    val columnFamilyId: UInt,
) {
    val lookup = WriteBatchWithIndexLookupState(key)
}

private val writeBatchWithIndexPutColumnFamilyCallback = staticCFunction<COpaquePointer?, UInt, CPointer<ByteVar>?, size_t, CPointer<ByteVar>?, size_t, Unit> { state, columnFamilyId, key, keyLength, value, valueLength ->
    try {
        state?.asStableRef<WriteBatchWithIndexColumnFamilyLookupState>()?.get()?.let {
            it.lookup.putColumnFamilyIfMatches(columnFamilyId, it.columnFamilyId, key, keyLength, value, valueLength)
        }
    } catch (_: Throwable) {
    }
}

private val writeBatchWithIndexDeleteColumnFamilyCallback = staticCFunction<COpaquePointer?, UInt, CPointer<ByteVar>?, size_t, Unit> { state, columnFamilyId, key, keyLength ->
    try {
        state?.asStableRef<WriteBatchWithIndexColumnFamilyLookupState>()?.get()?.let {
            it.lookup.deleteColumnFamilyIfMatches(columnFamilyId, it.columnFamilyId, key, keyLength)
        }
    } catch (_: Throwable) {
    }
}

private val writeBatchWithIndexMergeColumnFamilyCallback = staticCFunction<COpaquePointer?, UInt, CPointer<ByteVar>?, size_t, CPointer<ByteVar>?, size_t, Unit> { _, _, _, _, _, _ ->
}

actual class WriteBatchWithIndex(
    internal val native: CPointer<rocksdb_writebatch_wi_t>,
    private var ownedComparator: AbstractComparator? = null,
    private val ownsNative: Boolean = true,
    private val freeBorrowedWrapper: Boolean = false,
    private val supportsBorrowedWriteBatch: Boolean = true,
) : AbstractWriteBatch() {
    // The C iterate fallback only reports put/delete records, so disable it
    // after records it cannot observe.
    private var batchOnlyFallbackSafe = true
    private val borrowedIterators = mutableSetOf<RocksIterator>()
    private val borrowedWBWIIterators = mutableSetOf<WBWIRocksIterator>()

    init {
        if (!ownsNative) {
            borrowHandle()
        }
    }

    actual constructor() : this(false)

    actual constructor(overwriteKey: Boolean) : this(
        requireNotNull(rocksdb_writebatch_wi_create(0.asSizeT(), if (overwriteKey) 1.convert() else 0.convert())) {
            "Unable to allocate RocksDB write batch with index"
        }
    )

    actual constructor(fallbackIndexComparator: AbstractComparator, reservedBytes: Int, overwriteKey: Boolean) : this(
        createWithComparator(fallbackIndexComparator, reservedBytes, overwriteKey),
        fallbackIndexComparator,
    )

    actual fun newIterator(columnFamilyHandle: ColumnFamilyHandle): WBWIRocksIterator {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        val iterator = rocksdb.rocksdb_writebatch_wi_create_iterator_cf(native, columnFamilyHandle.native)
        return borrowWBWIIterator(WBWIRocksIterator(
            requireNotNull(iterator) {
                "RocksDB returned null write-batch-with-index column-family iterator"
            },
            this,
        ))
    }

    actual fun newIterator(): WBWIRocksIterator {
        checkOpenHandle()
        val iterator = rocksdb.rocksdb_writebatch_wi_create_iterator(native)
        return borrowWBWIIterator(WBWIRocksIterator(
            requireNotNull(iterator) {
                "RocksDB returned null write-batch-with-index iterator"
            },
            this,
        ))
    }

    actual fun newIteratorWithBase(columnFamilyHandle: ColumnFamilyHandle, baseIterator: RocksIterator): RocksIterator {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        check(baseIterator.isOwningHandle()) { "Base iterator is already closed or transferred." }
        val baseOwners = baseIterator.transferWrapperOwnershipToNativeAndDetachOwners()
        val iterator = rocksdb.rocksdb_writebatch_wi_create_iterator_with_base_cf(
            native,
            baseIterator.native,
            columnFamilyHandle.native
        )
        return borrowIterator(RocksIterator(
            requireNotNull(iterator) {
                "RocksDB returned null write-batch-with-index column-family iterator"
            },
            dbOwner = baseOwners.dbOwner,
            transactionOwner = baseOwners.transactionOwner,
            writeBatchWithIndexOwner = this,
        ))
    }

    actual fun newIteratorWithBase(
        columnFamilyHandle: ColumnFamilyHandle,
        baseIterator: RocksIterator,
        readOptions: ReadOptions?
    ): RocksIterator {
        if (readOptions == null) return newIteratorWithBase(columnFamilyHandle, baseIterator)
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        readOptions.checkOwningHandle()
        check(baseIterator.isOwningHandle()) { "Base iterator is already closed or transferred." }
        val baseOwners = baseIterator.transferWrapperOwnershipToNativeAndDetachOwners()
        val iterator = rocksdb.rocksdb_writebatch_wi_create_iterator_with_base_cf_readopts(
            native,
            baseIterator.native,
            columnFamilyHandle.native,
            readOptions.native,
        )
        return borrowIterator(RocksIterator(
            requireNotNull(iterator) {
                "RocksDB returned null write-batch-with-index column-family iterator"
            },
            dbOwner = baseOwners.dbOwner,
            transactionOwner = baseOwners.transactionOwner,
            writeBatchWithIndexOwner = this,
        ))
    }

    actual fun newIteratorWithBase(baseIterator: RocksIterator): RocksIterator {
        checkOpenHandle()
        check(baseIterator.isOwningHandle()) { "Base iterator is already closed or transferred." }
        val baseOwners = baseIterator.transferWrapperOwnershipToNativeAndDetachOwners()
        val iterator = rocksdb.rocksdb_writebatch_wi_create_iterator_with_base(native, baseIterator.native)
        return borrowIterator(RocksIterator(
            requireNotNull(iterator) {
                "RocksDB returned null write-batch-with-index iterator"
            },
            dbOwner = baseOwners.dbOwner,
            transactionOwner = baseOwners.transactionOwner,
            writeBatchWithIndexOwner = this,
        ))
    }

    actual fun newIteratorWithBase(baseIterator: RocksIterator, readOptions: ReadOptions?): RocksIterator {
        if (readOptions == null) return newIteratorWithBase(baseIterator)
        checkOpenHandle()
        readOptions.checkOwningHandle()
        check(baseIterator.isOwningHandle()) { "Base iterator is already closed or transferred." }
        val baseOwners = baseIterator.transferWrapperOwnershipToNativeAndDetachOwners()
        val iterator = rocksdb.rocksdb_writebatch_wi_create_iterator_with_base_readopts(
            native,
            baseIterator.native,
            readOptions.native,
        )
        return borrowIterator(RocksIterator(
            requireNotNull(iterator) {
                "RocksDB returned null write-batch-with-index iterator"
            },
            dbOwner = baseOwners.dbOwner,
            transactionOwner = baseOwners.transactionOwner,
            writeBatchWithIndexOwner = this,
        ))
    }

    actual fun getFromBatch(columnFamilyHandle: ColumnFamilyHandle, options: DBOptions, key: ByteArray): ByteArray? {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        options.checkOwningHandle()
        var a: ByteArray? = null
        wrapWithErrorThrower { error ->
            memScoped {
                val length = alloc<size_tVar>()
                val value = key.usePointer { keyPointer ->
                    rocksdb.rocksdb_writebatch_wi_get_from_batch_cf(
                        native,
                        options.native,
                        columnFamilyHandle.native,
                        keyPointer,
                        key.size.asSizeT(),
                        length.ptr,
                        error
                    )
                }
                a = value?.let {
                    try {
                        it.toByteArray(length.value)
                    } finally {
                        rocksdb_free(it)
                    }
                }
            }
        }
        return a ?: if (batchOnlyFallbackSafe) findColumnFamilyBatchValueByIterating(columnFamilyHandle, key) else null
    }

    actual fun getFromBatch(options: DBOptions, key: ByteArray): ByteArray? {
        checkOpenHandle()
        options.checkOwningHandle()
        var a: ByteArray? = null
        memScoped {
            wrapWithErrorThrower { error ->
                val length = alloc<size_tVar>()
                val value = key.usePointer { keyPointer ->
                    rocksdb.rocksdb_writebatch_wi_get_from_batch(
                        native,
                        options.native,
                        keyPointer,
                        key.size.asSizeT(),
                        length.ptr,
                        error
                    )
                }
                a = value?.let {
                    try {
                        it.toByteArray(length.value)
                    } finally {
                        rocksdb_free(it)
                    }
                }
            }
        }
        return a ?: if (batchOnlyFallbackSafe) findDefaultBatchValueByIterating(key) else null
    }

    actual fun getFromBatchAndDB(
        db: RocksDB,
        columnFamilyHandle: ColumnFamilyHandle,
        options: ReadOptions,
        key: ByteArray
    ): ByteArray? {
        checkOpenHandle()
        db.checkOwningHandle()
        columnFamilyHandle.checkOwningHandle()
        options.checkOwningHandle()
        return memScoped {
            var result: ByteArray? = null
            wrapWithErrorThrower { error ->
                val length = alloc<size_tVar>()
                val value = key.usePointer { keyPointer ->
                    rocksdb.rocksdb_writebatch_wi_get_from_batch_and_db_cf(
                        native,
                        db.native,
                        options.native,
                        columnFamilyHandle.native,
                        keyPointer,
                        key.size.asSizeT(),
                        length.ptr,
                        error,
                    )
                }
                result = value?.let {
                    try {
                        it.toByteArray(length.value)
                    } finally {
                        rocksdb_free(it)
                    }
                }
            }
            result
        }
    }

    actual fun getFromBatchAndDB(db: RocksDB, options: ReadOptions, key: ByteArray): ByteArray? {
        checkOpenHandle()
        db.checkOwningHandle()
        options.checkOwningHandle()
        return memScoped {
            var result: ByteArray? = null
            wrapWithErrorThrower { error ->
                val length = alloc<size_tVar>()
                val value = key.usePointer { keyPointer ->
                    rocksdb.rocksdb_writebatch_wi_get_from_batch_and_db(
                        native,
                        db.native,
                        options.native,
                        keyPointer,
                        key.size.asSizeT(),
                        length.ptr,
                        error,
                    )
                }
                result = value?.let {
                    try {
                        it.toByteArray(length.value)
                    } finally {
                        rocksdb_free(it)
                    }
                }
            }
            result
        }
    }

    private fun findDefaultBatchValueByIterating(key: ByteArray): ByteArray? {
        val state = WriteBatchWithIndexLookupState(key)
        val ref = StableRef.create(state)
        try {
            rocksdb.rocksdb_writebatch_wi_iterate(
                native,
                ref.asCPointer(),
                writeBatchWithIndexPutCallback,
                writeBatchWithIndexDeleteCallback,
            )
        } finally {
            ref.dispose()
        }
        state.failure?.let { throw it }
        return if (state.found) state.value else null
    }

    private fun findColumnFamilyBatchValueByIterating(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray): ByteArray? {
        if (!supportsBorrowedWriteBatch) return null
        val state = WriteBatchWithIndexColumnFamilyLookupState(key, columnFamilyHandle.getID().toUInt())
        val ref = StableRef.create(state)
        val writeBatch = requireNotNull(rocksdb.rocksdb_writebatch_wi_get_write_batch(native)) {
            "RocksDB returned null write-batch-with-index underlying batch"
        }
        try {
            rocksdb.rocksdb_writebatch_iterate_cf(
                writeBatch,
                ref.asCPointer(),
                writeBatchWithIndexPutColumnFamilyCallback,
                writeBatchWithIndexDeleteColumnFamilyCallback,
                writeBatchWithIndexMergeColumnFamilyCallback,
            )
        } finally {
            ref.dispose()
        }
        state.lookup.failure?.let { throw it }
        return if (state.lookup.found) state.lookup.value else null
    }

    override fun close() {
        if (ownsNative && tryClose()) {
            invalidateBorrowedIterators()
            invalidateBorrowedWBWIIterators()
            rocksdb.rocksdb_writebatch_wi_destroy(native)
            ownedComparator?.closeFromOptions()
            ownedComparator = null
            super.close()
        } else if (!ownsNative && tryCloseBorrowed()) {
            invalidateBorrowedIterators()
            invalidateBorrowedWBWIIterators()
            if (freeBorrowedWrapper) {
                free(native)
            }
            super.close()
        }
    }

    internal fun unregisterBorrowedIterator(iterator: RocksIterator) {
        borrowedIterators.remove(iterator)
    }

    internal fun registerBorrowedIterator(iterator: RocksIterator) {
        borrowedIterators.add(iterator)
    }

    internal fun unregisterBorrowedWBWIIterator(iterator: WBWIRocksIterator) {
        borrowedWBWIIterators.remove(iterator)
    }

    private fun borrowIterator(iterator: RocksIterator): RocksIterator =
        iterator.also(borrowedIterators::add)

    private fun borrowWBWIIterator(iterator: WBWIRocksIterator): WBWIRocksIterator =
        iterator.also(borrowedWBWIIterators::add)

    private fun invalidateBorrowedIterators() {
        if (borrowedIterators.isEmpty()) return
        val iterators = borrowedIterators.toList()
        borrowedIterators.clear()
        iterators.forEach { it.invalidateFromOwner() }
    }

    private fun invalidateBorrowedWBWIIterators() {
        if (borrowedWBWIIterators.isEmpty()) return
        val iterators = borrowedWBWIIterators.toList()
        borrowedWBWIIterators.clear()
        iterators.forEach { it.invalidateFromOwner() }
    }

    override fun singleDelete(key: ByteArray) {
        checkOpenHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_writebatch_wi_singledelete(native, keyPointer, key.size.asSizeT(), error)
            }
        }
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_writebatch_wi_singledelete_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    override fun count(): Int {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_wi_count(native)
    }

    override fun put(key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_writebatch_wi_put(
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

    override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_writebatch_wi_put_cf(
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

    override fun merge(key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_writebatch_wi_merge(
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

    override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb.rocksdb_writebatch_wi_merge_cf(
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

    override fun delete(key: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_writebatch_wi_delete(native, keyPointer, key.size.asSizeT(), error)
            }
        }
    }

    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_writebatch_wi_delete_cf(
                    native,
                    columnFamilyHandle.native,
                    keyPointer,
                    key.size.asSizeT(),
                    error
                )
            }
        }
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        checkOpenHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            usePointers(beginKey, endKey) { beginPointer, endPointer ->
                rocksdb.rocksdb_writebatch_wi_delete_range(
                    native,
                    beginPointer,
                    beginKey.size.asSizeT(),
                    endPointer,
                    endKey.size.asSizeT(),
                    error,
                )
            }
        }
    }

    override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            usePointers(beginKey, endKey) { beginPointer, endPointer ->
                rocksdb.rocksdb_writebatch_wi_delete_range_cf(
                    native,
                    columnFamilyHandle.native,
                    beginPointer,
                    beginKey.size.asSizeT(),
                    endPointer,
                    endKey.size.asSizeT(),
                    error,
                )
            }
        }
    }

    override fun putLogData(blob: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            blob.usePointer { blobPointer ->
                rocksdb.rocksdb_writebatch_wi_put_log_data(native, blobPointer, blob.size.asSizeT(), error)
            }
        }
    }

    override fun clear() {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_clear(native)
        }
        invalidateBorrowedIterators()
        invalidateBorrowedWBWIIterators()
        batchOnlyFallbackSafe = true
    }

    override fun setSavePoint() {
        checkOpenHandle()
        rocksdb.rocksdb_writebatch_wi_set_save_point(native)
    }

    override fun rollbackToSavePoint() {
        checkOpenHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_rollback_to_save_point(native, error)
        }
        invalidateBorrowedIterators()
        invalidateBorrowedWBWIIterators()
    }

    override fun popSavePoint() {
        checkOpenHandle()
        batchOnlyFallbackSafe = false
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_pop_save_point(native, error)
        }
    }

    override fun setMaxBytes(maxBytes: Long) {
        checkOpenHandle()
        rocksdb.rocksdb_writebatch_wi_set_max_bytes(native, maxBytes.asSizeT())
    }

    override fun getWriteBatch(): WriteBatch {
        checkOpenHandle()
        check(supportsBorrowedWriteBatch) {
            "RocksDB transaction write-batch wrappers do not expose a safe borrowed underlying write batch."
        }
        return WriteBatch(
            requireNotNull(rocksdb.rocksdb_writebatch_wi_get_write_batch(native)) {
                "RocksDB returned null borrowed write batch"
            },
            ownsNative = false,
        )
    }

    companion object {
        private fun createWithComparator(
            fallbackIndexComparator: AbstractComparator,
            reservedBytes: Int,
            overwriteKey: Boolean,
        ): CPointer<rocksdb_writebatch_wi_t> {
            val comparatorNative = fallbackIndexComparator.transferOwnershipToOptions()
            try {
                return requireNotNull(
                    rocksdb.rocksdb_writebatch_wi_create_with_params(
                        comparatorNative,
                        reservedBytes.asSizeT(),
                        if (overwriteKey) 1.convert() else 0.convert(),
                        0.asSizeT(),
                        0.asSizeT()
                    )
                ) {
                    "Unable to allocate RocksDB write batch with index"
                }
            } catch (throwable: Throwable) {
                fallbackIndexComparator.closeFromOptions()
                throw throwable
            }
        }
    }
}
