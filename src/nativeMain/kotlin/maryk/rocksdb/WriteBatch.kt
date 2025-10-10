@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.toBoolean
import maryk.toByteArray
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
    internal val native: CPointer<rocksdb_writebatch_t>
) : AbstractWriteBatch() {
    actual constructor() : this(rocksdb_writebatch_create()!!)

    actual fun getDataSize(): Long = rocksdb.rocksdb_writebatch_get_data_size(native).toLong()

    actual fun getWalTerminationPoint(): WriteBatchSavePoint {
        val terminationPoint = rocksdb.rocksdb_writebatch_get_wal_termination_point(native)

        return WriteBatchSavePoint(
            size = rocksdb.rocksdb_save_point_get_size(terminationPoint).toLong(),
            count = rocksdb.rocksdb_save_point_get_count(terminationPoint).toLong(),
            contentFlags = rocksdb.rocksdb_save_point_get_content_flags(terminationPoint).toLong(),
        ).also {
            rocksdb.rocksdb_save_point_destroy(terminationPoint)
        }
    }

    actual fun data(): ByteArray {
        memScoped {
            val length = alloc<size_tVar>()
            return rocksdb.rocksdb_writebatch_data(native, length.ptr)!!.toByteArray(length.value)
        }
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_writebatch_destroy(native)
            super.close()
        }
    }

    override fun singleDelete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_singledelete(native, key.toCValues(), key.size.asSizeT(), error)
        }
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_singledelete_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.asSizeT(), error)
        }
    }

    override fun count(): Int =
        rocksdb_writebatch_count(native)

    override fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_put(native, key.toCValues(), key.size.asSizeT(), value.toCValues(), value.size.asSizeT(), error)
        }
    }

    override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_put_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.asSizeT(), value.toCValues(), value.size.asSizeT(), error)
        }
    }

    override fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_merge(native, key.toCValues(), key.size.asSizeT(), value.toCValues(), value.size.asSizeT(), error)
        }
    }

    override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_merge_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.asSizeT(), value.toCValues(), value.size.asSizeT(), error)
        }
    }

    override fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete(native, key.toCValues(), key.size.asSizeT(), error)
        }
    }

    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.asSizeT(), error)
        }
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete_range(native, beginKey.toCValues(), beginKey.size.asSizeT(), endKey.toCValues(), endKey.size.asSizeT(), error)
        }
    }

    override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete_range_cf(native, columnFamilyHandle.native, beginKey.toCValues(), beginKey.size.asSizeT(), endKey.toCValues(), endKey.size.asSizeT(), error)
        }
    }

    override fun putLogData(blob: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_put_log_data(native, blob.toCValues(), blob.size.asSizeT(), error)
        }
    }

    override fun clear() {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_clear(native)
        }
    }

    override fun setSavePoint() {
        rocksdb_writebatch_set_save_point(native)
    }

    override fun rollbackToSavePoint() {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_rollback_to_save_point(native, error)
        }
    }

    override fun popSavePoint() {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_pop_save_point(native, error)
        }
    }

    override fun setMaxBytes(maxBytes: Long) {
        rocksdb.rocksdb_writebatch_set_max_bytes(native, maxBytes.asSizeT())
    }

    actual fun hasPut(): Boolean = rocksdb.rocksdb_writebatch_has_put(native).toBoolean()

    actual fun hasDelete(): Boolean = rocksdb.rocksdb_writebatch_has_delete(native).toBoolean()

    actual fun hasSingleDelete(): Boolean = rocksdb.rocksdb_writebatch_has_single_delete(native).toBoolean()

    actual fun hasDeleteRange(): Boolean = rocksdb.rocksdb_writebatch_has_delete_range(native).toBoolean()

    actual fun hasMerge(): Boolean = rocksdb.rocksdb_writebatch_has_merge(native).toBoolean()

    actual fun hasBeginPrepare(): Boolean = rocksdb.rocksdb_writebatch_has_begin_prepare(native).toBoolean()

    actual fun hasEndPrepare(): Boolean = rocksdb.rocksdb_writebatch_has_end_prepare(native).toBoolean()

    actual fun hasCommit(): Boolean = rocksdb.rocksdb_writebatch_has_commit(native).toBoolean()

    actual fun hasRollback(): Boolean = rocksdb.rocksdb_writebatch_has_rollback(native).toBoolean()

    actual fun markWalTerminationPoint() {
        rocksdb.rocksdb_writebatch_mark_wal_termination_point(native)
    }

    override fun getWriteBatch(): WriteBatch = this
}
