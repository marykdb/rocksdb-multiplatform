package maryk.rocksdb

import rocksdb.RocksDBColumnFamilyHandle
import maryk.toByteArray

actual class ColumnFamilyHandle
    internal constructor(
        val native: RocksDBColumnFamilyHandle
    )
: RocksObject() {
    actual fun getName() =
        native.name?.toByteArray()
            ?: throw RocksDBException("Missing Column Family Name")

    actual fun getID() = native.id.toInt()
}
