package maryk.rocksdb

expect class ReadOptions() : RocksObject {
    /**
     * If true, all data read from underlying storage will be
     * verified against corresponding checksums.
     * Default: true
     *
     * @return true if checksum verification is on.
     */
    fun verifyChecksums(): Boolean

    /**
     * If true, all data read from underlying storage will be
     * verified against corresponding checksums.
     * Default: true
     *
     * @param verifyChecksums if true, then checksum verification
     * will be performed on every read.
     * @return the reference to the current ReadOptions.
     */
    fun setVerifyChecksums(
        verifyChecksums: Boolean
    ): ReadOptions

    /**
     * Fill the cache when loading the block-based sst formated db.
     * Callers may wish to set this field to false for bulk scans.
     * Default: true
     *
     * @return true if the fill-cache behavior is on.
     */
    fun fillCache(): Boolean

    /**
     * Fill the cache when loading the block-based sst formatted db.
     * Callers may wish to set this field to false for bulk scans.
     * Default: true
     *
     * @param fillCache if true, then fill-cache behavior will be
     * performed.
     * @return the reference to the current ReadOptions.
     */
    fun setFillCache(fillCache: Boolean): ReadOptions

    /**
     * Returns whether the iterator only iterates over the same prefix as the seek
     *
     * @return the setting of whether the iterator only iterates over the same
     * prefix as the seek, default is false
     */
    fun prefixSameAsStart(): Boolean

    /**
     * Enforce that the iterator only iterates over the same prefix as the seek.
     * This option is effective only for prefix seeks, i.e. prefix_extractor is
     * non-null for the column family and [.totalOrderSeek] is false.
     * Unlike iterate_upper_bound, [.setPrefixSameAsStart] only
     * works within a prefix but in both directions.
     *
     * @param prefixSameAsStart if true, then the iterator only iterates over the
     * same prefix as the seek
     * @return the reference to the current ReadOptions.
     */
    fun setPrefixSameAsStart(prefixSameAsStart: Boolean): ReadOptions

    /** Iterator readahead size in bytes; zero uses RocksDB's default behavior. */
    fun readaheadSize(): Long

    /** Set a non-negative iterator readahead size in bytes. */
    fun setReadaheadSize(readaheadSize: Long): ReadOptions

    /** Whether RocksDB may select prefix seeks when they preserve total-order results. */
    fun autoPrefixMode(): Boolean

    /** Enable automatic prefix selection based on the seek key and iterator upper bound. */
    fun setAutoPrefixMode(mode: Boolean): ReadOptions

    /** Whether seeks ignore the configured prefix extractor and use total key ordering. */
    fun totalOrderSeek(): Boolean

    /** Enable total-order seeks even when the column family has a prefix extractor. */
    fun setTotalOrderSeek(totalOrderSeek: Boolean): ReadOptions

    /** Whether asynchronous prefetching is requested; actual support depends on the filesystem. */
    fun asyncIo(): Boolean

    /** Request asynchronous prefetching where supported by RocksDB and the filesystem. */
    fun setAsyncIo(asyncIo: Boolean): ReadOptions

    /** Best-effort operation deadline in microseconds since the Unix epoch; zero disables it. */
    fun deadline(): Long

    /** Set a non-negative absolute deadline in microseconds since the Unix epoch. */
    fun setDeadline(deadlineTime: Long): ReadOptions

    /** Timeout for each filesystem read, in microseconds; zero disables it. */
    fun ioTimeout(): Long

    /** Set a non-negative timeout in microseconds for each filesystem read. */
    fun setIoTimeout(ioTimeout: Long): ReadOptions

    /** Whether iterator cleanup may schedule obsolete-file deletion in the background. */
    fun backgroundPurgeOnIteratorCleanup(): Boolean

    /** Schedule obsolete-file deletion in the background when cleaning up an iterator. */
    fun setBackgroundPurgeOnIteratorCleanup(backgroundPurgeOnIteratorCleanup: Boolean): ReadOptions

    /** Whether reads ignore range tombstones and may expose keys covered by DeleteRange. */
    fun ignoreRangeDeletions(): Boolean

    /** Ignore range tombstones. Deleted keys may become visible until compaction; this RocksDB option is deprecated. */
    fun setIgnoreRangeDeletions(ignoreRangeDeletions: Boolean): ReadOptions

    /** Maximum internal keys an iterator may skip before reporting incomplete; zero disables the limit. */
    fun maxSkippableInternalKeys(): Long

    /** Set a non-negative internal-key skip limit; zero disables the limit. */
    fun setMaxSkippableInternalKeys(maxSkippableInternalKeys: Long): ReadOptions

    /** Whether the iterator pins loaded data blocks until it closes. */
    fun pinData(): Boolean

    /** Pin loaded blocks for the iterator lifetime. This may increase retained memory. */
    fun setPinData(pinData: Boolean): ReadOptions

    /** Maximum cumulative MultiGet value bytes; -1 means unlimited. */
    fun valueSizeSoftLimit(): Long

    /** Set the cumulative MultiGet value-byte limit, or -1 for unlimited. */
    fun setValueSizeSoftLimit(valueSizeSoftLimit: Long): ReadOptions

    /**
     * @return the snapshot that will be used for reads, if any.
     */
    fun snapshot(): Snapshot?

    /**
     * Use the specified snapshot for read operations. Pass {@code null} to clear it.
     */
    fun setSnapshot(snapshot: Snapshot?): ReadOptions

    /**
     * @return the exclusive upper bound for iteration, or {@code null} if unset.
     */
    fun iterateUpperBound(): Slice?

    /**
     * Restrict iteration to keys strictly smaller than the supplied bound.
     * On Native, keep these options open while bounded iterators are in use.
     * Closing the options or changing a bound invalidates dependent iterators.
     */
    fun setIterateUpperBound(iterateUpperBound: AbstractSlice<*>): ReadOptions

    /**
     * @return the inclusive lower bound for iteration, or {@code null} if unset.
     */
    fun iterateLowerBound(): Slice?

    /**
     * Restrict iteration to keys greater or equal to the supplied bound.
     * On Native, keep these options open while bounded iterators are in use.
     * Closing the options or changing a bound invalidates dependent iterators.
     */
    fun setIterateLowerBound(iterateLowerBound: AbstractSlice<*>): ReadOptions

    /**
     * @return the tier used when serving reads.
     */
    fun readTier(): ReadTier

    /**
     * Choose which data tier to consult when serving reads.
     */
    fun setReadTier(readTier: ReadTier): ReadOptions

    /**
     * @return whether the iterator will remain positioned when new data is appended.
     */
    fun tailing(): Boolean

    /**
     * Enable a tailing iterator that can continue reading newly appended data.
     */
    fun setTailing(tailing: Boolean): ReadOptions

}

/**
 * On Native, keep these options open while iterators use this filter.
 * Closing the options or replacing the filter invalidates dependent iterators.
 */
expect fun ReadOptions.setTableFilter(tableFilter: AbstractTableFilter): ReadOptions
