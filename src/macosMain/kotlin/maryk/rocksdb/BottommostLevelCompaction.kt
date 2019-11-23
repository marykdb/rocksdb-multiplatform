package maryk.rocksdb

import maryk.rocksdb.BottommostLevelCompaction.kForce
import maryk.rocksdb.BottommostLevelCompaction.kIfHaveCompactionFilter
import maryk.rocksdb.BottommostLevelCompaction.kSkip
import rocksdb.RocksDBBottommostLevelCompaction
import rocksdb.RocksDBBottommostLevelCompaction.RocksDBBottommostLevelCompactionForce
import rocksdb.RocksDBBottommostLevelCompaction.RocksDBBottommostLevelCompactionIfHaveCompactionFilter
import rocksdb.RocksDBBottommostLevelCompaction.RocksDBBottommostLevelCompactionSkip

actual enum class BottommostLevelCompaction(
    internal val value: RocksDBBottommostLevelCompaction
) {
    kSkip(RocksDBBottommostLevelCompactionSkip),
    kIfHaveCompactionFilter(RocksDBBottommostLevelCompactionIfHaveCompactionFilter),
    kForce(RocksDBBottommostLevelCompactionForce),
}

fun bottommostLevelCompactionFromByte(bottommostLevelCompaction: RocksDBBottommostLevelCompaction): BottommostLevelCompaction? {
    return when (bottommostLevelCompaction) {
        RocksDBBottommostLevelCompactionSkip -> kSkip
        RocksDBBottommostLevelCompactionIfHaveCompactionFilter -> kIfHaveCompactionFilter
        RocksDBBottommostLevelCompactionForce -> kForce
        else -> null
    }
}
