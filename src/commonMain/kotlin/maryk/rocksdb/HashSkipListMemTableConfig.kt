package maryk.rocksdb

/**
 * The config for hash skip-list mem-table representation.
 * Such mem-table representation contains a fix-sized array of
 * buckets, where each bucket points to a skiplist (or null if the
 * bucket is empty).
 *
 * Note that since this mem-table representation relies on the
 * key prefix, it is required to invoke one of the usePrefixExtractor
 * functions to specify how to extract key prefix given a key.
 * If proper prefix-extractor is not set, then RocksDB will
 * use the default memtable representation (SkipList) instead
 * and post a warning in the LOG.
 */
expect class HashSkipListMemTableConfig() : MemTableConfig {
    /**
     * Set the number of hash buckets used in the hash skiplist memtable.
     * Default = 1000000.
     *
     * @param count the number of hash buckets used in the hash
     * skiplist memtable.
     * @return the reference to the current HashSkipListMemTableConfig.
     */
    fun setBucketCount(
        count: Long
    ): HashSkipListMemTableConfig

    /** @return the number of hash buckets */
    fun bucketCount(): Long

    /**
     * Set the height of the skip list.  Default = 4.
     *
     * @param height height to set.
     *
     * @return the reference to the current HashSkipListMemTableConfig.
     */
    fun setHeight(height: Int): HashSkipListMemTableConfig

    /** @return the height of the skip list. */
    fun height(): Int

    /**
     * Set the branching factor used in the hash skip-list memtable.
     * This factor controls the probabilistic size ratio between adjacent
     * links in the skip list.
     *
     * @param bf the probabilistic size ratio between adjacent link
     * lists in the skip list.
     * @return the reference to the current HashSkipListMemTableConfig.
     */
    fun setBranchingFactor(
        bf: Int
    ): HashSkipListMemTableConfig

    /**
     * @return branching factor, the probabilistic size ratio between
     * adjacent links in the skip list.
     */
    fun branchingFactor(): Int
}
