package maryk.rocksdb

expect class ReadOptions() : RocksObject {
    /**
     * @param verifyChecksums verification will be performed on every read
     * when set to true
     * @param fillCache if true, then fill-cache behavior will be performed.
     */
    constructor(verifyChecksums: Boolean, fillCache: Boolean)

    /**
     * Copy constructor.
     *
     * NOTE: This does a shallow copy, which means snapshot, iterate_upper_bound
     * and other pointers will be cloned!
     *
     * @param other The ReadOptions to copy.
     */
    constructor(other: ReadOptions)

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
     * Returns the currently assigned Snapshot instance.
     *
     * @return the Snapshot assigned to this instance. If no Snapshot
     * is assigned null.
     */
    fun snapshot(): Snapshot?

    /**
     *
     * If "snapshot" is non-nullptr, read as of the supplied snapshot
     * (which must belong to the DB that is being read and which must
     * not have been released).  If "snapshot" is nullptr, use an implicit
     * snapshot of the state at the beginning of this read operation.
     *
     * Default: null
     *
     * @param snapshot [Snapshot] instance
     * @return the reference to the current ReadOptions.
     */
    fun setSnapshot(snapshot: Snapshot?): ReadOptions

    /**
     * Returns the current read tier.
     *
     * @return the read tier in use, by default [ReadTier.READ_ALL_TIER]
     */
    fun readTier(): ReadTier

    /**
     * Specify if this read request should process data that ALREADY
     * resides on a particular cache. If the required data is not
     * found at the specified cache, then [RocksDBException] is thrown.
     *
     * @param readTier [ReadTier] instance
     * @return the reference to the current ReadOptions.
     */
    fun setReadTier(readTier: ReadTier): ReadOptions

    /**
     * Specify to create a tailing iterator -- a special iterator that has a
     * view of the complete database (i.e. it can also be used to read newly
     * added data) and is optimized for sequential reads. It will return records
     * that were inserted into the database after the creation of the iterator.
     * Default: false
     *
     * Not supported in `ROCKSDB_LITE` mode!
     *
     * @return true if tailing iterator is enabled.
     */
    fun tailing(): Boolean

    /**
     * Specify to create a tailing iterator -- a special iterator that has a
     * view of the complete database (i.e. it can also be used to read newly
     * added data) and is optimized for sequential reads. It will return records
     * that were inserted into the database after the creation of the iterator.
     * Default: false
     * Not supported in ROCKSDB_LITE mode!
     *
     * @param tailing if true, then tailing iterator will be enabled.
     * @return the reference to the current ReadOptions.
     */
    fun setTailing(tailing: Boolean): ReadOptions

    /**
     * Returns whether a total seek order will be used
     *
     * @return the setting of whether a total seek order will be used
     */
    fun totalOrderSeek(): Boolean

    /**
     * Enable a total order seek regardless of index format (e.g. hash index)
     * used in the table. Some table format (e.g. plain table) may not support
     * this option.
     *
     * @param totalOrderSeek if true, then total order seek will be enabled.
     * @return the reference to the current ReadOptions.
     */
    fun setTotalOrderSeek(totalOrderSeek: Boolean): ReadOptions

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
     * Returns whether the blocks loaded by the iterator will be pinned in memory
     *
     * @return the setting of whether the blocks loaded by the iterator will be
     * pinned in memory
     */
    fun pinData(): Boolean

    /**
     * Keep the blocks loaded by the iterator pinned in memory as long as the
     * iterator is not deleted, If used when reading from tables created with
     * BlockBasedTableOptions::use_delta_encoding = false,
     * Iterator's property "rocksdb.iterator.is-key-pinned" is guaranteed to
     * return 1.
     *
     * @param pinData if true, the blocks loaded by the iterator will be pinned
     * @return the reference to the current ReadOptions.
     */
    fun setPinData(pinData: Boolean): ReadOptions

    /**
     * If true, when PurgeObsoleteFile is called in CleanupIteratorState, we
     * schedule a background job in the flush job queue and delete obsolete files
     * in background.
     *
     * Default: false
     *
     * @return true when PurgeObsoleteFile is called in CleanupIteratorState
     */
    fun backgroundPurgeOnIteratorCleanup(): Boolean

    /**
     * If true, when PurgeObsoleteFile is called in CleanupIteratorState, we
     * schedule a background job in the flush job queue and delete obsolete files
     * in background.
     *
     * Default: false
     *
     * @param backgroundPurgeOnIteratorCleanup true when PurgeObsoleteFile is
     * called in CleanupIteratorState
     * @return the reference to the current ReadOptions.
     */
    fun setBackgroundPurgeOnIteratorCleanup(
        backgroundPurgeOnIteratorCleanup: Boolean
    ): ReadOptions

    /**
     * If non-zero, NewIterator will create a new table reader which
     * performs reads of the given size. Using a large size (&gt; 2MB) can
     * improve the performance of forward iteration on spinning disks.
     *
     * Default: 0
     *
     * @return The readahead size is bytes
     */
    fun readaheadSize(): Long

    /**
     * If non-zero, NewIterator will create a new table reader which
     * performs reads of the given size. Using a large size (&gt; 2MB) can
     * improve the performance of forward iteration on spinning disks.
     *
     * Default: 0
     *
     * @param readaheadSize The readahead size is bytes
     * @return the reference to the current ReadOptions.
     */
    fun setReadaheadSize(readaheadSize: Long): ReadOptions

    /**
     * A threshold for the number of keys that can be skipped before failing an
     * iterator seek as incomplete.
     *
     * @return the number of keys that can be skipped
     * before failing an iterator seek as incomplete.
     */
    fun maxSkippableInternalKeys(): Long

    /**
     * A threshold for the number of keys that can be skipped before failing an
     * iterator seek as incomplete. The default value of 0 should be used to
     * never fail a request as incomplete, even on skipping too many keys.
     *
     * Default: 0
     *
     * @param maxSkippableInternalKeys the number of keys that can be skipped
     * before failing an iterator seek as incomplete.
     *
     * @return the reference to the current ReadOptions.
     */
    fun setMaxSkippableInternalKeys(
        maxSkippableInternalKeys: Long
    ): ReadOptions

    /**
     * If true, keys deleted using the DeleteRange() API will be visible to
     * readers until they are naturally deleted during compaction. This improves
     * read performance in DBs with many range deletions.
     *
     * Default: false
     *
     * @return true if keys deleted using the DeleteRange() API will be visible
     */
    fun ignoreRangeDeletions(): Boolean

    /**
     * If true, keys deleted using the DeleteRange() API will be visible to
     * readers until they are naturally deleted during compaction. This improves
     * read performance in DBs with many range deletions.
     *
     * Default: false
     *
     * @param ignoreRangeDeletions true if keys deleted using the DeleteRange()
     * API should be visible
     * @return the reference to the current ReadOptions.
     */
    fun setIgnoreRangeDeletions(ignoreRangeDeletions: Boolean): ReadOptions

    /**
     * Defines the smallest key at which the backward
     * iterator can return an entry. Once the bound is passed,
     * [RocksIterator.isValid] will be false.
     *
     * The lower bound is inclusive i.e. the bound value is a valid
     * entry.
     *
     * If prefix_extractor is not null, the Seek target and `iterate_lower_bound`
     * need to have the same prefix. This is because ordering is not guaranteed
     * outside of prefix domain.
     *
     * Default: null
     *
     * @param iterateLowerBound Slice representing the upper bound
     * @return the reference to the current ReadOptions.
     */
    fun setIterateLowerBound(iterateLowerBound: Slice?): ReadOptions

    /**
     * Returns the smallest key at which the backward
     * iterator can return an entry.
     *
     * The lower bound is inclusive i.e. the bound value is a valid entry.
     *
     * @return the smallest key, or null if there is no lower bound defined.
     */
    fun iterateLowerBound(): Slice?

    /**
     * Defines the extent up to which the forward iterator
     * can returns entries. Once the bound is reached,
     * [RocksIterator.isValid] will be false.
     *
     * The upper bound is exclusive i.e. the bound value is not a valid entry.
     *
     * If iterator_extractor is not null, the Seek target and iterate_upper_bound
     * need to have the same prefix. This is because ordering is not guaranteed
     * outside of prefix domain.
     *
     * Default: null
     *
     * @param iterateUpperBound Slice representing the upper bound
     * @return the reference to the current ReadOptions.
     */
    fun setIterateUpperBound(iterateUpperBound: Slice?): ReadOptions

    /**
     * Returns the largest key at which the forward
     * iterator can return an entry.
     *
     * The upper bound is exclusive i.e. the bound value is not a valid entry.
     *
     * @return the largest key, or null if there is no upper bound defined.
     */
    fun iterateUpperBound(): Slice?

    /**
     * A callback to determine whether relevant keys for this scan exist in a
     * given table based on the table's properties. The callback is passed the
     * properties of each table during iteration. If the callback returns false,
     * the table will not be scanned. This option only affects Iterators and has
     * no impact on point lookups.
     *
     * Default: null (every table will be scanned)
     *
     * @param tableFilter the table filter for the callback.
     *
     * @return the reference to the current ReadOptions.
     */
    fun setTableFilter(tableFilter: AbstractTableFilter): ReadOptions

    /**
     * Needed to support differential snapshots. Has 2 effects:
     * 1) Iterator will skip all internal keys with seqnum &lt; iter_start_seqnum
     * 2) if this param &gt; 0 iterator will return INTERNAL keys instead of user
     * keys; e.g. return tombstones as well.
     *
     * Default: 0 (don't filter by seqnum, return user keys)
     *
     * @param startSeqnum the starting sequence number.
     *
     * @return the reference to the current ReadOptions.
     */
    fun setIterStartSeqnum(startSeqnum: Long): ReadOptions

    /**
     * Returns the starting Sequence Number of any iterator.
     * See [.setIterStartSeqnum].
     *
     * @return the starting sequence number of any iterator.
     */
    fun iterStartSeqnum(): Long
}
