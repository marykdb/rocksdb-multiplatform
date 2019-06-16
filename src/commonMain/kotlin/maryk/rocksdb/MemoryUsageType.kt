package maryk.rocksdb

/**
 * MemoryUsageType
 *
 * The value will be used as a key to indicate the type of memory usage
 * described
 */
expect enum class MemoryUsageType {
    /** Memory usage of all the mem-tables. */
    kMemTableTotal,
    /** Memory usage of those un-flushed mem-tables. */
    kMemTableUnFlushed,
    /** Memory usage of all the table readers. */
    kTableReadersTotal,
    /** Memory usage by Cache. */
    kCacheTotal,
    /** Max usage types - copied to keep 1:1 with native. */
    kNumUsageTypes;

    /**
     * Returns the byte value of the enumerations value
     * @return byte representation
     */
    fun getValue(): Byte
}
