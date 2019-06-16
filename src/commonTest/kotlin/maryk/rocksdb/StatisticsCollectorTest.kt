package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import maryk.sleep
import kotlin.test.Test
import kotlin.test.assertTrue

class StatisticsCollectorTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("StatisticsCollectorTest")

    @Test
    fun statisticsCollector() {
        Statistics().use { statistics ->
            Options().apply {
                setStatistics(statistics)
                setCreateIfMissing(true)
            }.use { opt ->
                openRocksDB(
                    opt,
                    createTestFolder()
                ).use {
                    opt.statistics().use { stats ->
                        val callback = StatsCallbackMock()
                        val statsInput = StatsCollectorInput(stats, callback)

                        val statsCollector = StatisticsCollector(
                            listOf(statsInput), 100
                        )
                        statsCollector.start()

                        sleep(1000)

                        assertTrue(0 < callback.tickerCallbackCount)
                        assertTrue(0 < callback.histCallbackCount)

                        statsCollector.shutDown(1000)
                    }
                }
            }
        }
    }
}

private class StatsCallbackMock : StatisticsCollectorCallback {
    var tickerCallbackCount = 0
    var histCallbackCount = 0

    override fun tickerCallback(tickerType: TickerType, tickerCount: Long) {
        tickerCallbackCount++
    }

    override fun histogramCallback(
        histType: HistogramType,
        histData: HistogramData
    ) {
        histCallbackCount++
    }
}
