package maryk.rocksdb

import rocksdb.RocksDBStatsLevel
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelAll
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelDisableAll
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelExceptDetailedTimers
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelExceptHistogramOrTimers
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelExceptTickers
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelExceptTimeForMutex
import rocksdb.RocksDBStatsLevel.RocksDBStatsLevelExceptTimers

actual enum class StatsLevel(
    internal val value: RocksDBStatsLevel
) {
    DISABLE_ALL(RocksDBStatsLevelDisableAll),
    EXCEPT_TICKERS(RocksDBStatsLevelExceptTickers),
    EXCEPT_HISTOGRAM_OR_TIMERS(RocksDBStatsLevelExceptHistogramOrTimers),
    EXCEPT_TIMERS(RocksDBStatsLevelExceptTimers),
    EXCEPT_DETAILED_TIMERS(RocksDBStatsLevelExceptDetailedTimers),
    EXCEPT_TIME_FOR_MUTEX(RocksDBStatsLevelExceptTimeForMutex),
    ALL(RocksDBStatsLevelAll);
}
