package maryk.rocksdb

actual typealias StatsLevel = org.rocksdb.StatsLevel

actual fun getStatsLevel(value: Byte) =
    StatsLevel.getStatsLevel(value)
