package maryk.rocksdb

expect class Statistics : RocksObject {
    constructor()

    /**
     * Gets the current stats level.
     *
     * @return The stats level.
     */
    fun statsLevel(): StatsLevel?

    /**
     * Sets the stats level.
     *
     * @param statsLevel The stats level to set.
     */
    fun setStatsLevel(statsLevel: StatsLevel)

    /**
     * Get the count for a ticker.
     *
     * @param tickerType The ticker to get the count for
     *
     * @return The count for the ticker
     */
    fun getTickerCount(tickerType: TickerType): Long

    /**
     * Gets the histogram data for a particular histogram.
     *
     * @param histogramType The histogram to retrieve the data for
     *
     * @return The histogram data
     */
    fun getHistogramData(histogramType: HistogramType): HistogramData

    /**
     * Resets all ticker and histogram stats.
     *
     * @throws RocksDBException if an error occurs when resetting the statistics.
     */
    fun reset()
}
