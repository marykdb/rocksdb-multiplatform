@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_externalfileingestioninfo_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import maryk.toCheckedLong
import platform.posix.size_tVar
import rocksdb.rocksdb_externalfileingestioninfo_cf_name
import rocksdb.rocksdb_externalfileingestioninfo_external_file_path
import rocksdb.rocksdb_externalfileingestioninfo_global_seqno
import rocksdb.rocksdb_externalfileingestioninfo_internal_file_path
import rocksdb.rocksdb_externalfileingestioninfo_table_properties

actual class ExternalFileIngestionInfo internal constructor(
    private val columnFamilyNameValue: String,
    private val externalFilePathValue: String,
    private val internalFilePathValue: String,
    private val globalSequenceNumberValue: Long,
    private val tablePropertiesValue: TableProperties?,
) {
    internal constructor(native: CPointer<rocksdb_externalfileingestioninfo_t>) : this(
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            requireNotNull(rocksdb_externalfileingestioninfo_cf_name(native, length.ptr)) {
                "RocksDB returned null external-file ingestion column family name"
            }.toByteArray(length.value).decodeToString()
        },
        externalFilePathValue = memScoped {
            val length = alloc<size_tVar>()
            requireNotNull(rocksdb_externalfileingestioninfo_external_file_path(native, length.ptr)) {
                "RocksDB returned null external-file ingestion external path"
            }.toByteArray(length.value).decodeToString()
        },
        internalFilePathValue = memScoped {
            val length = alloc<size_tVar>()
            requireNotNull(rocksdb_externalfileingestioninfo_internal_file_path(native, length.ptr)) {
                "RocksDB returned null external-file ingestion internal path"
            }.toByteArray(length.value).decodeToString()
        },
        globalSequenceNumberValue = rocksdb_externalfileingestioninfo_global_seqno(native)
            .toCheckedLong("external-file ingestion global sequence number"),
        tablePropertiesValue = rocksdb_externalfileingestioninfo_table_properties(native)?.let(::TableProperties),
    )

    actual fun columnFamilyName(): String = columnFamilyNameValue

    actual fun externalFilePath(): String = externalFilePathValue

    actual fun internalFilePath(): String = internalFilePathValue

    actual fun globalSequenceNumber(): Long =
        globalSequenceNumberValue

    actual fun tableProperties(): TableProperties? =
        tablePropertiesValue
}
