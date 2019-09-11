package maryk.rocksdb

actual interface StatisticsCollectorCallback {
    actual fun tickerCallback(tickerType: TickerType, tickerCount: Long)

    actual fun histogramCallback(histType: HistogramType, histData: HistogramData)
}
