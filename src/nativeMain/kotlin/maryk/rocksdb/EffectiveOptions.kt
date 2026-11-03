@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package maryk.rocksdb

import kotlinx.cinterop.toKString
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_free
import rocksdb.rocksdb_get_mutable_cf_options_string
import rocksdb.rocksdb_get_mutable_db_options_string

actual fun RocksDB.effectiveDBOptions(): Map<String, String> = withLifecycleLock {
    checkOwningHandle()
    val source = Unit.wrapWithNullErrorThrower { error -> rocksdb_get_mutable_db_options_string(native, error) }
        ?: error("RocksDB returned a null DB options snapshot")
    try {
        effectiveOptionSnapshot(source.toKString(), effectiveDBOptionKeys)
    } finally {
        rocksdb_free(source)
    }
}

actual fun RocksDB.effectiveColumnFamilyOptions(columnFamilyHandle: ColumnFamilyHandle?): Map<String, String> = withLifecycleLock {
    checkOwningHandle()
    columnFamilyHandle?.checkOwningHandle()
    val source = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_get_mutable_cf_options_string(native, columnFamilyHandle?.native, error)
    } ?: error("RocksDB returned a null column family options snapshot")
    try {
        effectiveOptionSnapshot(source.toKString(), effectiveColumnFamilyOptionKeys)
    } finally {
        rocksdb_free(source)
    }
}
