@file:Suppress("unused")

package maryk.rocksdb

actual class LevelMetaData(
    val level: Int,
    val size: ULong,
    val files: List<SstFileMetaData>,
) {
    actual fun level() = level

    actual fun size() = size.toLong()

    actual fun files() = files
}
