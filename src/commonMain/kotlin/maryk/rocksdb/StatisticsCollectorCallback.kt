package maryk.rocksdb

/**
 * Callback interface provided to StatisticsCollector.
 *
 * Thread safety:
 * StatisticsCollector doesn't make any guarantees about thread safety.
 * If the same reference of StatisticsCollectorCallback is passed to multiple
 * StatisticsCollector references, then its the responsibility of the
 * user to make StatisticsCollectorCallback's implementation thread-safe.
 */
expect interface StatisticsCollectorCallback {
    /**
     * Callback function to get ticker values.
     * @param tickerType Ticker type.
     * @param tickerCount Value of ticker type.
     */
    fun tickerCallback(tickerType: TickerType, tickerCount: Long)

    /**
     * Callback function to get histogram values.
     * @param histType Histogram type.
     * @param histData Histogram data.
     */
    fun histogramCallback(histType: HistogramType, histData: HistogramData)
}
