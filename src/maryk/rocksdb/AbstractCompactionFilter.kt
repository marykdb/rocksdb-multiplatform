package maryk.rocksdb

/**
 * A CompactionFilter allows an application to modify/delete a key-value at
 * the time of compaction.
 */
expect abstract class AbstractCompactionFilter<T : AbstractSlice<*>>: RocksObject

expect open class AbstractCompactionFilterContext {
    /**
     * Does this compaction run include all data files
     *
     * @return true if this is a full compaction run
     */
    fun isFullCompaction(): Boolean

    /**
     * Is this compaction requested by the client,
     * or is it occurring as an automatic compaction process
     *
     * @return true if the compaction was initiated by the client
     */
    fun isManualCompaction(): Boolean
}
