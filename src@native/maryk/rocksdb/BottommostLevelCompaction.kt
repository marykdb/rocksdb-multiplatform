package maryk.rocksdb

import maryk.rocksdb.BottommostLevelCompaction.kForce
import maryk.rocksdb.BottommostLevelCompaction.kForceOptimized
import maryk.rocksdb.BottommostLevelCompaction.kIfHaveCompactionFilter
import maryk.rocksdb.BottommostLevelCompaction.kSkip

actual enum class BottommostLevelCompaction(
    internal val value: UByte
) {
    kSkip(0u),
    kIfHaveCompactionFilter(1u),
    kForce(2u),
    kForceOptimized(3u)
}

fun bottommostLevelCompactionFromByte(bottommostLevelCompaction: UByte): BottommostLevelCompaction? {
    return when (bottommostLevelCompaction.toUInt()) {
        0u -> kSkip
        1u -> kIfHaveCompactionFilter
        2u -> kForce
        3u -> kForceOptimized
        else -> null
    }
}
