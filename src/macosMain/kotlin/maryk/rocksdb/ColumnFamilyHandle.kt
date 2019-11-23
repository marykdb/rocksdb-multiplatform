package maryk.rocksdb

import rocksdb.RocksDBColumnFamilyHandle

actual class ColumnFamilyHandle
    internal constructor(
        val native: RocksDBColumnFamilyHandle
    )
: RocksObject() {
    actual fun getName() =
        native.name?.encodeToByteArray()
            ?: throw RocksDBException("Missing Column Family Name")

    actual fun getID() = native.id.toInt()
}
