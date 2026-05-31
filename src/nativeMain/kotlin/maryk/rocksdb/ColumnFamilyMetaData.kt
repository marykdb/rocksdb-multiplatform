package maryk.rocksdb

import maryk.toCheckedLong

actual class ColumnFamilyMetaData internal constructor(
    val size: ULong,
    val fileCount: ULong,
    val name: String,
    val levels: List<LevelMetaData>,
) {
    actual fun size(): Long = size.toCheckedLong("column family metadata size")

    actual fun fileCount(): Long = fileCount.toCheckedLong("column family metadata file count")

    actual fun name(): ByteArray = name.encodeToByteArray()

    actual fun levels(): List<LevelMetaData> = levels
}
