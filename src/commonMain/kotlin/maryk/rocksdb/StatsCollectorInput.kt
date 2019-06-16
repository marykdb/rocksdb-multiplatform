package maryk.rocksdb


/**
 * Contains all information necessary to collect statistics from one instance
 * of DB statistics.
 */
expect class StatsCollectorInput
    /**
     * Constructor for StatsCollectorInput.
     *
     * @param statistics Reference of DB statistics.
     * @param statsCallback Reference of statistics callback interface.
     */
    constructor(
        statistics: Statistics,
        callback: StatisticsCollectorCallback
    )
{
    fun getStatistics(): Statistics

    fun getCallback(): StatisticsCollectorCallback
}
