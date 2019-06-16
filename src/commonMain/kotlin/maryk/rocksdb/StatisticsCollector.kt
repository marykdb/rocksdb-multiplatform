package maryk.rocksdb


/**
 * Helper class to collect DB statistics periodically at a period specified in
 * constructor. Callback function (provided in constructor) is called with
 * every statistics collection.
 *
 * Caller should call start() to start statistics collection. Shutdown() should
 * be called to stop stats collection and should be called before statistics (
 * provided in constructor) reference has been disposed.
 */
expect class StatisticsCollector
    /**
     * Constructor for statistics collector.
     *
     * @param statsCollectorInputList List of statistics collector input.
     * @param statsCollectionIntervalInMilliSeconds Statistics collection time
     * period (specified in milliseconds).
     */
    constructor(
        statsCollectorInputList: List<StatsCollectorInput>,
        statsCollectionInterval: Int
    )
{
    fun start()

    /**
     * Shuts down statistics collector.
     *
     * @param shutdownTimeout Time in milli-seconds to wait for shutdown before
     * killing the collection process.
     * @throws java.lang.InterruptedException thrown if Threads are interrupted.
     */
    fun shutDown(shutdownTimeout: Int)
}
