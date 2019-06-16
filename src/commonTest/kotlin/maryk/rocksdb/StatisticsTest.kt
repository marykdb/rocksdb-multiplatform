package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StatisticsTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("StatisticsTest")

    @Test
    fun statsLevel() {
        val statistics = Statistics()
        statistics.setStatsLevel(StatsLevel.ALL)
        assertEquals(StatsLevel.ALL, statistics.statsLevel())
    }

    @Test
    fun getTickerCount() {
        Statistics().use { statistics ->
            Options().apply {
                setStatistics(statistics)
                setCreateIfMissing(true)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    for (i in 0..9) {
                        db[key]
                    }

                    assertTrue(0 < statistics.getTickerCount(TickerType.BYTES_READ))
                }
            }
        }
    }

    @Test
    fun getAndResetTickerCount() {
        Statistics().use { statistics ->
            Options().apply {
                setStatistics(statistics)
                setCreateIfMissing(true)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    for (i in 0..9) {
                        db[key]
                    }

                    val read = statistics.getAndResetTickerCount(TickerType.BYTES_READ)
                    assertTrue(0 < read)

                    val readAfterReset = statistics.getTickerCount(TickerType.BYTES_READ)
                    assertTrue(read > readAfterReset)
                }
            }
        }
    }

    @Test
    fun getHistogramData() {
        Statistics().use { statistics ->
            Options().apply {
                setStatistics(statistics)
                setCreateIfMissing(true)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use { db ->
                    val key = "some-key".encodeToByteArray()
                    val value = "some-value".encodeToByteArray()

                    db.put(key, value)
                    for (i in 0..9) {
                        db[key]
                    }

                    val histogramData = statistics.getHistogramData(HistogramType.BYTES_PER_READ)
                    assertNotNull(histogramData)
                    assertTrue(0.0 < histogramData.getAverage())
                    assertTrue(0.0 < histogramData.getMedian())
                    assertTrue(0.0 < histogramData.getPercentile95())
                    assertTrue(0.0 < histogramData.getPercentile99())
                    assertEquals(0.00, histogramData.getStandardDeviation())
                    assertTrue(0.0 < histogramData.getMax())
                    assertTrue(0 < histogramData.getCount())
                    assertTrue(0 < histogramData.getSum())
                    assertTrue(0.0 < histogramData.getMin())
                }
            }
        }
    }

    @Test
    fun getHistogramString() {
        Statistics().use { statistics ->
            Options()
                .setStatistics(statistics)
                .setCreateIfMissing(true).use { opt ->
                    openRocksDB(
                        opt,
                        createTestFolder()
                    ).use { db ->

                        val key = "some-key".encodeToByteArray()
                        val value = "some-value".encodeToByteArray()

                        for (i in 0..9) {
                            db.put(key, value)
                        }

                        assertNotNull(statistics.getHistogramString(HistogramType.BYTES_PER_WRITE))
                    }
                }
        }
    }

    @Test
    fun reset() {
        Statistics().use { statistics ->
            Options()
                .setStatistics(statistics)
                .setCreateIfMissing(true).use { opt ->
                    openRocksDB(
                        opt,
                        createTestFolder()
                    ).use { db ->

                        val key = "some-key".encodeToByteArray()
                        val value = "some-value".encodeToByteArray()

                        db.put(key, value)
                        for (i in 0..9) {
                            db[key]
                        }

                        val read = statistics.getTickerCount(TickerType.BYTES_READ)
                        assertTrue(0 < read)

                        statistics.reset()

                        val readAfterReset = statistics.getTickerCount(TickerType.BYTES_READ)
                        assertTrue(read > readAfterReset)
                    }
                }
        }
    }

    @Test
    fun toStringNotNull() {
        Statistics().use { statistics ->
            Options().apply {
                setStatistics(statistics)
                setCreateIfMissing(true)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use {
                    assertNotNull(statistics.toString())
                }
            }
        }
    }
}
