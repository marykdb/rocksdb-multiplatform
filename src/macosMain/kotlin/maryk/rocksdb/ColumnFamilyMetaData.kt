package maryk.rocksdb

import rocksdb.RocksDBColumnFamilyMetaData
import rocksdb.RocksDBLevelFileMetaData

actual class ColumnFamilyMetaData(
    val native: RocksDBColumnFamilyMetaData
) {
    actual fun size(): Long {
        return native.size.toLong()
    }

    actual fun fileCount(): Long {
        return native.fileCount.toLong()
    }

    actual fun name(): ByteArray {
        return native.name.encodeToByteArray()
    }

    actual fun levels(): List<LevelMetaData> {
        @Suppress("UNCHECKED_CAST")
        val levels = native.levels as List<RocksDBLevelFileMetaData>
        return levels.map { LevelMetaData(it) }
    }
}
