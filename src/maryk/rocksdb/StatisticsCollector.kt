package maryk.rocksdb

data class StatsCollectorInput(
    val statistics: Statistics,
    val callback: StatisticsCollectorCallback
)

interface StatisticsCollectorCallback {
    fun tickerCallback(tickerType: TickerType, tickerCount: Long)
    fun histogramCallback(histogramType: HistogramType, histogramData: HistogramData)
}

expect class StatisticsCollector(
    statsCollectorInputList: List<StatsCollectorInput>,
    statsCollectionIntervalInMilliSeconds: Int
) {
    fun start()
    fun shutDown(shutdownTimeout: Int)
}
