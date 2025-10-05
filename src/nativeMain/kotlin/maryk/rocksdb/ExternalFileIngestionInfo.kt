@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_externalfileingestioninfo_t
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.size_tVar
import kotlinx.cinterop.ByteVar
import rocksdb.rocksdb_externalfileingestioninfo_cf_name
import rocksdb.rocksdb_externalfileingestioninfo_external_file_path
import rocksdb.rocksdb_externalfileingestioninfo_global_seqno
import rocksdb.rocksdb_externalfileingestioninfo_internal_file_path
import rocksdb.rocksdb_externalfileingestioninfo_table_properties

actual class ExternalFileIngestionInfo internal constructor(
    internal val native: CPointer<rocksdb_externalfileingestioninfo_t>
) {
    actual fun columnFamilyName(): String = memScoped {
        val length = alloc<size_tVar>()
        rocksdb_externalfileingestioninfo_cf_name(native, length.ptr)
            ?.toByteArray(length.value)
            ?.decodeToString() ?: ""
    }

    actual fun externalFilePath(): String = memScoped {
        val length = alloc<size_tVar>()
        rocksdb_externalfileingestioninfo_external_file_path(native, length.ptr)
            ?.toByteArray(length.value)
            ?.decodeToString() ?: ""
    }

    actual fun internalFilePath(): String = memScoped {
        val length = alloc<size_tVar>()
        rocksdb_externalfileingestioninfo_internal_file_path(native, length.ptr)
            ?.toByteArray(length.value)
            ?.decodeToString() ?: ""
    }

    actual fun globalSequenceNumber(): Long =
        rocksdb_externalfileingestioninfo_global_seqno(native).toLong()

    actual fun tableProperties(): TableProperties? =
        rocksdb_externalfileingestioninfo_table_properties(native)?.let(::TableProperties)
}
