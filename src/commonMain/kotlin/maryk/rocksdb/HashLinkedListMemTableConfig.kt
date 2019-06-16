package maryk.rocksdb

import kotlin.jvm.JvmStatic

/**
 * The config for hash linked list memtable representation
 * Such memtable contains a fix-sized array of buckets, where
 * each bucket points to a sorted singly-linked
 * list (or null if the bucket is empty).
 *
 * Note that since this mem-table representation relies on the
 * key prefix, it is required to invoke one of the usePrefixExtractor
 * functions to specify how to extract key prefix given a key.
 * If proper prefix-extractor is not set, then RocksDB will
 * use the default memtable representation (SkipList) instead
 * and post a warning in the LOG.
 */
expect class HashLinkedListMemTableConfig() : MemTableConfig {
    /**
     * Set the number of buckets in the fixed-size array used
     * in the hash linked-list mem-table.
     *
     * @param count the number of hash buckets.
     * @return the reference to the current HashLinkedListMemTableConfig.
     */
    fun setBucketCount(
        count: Long
    ): HashLinkedListMemTableConfig

    /**
     * Returns the number of buckets that will be used in the memtable
     * created based on this config.
     *
     * @return the number of buckets
     */
    fun bucketCount(): Long

    /**
     *
     * Set the size of huge tlb or allocate the hashtable bytes from
     * malloc if `size <= 0`.
     *
     *
     * The user needs to reserve huge pages for it to be allocated,
     * like: `sysctl -w vm.nr_hugepages=20`
     *
     *
     * See linux documentation/vm/hugetlbpage.txt
     *
     * @param size if set to `<= 0` hashtable bytes from malloc
     * @return the reference to the current HashLinkedListMemTableConfig.
     */
    fun setHugePageTlbSize(
        size: Long
    ): HashLinkedListMemTableConfig

    /**
     * Returns the size value of hugePageTlbSize.
     *
     * @return the hugePageTlbSize.
     */
    fun hugePageTlbSize(): Long

    /**
     * If number of entries in one bucket exceeds that setting, log
     * about it.
     *
     * @param threshold - number of entries in a single bucket before
     * logging starts.
     * @return the reference to the current HashLinkedListMemTableConfig.
     */
    fun setBucketEntriesLoggingThreshold(threshold: Int): HashLinkedListMemTableConfig

    /**
     * Returns the maximum number of entries in one bucket before
     * logging starts.
     *
     * @return maximum number of entries in one bucket before logging
     * starts.
     */
    fun bucketEntriesLoggingThreshold(): Int

    /**
     * If true the distribution of number of entries will be logged.
     *
     * @param logDistribution - boolean parameter indicating if number
     * of entry distribution shall be logged.
     * @return the reference to the current HashLinkedListMemTableConfig.
     */
    fun setIfLogBucketDistWhenFlush(logDistribution: Boolean): HashLinkedListMemTableConfig

    /**
     * Returns information about logging the distribution of
     * number of entries on flush.
     *
     * @return if distribution of number of entries shall be logged.
     */
    fun ifLogBucketDistWhenFlush(): Boolean

    /**
     * Set maximum number of entries in one bucket. Exceeding this val
     * leads to a switch from LinkedList to SkipList.
     *
     * @param threshold maximum number of entries before SkipList is
     * used.
     * @return the reference to the current HashLinkedListMemTableConfig.
     */
    fun setThresholdUseSkiplist(threshold: Int): HashLinkedListMemTableConfig

    /**
     * Returns entries per bucket threshold before LinkedList is
     * replaced by SkipList usage for that bucket.
     *
     * @return entries per bucket threshold before SkipList is used.
     */
    fun thresholdUseSkiplist(): Int

    companion object {
        @JvmStatic
        val DEFAULT_BUCKET_COUNT: Long
        @JvmStatic
        val DEFAULT_HUGE_PAGE_TLB_SIZE: Long
        @JvmStatic
        val DEFAULT_BUCKET_ENTRIES_LOG_THRES: Int
        @JvmStatic
        val DEFAULT_IF_LOG_BUCKET_DIST_WHEN_FLUSH: Boolean
        @JvmStatic
        val DEFAUL_THRESHOLD_USE_SKIPLIST: Int
    }
}
