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
     */
    fun setIterateUpperBound(iterateUpperBound: AbstractSlice<*>): ReadOptions

    /**
     * @return the inclusive lower bound for iteration, or {@code null} if unset.
     */
    fun iterateLowerBound(): Slice?

    /**
     * Restrict iteration to keys greater or equal to the supplied bound.
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

expect fun ReadOptions.setTableFilter(tableFilter: AbstractTableFilter): ReadOptions
