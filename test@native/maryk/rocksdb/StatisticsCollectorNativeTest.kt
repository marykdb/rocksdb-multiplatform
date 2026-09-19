package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import maryk.rocksdb.util.sleepMillis

class StatisticsCollectorNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun statisticsCollectorStartShutdownCanRepeatRapidly() {
        val dbPath = createTestDBFolder("StatisticsCollectorNativeTest")
        Statistics().use { statistics ->
            statistics.setStatsLevel(StatsLevel.ALL)

            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { db ->
                    val tickerCallbackCount = ThreadSafeCounter()
                    val histogramCallbackCount = ThreadSafeCounter()

                    val callback = object : StatisticsCollectorCallback {
                        override fun tickerCallback(tickerType: TickerType, tickerCount: Long) {
                            tickerCallbackCount.increment()
                        }

                        override fun histogramCallback(
                            histogramType: HistogramType,
                            histogramData: HistogramData
                        ) {
                            histogramCallbackCount.increment()
                        }
                    }

                    val collector = StatisticsCollector(
                        listOf(StatsCollectorInput(statistics, callback)),
                        0
                    )

                    repeat(50) { index ->
                        collector.start()
                        collector.start()

                        val key = "stats-race-key-$index".encodeToByteArray()
                        val value = ByteArray(16) { index.toByte() }
                        db.put(key, value)
                        db.get(key)

                        collector.shutDown(1_000)
                        collector.shutDown(0)
                    }

                    collector.start()
                    repeat(10) { index ->
                        val key = "stats-race-final-key-$index".encodeToByteArray()
                        val value = ByteArray(16) { (index + 1).toByte() }
                        db.put(key, value)
                        db.get(key)
                    }

                    val timeout = 5.seconds
                    val timer = TimeSource.Monotonic.markNow()
                    while ((tickerCallbackCount.value() == 0 || histogramCallbackCount.value() == 0) &&
                        timer.elapsedNow() < timeout
                    ) {
                        sleepMillis(50)
                    }

                    collector.shutDown(1_000)

                    assertTrue(tickerCallbackCount.value() > 0, "Ticker callbacks should survive repeated start/shutdown")
                    assertTrue(histogramCallbackCount.value() > 0, "Histogram callbacks should survive repeated start/shutdown")
                }
            }
        }
    }
}
