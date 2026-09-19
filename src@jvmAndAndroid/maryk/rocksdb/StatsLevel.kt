package maryk.rocksdb

actual typealias StatsLevel = org.rocksdb.StatsLevel

fun getStatsLevel(value: Byte) =
    StatsLevel.getStatsLevel(value)
