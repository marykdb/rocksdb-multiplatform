@file:OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_sst_file_manager_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.staticCFunction
import maryk.sizeTToInt
import platform.posix.size_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import maryk.asUInt64
import maryk.toCheckedLong
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
    internal val native: CPointer<rocksdb_sst_file_manager_t>,
    private var envRef: Env? = null,
) : RocksObject() {

    actual constructor(env: Env) : this(createSstFileManager(env), env)

    actual fun setMaxAllowedSpaceUsage(maxAllowedSpace: Long) {
        checkOwningHandle()
        rocksdb_sst_file_manager_set_max_allowed_space_usage(native, maxAllowedSpace.asUInt64())
    }

    actual fun setCompactionBufferSize(compactionBufferSize: Long) {
        checkOwningHandle()
        rocksdb_sst_file_manager_set_compaction_buffer_size(native, compactionBufferSize.asUInt64())
    }

    actual fun isMaxAllowedSpaceReached(): Boolean {
        checkOwningHandle()
        return rocksdb_sst_file_manager_is_max_allowed_space_reached(native)
    }

    actual fun isMaxAllowedSpaceReachedIncludingCompactions(): Boolean {
        checkOwningHandle()
        return rocksdb_sst_file_manager_is_max_allowed_space_reached_including_compactions(native)
    }

    actual fun getTrackedFiles(): Map<String, Long> {
        checkOwningHandle()
        val snapshot = TrackedFileSnapshot()
        val state = StableRef.create(snapshot)
        try {
            rocksdb.rocksdb_sst_file_manager_get_tracked_files(native, state.asCPointer(), trackedFileCallback)
            snapshot.failure?.let { throw it }
            return snapshot.files.toMap()
        } finally {
            state.dispose()
        }
    }

    actual fun getTotalSize(): Long {
        checkOwningHandle()
        return rocksdb_sst_file_manager_get_total_size(native).toCheckedLong("SST file manager total size")
    }

    actual fun getDeleteRateBytesPerSecond(): Long {
        checkOwningHandle()
        return rocksdb_sst_file_manager_get_delete_rate_bytes_per_second(native).convert()
    }

    actual fun setDeleteRateBytesPerSecond(deleteRate: Long) {
        checkOwningHandle()
        rocksdb_sst_file_manager_set_delete_rate_bytes_per_second(native, deleteRate)
    }

    actual fun getMaxTrashDBRatio(): Double {
        checkOwningHandle()
        return rocksdb_sst_file_manager_get_max_trash_db_ratio(native)
    }

    actual fun setMaxTrashDBRatio(ratio: Double) {
        checkOwningHandle()
        rocksdb_sst_file_manager_set_max_trash_db_ratio(native, ratio)
    }

    actual override fun close() {
        if (tryClose()) {
            rocksdb_sst_file_manager_destroy(native)
            envRef = null
            super.close()
        }
    }
}

private fun createSstFileManager(env: Env): CPointer<rocksdb_sst_file_manager_t> {
    env.checkOwningHandle()
    return requireNotNull(rocksdb_sst_file_manager_create(env.native)) {
        "Unable to allocate RocksDB SST file manager"
    }
}

private class TrackedFileSnapshot {
    val files = linkedMapOf<String, Long>()
    var failure: Throwable? = null
}

private val trackedFileCallback = staticCFunction {
        state: COpaquePointer?, path: CPointer<ByteVar>?, pathLength: size_t, size: ULong ->
    val snapshot = requireNotNull(state).asStableRef<TrackedFileSnapshot>().get()
    if (snapshot.failure == null) {
        try {
            val name = requireNotNull(path) { "RocksDB returned a null tracked file path" }
                .readBytes(sizeTToInt(pathLength, "tracked file path length")).decodeToString()
            snapshot.files[name] = size.toCheckedLong("tracked SST file size")
        } catch (error: Throwable) {
            snapshot.failure = error
        }
    }
}
