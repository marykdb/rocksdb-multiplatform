package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toBoolean
import maryk.toByteArray
import platform.posix.uint64_tVar
import rocksdb.rocksdb_writebatch_create

actual class WriteBatch(
    internal val native: CPointer<rocksdb_writebatch_t>
) : AbstractWriteBatch(native) {
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
            val length = alloc<uint64_tVar>()
            return rocksdb.rocksdb_writebatch_data(native, length.ptr)!!.toByteArray(length.value)
        }
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

    override fun getWriteBatch(): WriteBatch {
        return this
    }
}
