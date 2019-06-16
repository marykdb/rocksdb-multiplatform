package maryk.rocksdb

/**
 * The level of Statistics to report.
 */
expect enum class StatsLevel {
    /**
     * Collect all stats except time inside mutex lock AND time spent on
     * compression.
     */
    EXCEPT_DETAILED_TIMERS,

    /**
     * Collect all stats except the counters requiring to get time inside the
     * mutex lock.
     */
    EXCEPT_TIME_FOR_MUTEX,

    /**
     * Collect all stats, including measuring duration of mutex operations.
     *
     * If getting time is expensive on the platform to run, it can
     * reduce scalability to more threads, especially for writes.
     */
    ALL;
}

/**
 * Get StatsLevel by byte value.
 *
 * @param value byte representation of StatsLevel.
 *
 * @return [org.rocksdb.StatsLevel] instance.
 * @throws java.lang.IllegalArgumentException if an invalid
 * value is provided.
 */
expect fun getStatsLevel(value: Byte): StatsLevel
