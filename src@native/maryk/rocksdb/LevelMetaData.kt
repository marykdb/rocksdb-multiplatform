@file:Suppress("unused")

package maryk.rocksdb

import maryk.toCheckedLong

actual class LevelMetaData(
    val level: Int,
    val size: ULong,
    val files: List<SstFileMetaData>,
) {
    actual fun level() = level

    actual fun size() = size.toCheckedLong("level metadata size")

    actual fun files() = files
}
