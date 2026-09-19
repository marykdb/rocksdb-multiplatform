package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import maryk.rocksdb.util.createTestDBFolder

class StatisticsTest {
    init {
        loadRocksDBLibrary()
    }

    private fun newDbPath(suffix: String) = createTestDBFolder("StatisticsTest-$suffix")

    @Test
    fun createStatistics() {
        Statistics().use { statistics ->
            statistics.setStatsLevel(StatsLevel.EXCEPT_DETAILED_TIMERS)
        }

        createStatistics(setOf(HistogramType.DB_WRITE, HistogramType.COMPACTION_TIME)).use { statistics ->
            statistics.reset()
        }
    }

    @Test
    fun statsLevel() {
        Statistics().use { statistics ->
            statistics.setStatsLevel(StatsLevel.ALL)
            assertEquals(StatsLevel.ALL, statistics.statsLevel())
        }
    }

    @Test
    fun getTickerCount() {
        val dbPath = newDbPath("getTickerCount")
        Statistics().use { statistics ->
            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    repeat(10) {
                        db.get(key)
                    }

                    assertTrue(
                        statistics.getTickerCount(TickerType.BYTES_READ) > 0,
                        "Ticker count should increase after reads"
                    )
                }
            }
        }
    }

    @Test
    fun getAndResetTickerCount() {
        val dbPath = newDbPath("getAndResetTickerCount")
        Statistics().use { statistics ->
            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    repeat(10) {
                        db.get(key)
                    }

                    val read = statistics.getAndResetTickerCount(TickerType.BYTES_READ)
                    assertTrue(read > 0, "Ticker count should be non-zero before reset")

                    val afterReset = statistics.getTickerCount(TickerType.BYTES_READ)
                    assertTrue(afterReset < read, "Ticker count should drop after reset")
                }
            }
        }
    }

    @Test
    fun getHistogramData() {
        val dbPath = newDbPath("getHistogramData")
        Statistics().use { statistics ->
            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    repeat(10) {
                        db.get(key)
                    }

                    val histogramData = statistics.getHistogramData(HistogramType.BYTES_PER_READ)
                    assertNotNull(histogramData)
                    assertTrue(histogramData.getAverage() > 0)
                    assertTrue(histogramData.getMedian() > 0)
                    assertTrue(histogramData.getPercentile95() > 0)
                    assertTrue(histogramData.getPercentile99() > 0)
                    assertEquals(0.0, histogramData.getStandardDeviation())
                    assertTrue(histogramData.getMax() > 0)
                    assertTrue(histogramData.getCount() > 0)
                    assertTrue(histogramData.getSum() > 0)
                    assertTrue(histogramData.getMin() > 0)
                }
            }
        }
    }

    @Test
    fun getHistogramString() {
        val dbPath = newDbPath("getHistogramString")
        Statistics().use { statistics ->
            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    repeat(10) {
                        db.put(key, value)
                    }

                    val histogramString = statistics.getHistogramString(HistogramType.BYTES_PER_WRITE)
                    assertTrue(histogramString.isNotEmpty(), "Histogram string should not be empty")
                }
            }
        }
    }

    @Test
    fun reset() {
        val dbPath = newDbPath("reset")
        Statistics().use { statistics ->
            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    repeat(10) {
                        db.get(key)
                    }

                    val read = statistics.getTickerCount(TickerType.BYTES_READ)
                    assertTrue(read > 0)

                    statistics.reset()

                    val afterReset = statistics.getTickerCount(TickerType.BYTES_READ)
                    assertTrue(afterReset < read)
                }
            }
        }
    }

    @Test
    fun statisticsToString() {
        val dbPath = newDbPath("toString")
        Statistics().use { statistics ->
            Options().setCreateIfMissing(true).use { options ->
                options.setStatistics(statistics)

                openRocksDB(options, dbPath).use { _ ->
                    assertNotEquals("", statistics.toString())
                }
            }
        }
    }
}
