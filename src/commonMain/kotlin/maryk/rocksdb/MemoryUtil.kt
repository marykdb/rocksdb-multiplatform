package maryk.rocksdb

expect object MemoryUtil {
    /**
     * Returns the approximate memory usage of different types in the input
     * list of DBs and Cache set.  For instance, in the output map the key
     * kMemTableTotal will be associated with the memory
     * usage of all the mem-tables from all the input rocksdb instances.
     *
     *
     * Note that for memory usage inside Cache class, we will
     * only report the usage of the input "cache_set" without
     * including those Cache usage inside the input list "dbs"
     * of DBs.
     *
     * @param dbs List of dbs to collect memory usage for.
     * @param caches Set of caches to collect memory usage for.
     * @return Map from [MemoryUsageType] to memory usage as a [Long].
     */
    fun getApproximateMemoryUsageByType(dbs: List<RocksDB>?, caches: Set<Cache>?): Map<MemoryUsageType, Long>
}
