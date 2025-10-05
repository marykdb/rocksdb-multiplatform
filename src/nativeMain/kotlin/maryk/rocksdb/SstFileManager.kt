@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_sst_file_manager_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.toBoolean
import rocksdb.rocksdb_sst_file_manager_create
import rocksdb.rocksdb_sst_file_manager_destroy
import rocksdb.rocksdb_sst_file_manager_get_delete_rate_bytes_per_second
import rocksdb.rocksdb_sst_file_manager_get_max_trash_db_ratio
import rocksdb.rocksdb_sst_file_manager_get_total_size
import rocksdb.rocksdb_sst_file_manager_is_max_allowed_space_reached
import rocksdb.rocksdb_sst_file_manager_is_max_allowed_space_reached_including_compactions
import rocksdb.rocksdb_sst_file_manager_set_compaction_buffer_size
import rocksdb.rocksdb_sst_file_manager_set_delete_rate_bytes_per_second
import rocksdb.rocksdb_sst_file_manager_set_max_allowed_space_usage
import rocksdb.rocksdb_sst_file_manager_set_max_trash_db_ratio

actual class SstFileManager internal constructor(
    internal val native: CPointer<rocksdb_sst_file_manager_t>
) : RocksObject() {

    actual constructor(env: Env) : this(rocksdb_sst_file_manager_create(env.native)!!)

    actual fun setMaxAllowedSpaceUsage(maxAllowedSpace: Long) {
        rocksdb_sst_file_manager_set_max_allowed_space_usage(native, maxAllowedSpace.toULong())
    }

    actual fun setCompactionBufferSize(compactionBufferSize: Long) {
        rocksdb_sst_file_manager_set_compaction_buffer_size(native, compactionBufferSize.toULong())
    }

    actual fun isMaxAllowedSpaceReached(): Boolean =
        rocksdb_sst_file_manager_is_max_allowed_space_reached(native)

    actual fun isMaxAllowedSpaceReachedIncludingCompactions(): Boolean =
        rocksdb_sst_file_manager_is_max_allowed_space_reached_including_compactions(native)

    actual fun getTotalSize(): Long = rocksdb_sst_file_manager_get_total_size(native).toLong()

    actual fun getDeleteRateBytesPerSecond(): Long =
        rocksdb_sst_file_manager_get_delete_rate_bytes_per_second(native).toLong()

    actual fun setDeleteRateBytesPerSecond(deleteRate: Long) {
        rocksdb_sst_file_manager_set_delete_rate_bytes_per_second(native, deleteRate)
    }

    actual fun getMaxTrashDBRatio(): Double =
        rocksdb_sst_file_manager_get_max_trash_db_ratio(native)

    actual fun setMaxTrashDBRatio(ratio: Double) {
        rocksdb_sst_file_manager_set_max_trash_db_ratio(native, ratio)
    }

    actual override fun close() {
        if (isOwningHandle()) {
            rocksdb_sst_file_manager_destroy(native)
            super.close()
        }
    }
}
