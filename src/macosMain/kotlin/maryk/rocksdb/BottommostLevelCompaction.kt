package maryk.rocksdb

import maryk.rocksdb.BottommostLevelCompaction.kForce
import maryk.rocksdb.BottommostLevelCompaction.kIfHaveCompactionFilter
import maryk.rocksdb.BottommostLevelCompaction.kSkip

private const val VALUE_kSkip: Byte = 0
private const val VALUE_kIfHaveCompactionFilter: Byte = 1
private const val VALUE_kForce: Byte = 2

actual enum class BottommostLevelCompaction(
    private val value: Byte
) {
    kSkip(VALUE_kSkip),
    kIfHaveCompactionFilter(VALUE_kIfHaveCompactionFilter),
    kForce(VALUE_kForce)
}

fun fromRocksId(bottommostLevelCompaction: Byte): BottommostLevelCompaction? {
    return when (bottommostLevelCompaction) {
        VALUE_kSkip -> kSkip
        VALUE_kIfHaveCompactionFilter -> kIfHaveCompactionFilter
        VALUE_kForce -> kForce
        else -> null
    }
}
