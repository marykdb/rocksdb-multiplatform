package maryk.rocksdb

actual class ColumnFamilyMetaData internal constructor(
    val size: ULong,
    val fileCount: ULong,
    val name: String,
    val levels: List<LevelMetaData>,
) {
    actual fun size(): Long = size.toLong()

    actual fun fileCount(): Long = fileCount.toLong()

    actual fun name(): ByteArray = name.encodeToByteArray()

    actual fun levels(): List<LevelMetaData> = levels
}
