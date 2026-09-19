@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.sizeTToLong
import maryk.toBoolean
import maryk.toByteArray
import maryk.usePointer
import maryk.usePointers
import maryk.wrapWithErrorThrower
import platform.posix.size_tVar
import rocksdb.rocksdb_writebatch_clear
import rocksdb.rocksdb_writebatch_count
import rocksdb.rocksdb_writebatch_create
import rocksdb.rocksdb_writebatch_delete
import rocksdb.rocksdb_writebatch_delete_cf
import rocksdb.rocksdb_writebatch_delete_range
import rocksdb.rocksdb_writebatch_delete_range_cf
import rocksdb.rocksdb_writebatch_destroy
import rocksdb.rocksdb_writebatch_merge
import rocksdb.rocksdb_writebatch_merge_cf
import rocksdb.rocksdb_writebatch_pop_save_point
import rocksdb.rocksdb_writebatch_put
import rocksdb.rocksdb_writebatch_put_cf
import rocksdb.rocksdb_writebatch_put_log_data
import rocksdb.rocksdb_writebatch_rollback_to_save_point
import rocksdb.rocksdb_writebatch_set_save_point

actual class WriteBatch(
    internal val native: CPointer<rocksdb_writebatch_t>,
    private val ownsNative: Boolean = true,
) : AbstractWriteBatch() {
    init {
        if (!ownsNative) {
            borrowHandle()
        }
    }

    actual constructor() : this(requireNotNull(rocksdb_writebatch_create()) { "Unable to allocate RocksDB write batch" })

    actual fun getDataSize(): Long {
        checkOpenHandle()
        return sizeTToLong(rocksdb.rocksdb_writebatch_get_data_size(native), "write batch data size")
    }

    actual fun getWalTerminationPoint(): WriteBatchSavePoint {
        checkOpenHandle()
        val terminationPoint = requireNotNull(rocksdb.rocksdb_writebatch_get_wal_termination_point(native)) {
            "RocksDB returned null write batch WAL termination point"
        }
        try {
            return WriteBatchSavePoint(
                size = sizeTToLong(rocksdb.rocksdb_save_point_get_size(terminationPoint), "write batch WAL termination size"),
                count = rocksdb.rocksdb_save_point_get_count(terminationPoint).toLong(),
                contentFlags = rocksdb.rocksdb_save_point_get_content_flags(terminationPoint).toLong(),
            )
        } finally {
            rocksdb.rocksdb_save_point_destroy(terminationPoint)
        }
    }

    actual fun data(): ByteArray {
        checkOpenHandle()
        memScoped {
            val length = alloc<size_tVar>()
            val data = rocksdb.rocksdb_writebatch_data(native, length.ptr)
            if (length.value == 0.asSizeT()) {
                return ByteArray(0)
            }
            return requireNotNull(data) {
                "RocksDB returned null write batch data for ${length.value} bytes."
            }.toByteArray(length.value)
        }
    }

    override fun close() {
        if (ownsNative && tryClose()) {
            rocksdb_writebatch_destroy(native)
            super.close()
        } else if (!ownsNative && tryCloseBorrowed()) {
            super.close()
        }
    }

    override fun singleDelete(key: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_writebatch_singledelete(native, keyPointer, key.size.asSizeT(), error)
            }
        }
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb.rocksdb_writebatch_singledelete_cf(native, columnFamilyHandle.native, keyPointer, key.size.asSizeT(), error)
            }
        }
    }

    override fun count(): Int {
        checkOpenHandle()
        return rocksdb_writebatch_count(native)
    }

    override fun put(key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_writebatch_put(native, keyPointer, key.size.asSizeT(), valuePointer, value.size.asSizeT(), error)
            }
        }
    }

    override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_writebatch_put_cf(native, columnFamilyHandle.native, keyPointer, key.size.asSizeT(), valuePointer, value.size.asSizeT(), error)
            }
        }
    }

    override fun merge(key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_writebatch_merge(native, keyPointer, key.size.asSizeT(), valuePointer, value.size.asSizeT(), error)
            }
        }
    }

    override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(key, value) { keyPointer, valuePointer ->
                rocksdb_writebatch_merge_cf(native, columnFamilyHandle.native, keyPointer, key.size.asSizeT(), valuePointer, value.size.asSizeT(), error)
            }
        }
    }

    override fun delete(key: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb_writebatch_delete(native, keyPointer, key.size.asSizeT(), error)
            }
        }
    }

    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            key.usePointer { keyPointer ->
                rocksdb_writebatch_delete_cf(native, columnFamilyHandle.native, keyPointer, key.size.asSizeT(), error)
            }
        }
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            usePointers(beginKey, endKey) { beginPointer, endPointer ->
                rocksdb_writebatch_delete_range(native, beginPointer, beginKey.size.asSizeT(), endPointer, endKey.size.asSizeT(), error)
            }
        }
    }

    override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        checkOpenHandle()
        columnFamilyHandle.checkOwningHandle()
        wrapWithErrorThrower { error ->
            usePointers(beginKey, endKey) { beginPointer, endPointer ->
                rocksdb_writebatch_delete_range_cf(native, columnFamilyHandle.native, beginPointer, beginKey.size.asSizeT(), endPointer, endKey.size.asSizeT(), error)
            }
        }
    }

    override fun putLogData(blob: ByteArray) {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            blob.usePointer { blobPointer ->
                rocksdb_writebatch_put_log_data(native, blobPointer, blob.size.asSizeT(), error)
            }
        }
    }

    override fun clear() {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_clear(native)
        }
    }

    override fun setSavePoint() {
        checkOpenHandle()
        rocksdb_writebatch_set_save_point(native)
    }

    override fun rollbackToSavePoint() {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_rollback_to_save_point(native, error)
        }
    }

    override fun popSavePoint() {
        checkOpenHandle()
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_pop_save_point(native, error)
        }
    }

    override fun setMaxBytes(maxBytes: Long) {
        checkOpenHandle()
        rocksdb.rocksdb_writebatch_set_max_bytes(native, maxBytes.asSizeT())
    }

    actual fun hasPut(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_put(native).toBoolean()
    }

    actual fun hasDelete(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_delete(native).toBoolean()
    }

    actual fun hasSingleDelete(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_single_delete(native).toBoolean()
    }

    actual fun hasDeleteRange(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_delete_range(native).toBoolean()
    }

    actual fun hasMerge(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_merge(native).toBoolean()
    }

    actual fun hasBeginPrepare(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_begin_prepare(native).toBoolean()
    }

    actual fun hasEndPrepare(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_end_prepare(native).toBoolean()
    }

    actual fun hasCommit(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_commit(native).toBoolean()
    }

    actual fun hasRollback(): Boolean {
        checkOpenHandle()
        return rocksdb.rocksdb_writebatch_has_rollback(native).toBoolean()
    }

    actual fun markWalTerminationPoint() {
        checkOpenHandle()
        rocksdb.rocksdb_writebatch_mark_wal_termination_point(native)
    }

    override fun getWriteBatch(): WriteBatch {
        checkOpenHandle()
        return this
    }
}
