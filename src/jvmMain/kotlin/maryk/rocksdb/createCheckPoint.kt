package maryk.rocksdb

actual typealias Checkpoint = org.rocksdb.Checkpoint

actual fun createCheckpoint(db: RocksDB) = Checkpoint.create(db)
