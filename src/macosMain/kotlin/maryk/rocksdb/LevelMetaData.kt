package maryk.rocksdb

import rocksdb.RocksDBLevelFileMetaData
import rocksdb.RocksDBSstFileMetaData

actual class LevelMetaData(
    val native: RocksDBLevelFileMetaData
) {
    actual fun level(): Int {
        return native.level
    }

    actual fun size(): Long {
        return native.size.toLong()
    }

    actual fun files(): List<SstFileMetaData> {
        @Suppress("UNCHECKED_CAST")
        return (native.files as List<RocksDBSstFileMetaData>).map { sstFileMetaData ->
            SstFileMetaData(sstFileMetaData)
        }
    }
}
