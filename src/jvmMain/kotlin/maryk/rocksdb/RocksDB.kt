package maryk.rocksdb

actual typealias RocksDB = org.rocksdb.RocksDB

actual val defaultColumnFamily = RocksDB.DEFAULT_COLUMN_FAMILY
actual val rocksDBNotFound = RocksDB.NOT_FOUND

actual fun destroyRocksDB(path: String, options: Options) {
    RocksDB.destroyDB(path, options)
}

actual fun listColumnFamilies(
    options: Options,
    path: String
) = RocksDB.listColumnFamilies(options, path)
