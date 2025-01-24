package maryk.rocksdb

expect class ColumnFamilyOptions() : RocksObject {
    /**
     * The minimum number of write buffers that will be merged together
     * before writing to storage.  If set to 1, then
     * all write buffers are flushed to L0 as individual files and this increases
     * read amplification because a get request has to check in all of these
     * files. Also, an in-memory merge may result in writing lesser
     * data to storage if there are duplicate records in each of these
     * individual write buffers.  Default: 1
     *
     * @param minWriteBufferNumberToMerge the minimum number of write buffers
     * that will be merged together.
     * @return the reference to the current options.
     */
    fun setMinWriteBufferNumberToMerge(
        minWriteBufferNumberToMerge: Int
    ): ColumnFamilyOptions

    /**
     * The minimum number of write buffers that will be merged together
     * before writing to storage.  If set to 1, then
     * all write buffers are flushed to L0 as individual files and this increases
     * read amplification because a get request has to check in all of these
     * files. Also, an in-memory merge may result in writing lesser
     * data to storage if there are duplicate records in each of these
     * individual write buffers.  Default: 1
     *
     * @return the minimum number of write buffers that will be merged together.
     */
    fun minWriteBufferNumberToMerge(): Int

    /**
     * The total maximum number of write buffers to maintain in memory including
     * copies of buffers that have already been flushed.  Unlike
     * [AdvancedMutableColumnFamilyOptionsInterface.maxWriteBufferNumber],
     * this parameter does not affect flushing.
     * This controls the minimum amount of write history that will be available
     * in memory for conflict checking when Transactions are used.
     *
     * When using an OptimisticTransactionDB:
     * If this value is too low, some transactions may fail at commit time due
     * to not being able to determine whether there were any write conflicts.
     *
     * When using a TransactionDB:
     * If Transaction::SetSnapshot is used, TransactionDB will read either
     * in-memory write buffers or SST files to do write-conflict checking.
     * Increasing this value can reduce the number of reads to SST files
     * done for conflict detection.
     *
     * Setting this value to 0 will cause write buffers to be freed immediately
     * after they are flushed.
     * If this value is set to -1,
     * [AdvancedMutableColumnFamilyOptionsInterface.maxWriteBufferNumber]
     * will be used.
     *
     * Default:
     * If using a TransactionDB/OptimisticTransactionDB, the default value will
     * be set to the value of
     * [AdvancedMutableColumnFamilyOptionsInterface.maxWriteBufferNumber]
     * if it is not explicitly set by the user. Otherwise, the default is 0.
     *
     * @param maxWriteBufferNumberToMaintain The maximum number of write
     * buffers to maintain
     *
     * @return the reference to the current options.
     */
    fun setMaxWriteBufferNumberToMaintain(
        maxWriteBufferNumberToMaintain: Int
    ): ColumnFamilyOptions

    /**
     * The total maximum number of write buffers to maintain in memory including
     * copies of buffers that have already been flushed.
     *
     * @return maxWriteBufferNumberToMaintain The maximum number of write buffers
     * to maintain
     */
    fun maxWriteBufferNumberToMaintain(): Int

    /**
     * Control locality of bloom filter probes to improve cache miss rate.
     * This option only applies to memtable prefix bloom and plaintable
     * prefix bloom. It essentially limits the max number of cache lines each
     * bloom filter check can touch.
     * This optimization is turned off when set to 0. The number should never
     * be greater than number of probes. This option can boost performance
     * for in-memory workload but should use with care since it can cause
     * higher false positive rate.
     * Default: 0
     *
     * @param bloomLocality the level of locality of bloom-filter probes.
     * @return the reference to the current options.
     */
    fun setBloomLocality(bloomLocality: Int): ColumnFamilyOptions

    /**
     * Control locality of bloom filter probes to improve cache miss rate.
     * This option only applies to memtable prefix bloom and plaintable
     * prefix bloom. It essentially limits the max number of cache lines each
     * bloom filter check can touch.
     * This optimization is turned off when set to 0. The number should never
     * be greater than number of probes. This option can boost performance
     * for in-memory workload but should use with care since it can cause
     * higher false positive rate.
     * Default: 0
     *
     * @return the level of locality of bloom-filter probes.
     * @see .setBloomLocality
     */
    fun bloomLocality(): Int

    /**
     * Set the number of levels for this database
     * If level-styled compaction is used, then this number determines
     * the total number of levels.
     *
     * @param numLevels the number of levels.
     * @return the reference to the current options.
     */
    fun setNumLevels(numLevels: Int): ColumnFamilyOptions

    /**
     * If level-styled compaction is used, then this number determines
     * the total number of levels.
     *
     * @return the number of levels.
     */
    fun numLevels(): Int

    /**
     * Set compaction style for DB.
     *
     * Default: LEVEL.
     *
     * @param compactionStyle Compaction style.
     * @return the reference to the current options.
     */
    fun setCompactionStyle(
        compactionStyle: CompactionStyle
    ): ColumnFamilyOptions

    /**
     * Compaction style for DB.
     *
     * @return Compaction style.
     */
    fun compactionStyle(): CompactionStyle

    /**
     * Set [BuiltinComparator] to be used with RocksDB.
     *
     * Note: Comparator can be set once upon database creation.
     *
     * Default: BytewiseComparator.
     * @param builtinComparator a [BuiltinComparator] type.
     * @return the instance of the current object.
     */
    fun setComparator(
        builtinComparator: BuiltinComparator
    ): ColumnFamilyOptions

    /**
     * Use the specified comparator for key ordering.
     *
     * Comparator should not be disposed before options instances using this comparator is
     * disposed. If dispose() function is not called, then comparator object will be
     * GC'd automatically.
     *
     * Comparator instance can be re-used in multiple options instances.
     *
     * @param comparator instance.
     * @return the instance of the current object.
     */
    fun setComparator(
        comparator: AbstractComparator
    ): ColumnFamilyOptions

    /**
     * Set the merge operator to be used for merging two merge operands
     * of the same key. The merge function is invoked during
     * compaction and at lookup time, if multiple key/value pairs belonging
     * to the same key are found in the database.
     *
     * @param name the name of the merge function, as defined by
     * the MergeOperators factory (see utilities/MergeOperators.h)
     * The merge function is specified by name and must be one of the
     * standard merge operators provided by RocksDB. The available
     * operators are "put", "uint64add", "stringappend" and "stringappendtest".
     * @return the instance of the current object.
     */
    fun setMergeOperatorName(name: String): ColumnFamilyOptions

    /**
     * Set the merge operator to be used for merging two different key/value
     * pairs that share the same key. The merge function is invoked during
     * compaction and at lookup time, if multiple key/value pairs belonging
     * to the same key are found in the database.
     *
     * @param mergeOperator {@link MergeOperator} instance.
     * @return the instance of the current object.
     */
    fun setMergeOperator(mergeOperator: MergeOperator): ColumnFamilyOptions

    /**
     * A single CompactionFilter instance to call into during compaction.
     * Allows an application to modify/delete a key-value during background
     * compaction.
     *
     * If the client requires a new compaction filter to be used for different
     * compaction runs, it can specify call
     * {@link #setCompactionFilterFactory(AbstractCompactionFilterFactory)}
     * instead.
     *
     * The client should specify only set one of the two.
     * {@link #setCompactionFilter(AbstractCompactionFilter)} takes precedence
     * over {@link #setCompactionFilterFactory(AbstractCompactionFilterFactory)}
     * if the client specifies both.
     *
     * If multithreaded compaction is being used, the supplied CompactionFilter
     * instance may be used from different threads concurrently and so should be thread-safe.
     *
     * @param compactionFilter {@link AbstractCompactionFilter} instance.
     * @return the instance of the current object.
     */
    fun setCompactionFilter(
        compactionFilter: AbstractCompactionFilter<out AbstractSlice<*>>
    ): ColumnFamilyOptions

    /**
     * Accessor for the CompactionFilter instance in use.
     *
     * @return Reference to the CompactionFilter, or null if one hasn't been set.
     */
    fun compactionFilter(): AbstractCompactionFilter<out AbstractSlice<*>>?

    /**
     * This prefix-extractor uses the first n bytes of a key as its prefix.
     *
     * In some hash-based memtable representation such as HashLinkedList
     * and HashSkipList, prefixes are used to partition the keys into
     * several buckets.  Prefix extractor is used to specify how to
     * extract the prefix given a key.
     *
     * @param n use the first n bytes of a key as its prefix.
     * @return the reference to the current option.
     */
    fun useFixedLengthPrefixExtractor(n: Int): ColumnFamilyOptions

    /**
     * The ratio between the total size of level-(L+1) files and the total
     * size of level-L files for all L.
     * DEFAULT: 10
     *
     * @param multiplier the ratio between the total size of level-(L+1)
     * files and the total size of level-L files for all L.
     * @return the reference to the current option.
     */
    fun setMaxBytesForLevelMultiplier(
        multiplier: Double
    ): ColumnFamilyOptions

    /**
     * The ratio between the total size of level-(L+1) files and the total
     * size of level-L files for all L.
     * DEFAULT: 10
     *
     * @return the ratio between the total size of level-(L+1) files and
     * the total size of level-L files for all L.
     */
    fun maxBytesForLevelMultiplier(): Double

    /**
     * Amount of data to build up in memory (backed by an unsorted log
     * on disk) before converting to a sorted on-disk file.
     *
     * Larger values increase performance, especially during bulk loads.
     * Up to `max_write_buffer_number` write buffers may be held in memory
     * at the same time, so you may wish to adjust this parameter
     * to control memory usage.
     *
     * Also, a larger write buffer will result in a longer recovery time
     * the next time the database is opened.
     *
     * Default: 4MB
     * @param writeBufferSize the size of write buffer.
     * @return the instance of the current object.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setWriteBufferSize(writeBufferSize: Long): ColumnFamilyOptions

    /**
     * Return size of write buffer size.
     *
     * @return size of write buffer.
     * @see .setWriteBufferSize
     */
    fun writeBufferSize(): Long

    /**
     * Disable automatic compactions. Manual compactions can still
     * be issued on this column family
     *
     * @param disableAutoCompactions true if auto-compactions are disabled.
     * @return the reference to the current option.
     */
    fun setDisableAutoCompactions(
        disableAutoCompactions: Boolean
    ): ColumnFamilyOptions

    /**
     * Disable automatic compactions. Manual compactions can still
     * be issued on this column family
     *
     * @return true if auto-compactions are disabled.
     */
    fun disableAutoCompactions(): Boolean

    /**
     * Number of files to trigger level-0 compaction. A value < 0 means that
     * level-0 compaction will not be triggered by number of files at all.
     *
     * Default: 4
     *
     * @param level0FileNumCompactionTrigger The number of files to trigger
     * level-0 compaction
     * @return the reference to the current option.
     */
    fun setLevel0FileNumCompactionTrigger(
        level0FileNumCompactionTrigger: Int
    ): ColumnFamilyOptions

    /**
     * Number of files to trigger level-0 compaction. A value < 0 means that
     * level-0 compaction will not be triggered by number of files at all.
     *
     * Default: 4
     *
     * @return The number of files to trigger
     */
    fun level0FileNumCompactionTrigger(): Int

    /**
     * The upper-bound of the total size of level-1 files in bytes.
     * Maximum number of bytes for level L can be calculated as
     * (maxBytesForLevelBase) * (maxBytesForLevelMultiplier ^ (L-1))
     * For example, if maxBytesForLevelBase is 20MB, and if
     * max_bytes_for_level_multiplier is 10, total data size for level-1
     * will be 20MB, total file size for level-2 will be 200MB,
     * and total file size for level-3 will be 2GB.
     * by default 'maxBytesForLevelBase' is 10MB.
     *
     * @param maxBytesForLevelBase maximum bytes for level base.
     *
     * @return the reference to the current option.
     *
     * See [AdvancedMutableColumnFamilyOptionsInterface.setMaxBytesForLevelMultiplier]
     */
    fun setMaxBytesForLevelBase(
        maxBytesForLevelBase: Long
    ): ColumnFamilyOptions

    /**
     * The upper-bound of the total size of level-1 files in bytes.
     * Maximum number of bytes for level L can be calculated as
     * (maxBytesForLevelBase) * (maxBytesForLevelMultiplier ^ (L-1))
     * For example, if maxBytesForLevelBase is 20MB, and if
     * max_bytes_for_level_multiplier is 10, total data size for level-1
     * will be 20MB, total file size for level-2 will be 200MB,
     * and total file size for level-3 will be 2GB.
     * by default 'maxBytesForLevelBase' is 10MB.
     *
     * @return the upper-bound of the total size of level-1 files
     * in bytes.
     *
     * See [AdvancedMutableColumnFamilyOptionsInterface.maxBytesForLevelMultiplier]
     */
    fun maxBytesForLevelBase(): Long

    /**
     * Compress blocks using the specified compression algorithm.  This
     * parameter can be changed dynamically.
     *
     * Default: SNAPPY_COMPRESSION, which gives lightweight but fast compression.
     *
     * @param compressionType Compression Type.
     * @return the reference to the current option.
     */
    fun setCompressionType(
        compressionType: CompressionType
    ): ColumnFamilyOptions

    /**
     * Compress blocks using the specified compression algorithm.  This
     * parameter can be changed dynamically.
     *
     * Default: SNAPPY_COMPRESSION, which gives lightweight but fast compression.
     *
     * @return Compression type.
     */
    fun compressionType(): CompressionType

    /**
     * The maximum number of write buffers that are built up in memory.
     * The default is 2, so that when 1 write buffer is being flushed to
     * storage, new writes can continue to the other write buffer.
     * Default: 2
     *
     * @param maxWriteBufferNumber maximum number of write buffers.
     * @return the instance of the current options.
     */
    fun setMaxWriteBufferNumber(
        maxWriteBufferNumber: Int
    ): ColumnFamilyOptions

    /**
     * Returns maximum number of write buffers.
     *
     * @return maximum number of write buffers.
     * @see .setMaxWriteBufferNumber
     */
    fun maxWriteBufferNumber(): Int

    /**
     * if prefix_extractor is set and memtable_prefix_bloom_size_ratio is not 0,
     * create prefix bloom for memtable with the size of
     * write_buffer_size * memtable_prefix_bloom_size_ratio.
     * If it is larger than 0.25, it is santinized to 0.25.
     *
     * Default: 0 (disable)
     *
     * @param memtablePrefixBloomSizeRatio The ratio
     * @return the reference to the current options.
     */
    fun setMemtablePrefixBloomSizeRatio(
        memtablePrefixBloomSizeRatio: Double
    ): ColumnFamilyOptions

    /**
     * if prefix_extractor is set and memtable_prefix_bloom_size_ratio is not 0,
     * create prefix bloom for memtable with the size of
     * write_buffer_size * memtable_prefix_bloom_size_ratio.
     * If it is larger than 0.25, it is santinized to 0.25.
     *
     * Default: 0 (disable)
     *
     * @return the ratio
     */
    fun memtablePrefixBloomSizeRatio(): Double

    /**
     * Page size for huge page TLB for bloom in memtable. If  0, not allocate
     * from huge page TLB but from malloc.
     * Need to reserve huge pages for it to be allocated. For example:
     * sysctl -w vm.nr_hugepages=20
     * See linux doc Documentation/vm/hugetlbpage.txt
     *
     * @param memtableHugePageSize The page size of the huge
     * page tlb
     * @return the reference to the current options.
     */
    fun setMemtableHugePageSize(
        memtableHugePageSize: Long
    ): ColumnFamilyOptions

    /**
     * Page size for huge page TLB for bloom in memtable. If  0, not allocate
     * from huge page TLB but from malloc.
     * Need to reserve huge pages for it to be allocated. For example:
     * sysctl -w vm.nr_hugepages=20
     * See linux doc Documentation/vm/hugetlbpage.txt
     *
     * @return The page size of the huge page tlb
     */
    fun memtableHugePageSize(): Long

    /**
     * The size of one block in arena memory allocation.
     * If  0, a proper value is automatically calculated (usually 1/10 of
     * writer_buffer_size).
     *
     * There are two additional restriction of the specified size:
     * (1) size should be in the range of [4096, 2 &lt;&lt; 30] and
     * (2) be the multiple of the CPU word (which helps with the memory
     * alignment).
     *
     * We'll automatically check and adjust the size number to make sure it
     * conforms to the restrictions.
     * Default: 0
     *
     * @param arenaBlockSize the size of an arena block
     * @return the reference to the current options.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setArenaBlockSize(arenaBlockSize: Long): ColumnFamilyOptions

    /**
     * The size of one block in arena memory allocation.
     * If  0, a proper value is automatically calculated (usually 1/10 of
     * writer_buffer_size).
     *
     * There are two additional restriction of the specified size:
     * (1) size should be in the range of [4096, 2 &lt;&lt; 30] and
     * (2) be the multiple of the CPU word (which helps with the memory
     * alignment).
     *
     * We'll automatically check and adjust the size number to make sure it
     * conforms to the restrictions.
     * Default: 0
     *
     * @return the size of an arena block
     */
    fun arenaBlockSize(): Long

    /**
     * Soft limit on number of level-0 files. We start slowing down writes at this
     * point. A value &lt; 0 means that no writing slow down will be triggered by
     * number of files in level-0.
     *
     * @param level0SlowdownWritesTrigger The soft limit on the number of
     * level-0 files
     * @return the reference to the current options.
     */
    fun setLevel0SlowdownWritesTrigger(
        level0SlowdownWritesTrigger: Int
    ): ColumnFamilyOptions

    /**
     * Soft limit on number of level-0 files. We start slowing down writes at this
     * point. A value &lt; 0 means that no writing slow down will be triggered by
     * number of files in level-0.
     *
     * @return The soft limit on the number of
     * level-0 files
     */
    fun level0SlowdownWritesTrigger(): Int

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @param level0StopWritesTrigger The maximum number of level-0 files
     * @return the reference to the current options.
     */
    fun setLevel0StopWritesTrigger(
        level0StopWritesTrigger: Int
    ): ColumnFamilyOptions

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @return The maximum number of level-0 files
     */
    fun level0StopWritesTrigger(): Int

    /**
     * The target file size for compaction.
     * This targetFileSizeBase determines a level-1 file size.
     * Target file size for level L can be calculated by
     * targetFileSizeBase * (targetFileSizeMultiplier ^ (L-1))
     * For example, if targetFileSizeBase is 2MB and
     * target_file_size_multiplier is 10, then each file on level-1 will
     * be 2MB, and each file on level 2 will be 20MB,
     * and each file on level-3 will be 200MB.
     * by default targetFileSizeBase is 2MB.
     *
     * @param targetFileSizeBase the target size of a level-0 file.
     * @return the reference to the current options.
     *
     * @see .setTargetFileSizeMultiplier
     */
    fun setTargetFileSizeBase(
        targetFileSizeBase: Long
    ): ColumnFamilyOptions

    /**
     * The target file size for compaction.
     * This targetFileSizeBase determines a level-1 file size.
     * Target file size for level L can be calculated by
     * targetFileSizeBase * (targetFileSizeMultiplier ^ (L-1))
     * For example, if targetFileSizeBase is 2MB and
     * target_file_size_multiplier is 10, then each file on level-1 will
     * be 2MB, and each file on level 2 will be 20MB,
     * and each file on level-3 will be 200MB.
     * by default targetFileSizeBase is 2MB.
     *
     * @return the target size of a level-0 file.
     *
     * @see .targetFileSizeMultiplier
     */
    fun targetFileSizeBase(): Long

    /**
     * targetFileSizeMultiplier defines the size ratio between a
     * level-L file and level-(L+1) file.
     * By default target_file_size_multiplier is 1, meaning
     * files in different levels have the same target.
     *
     * @param multiplier the size ratio between a level-(L+1) file
     * and level-L file.
     * @return the reference to the current options.
     */
    fun setTargetFileSizeMultiplier(
        multiplier: Int
    ): ColumnFamilyOptions

    /**
     * targetFileSizeMultiplier defines the size ratio between a
     * level-(L+1) file and level-L file.
     * By default targetFileSizeMultiplier is 1, meaning
     * files in different levels have the same target.
     *
     * @return the size ratio between a level-(L+1) file and level-L file.
     */
    fun targetFileSizeMultiplier(): Int

    /**
     * An iteration-&gt;Next() sequentially skips over keys with the same
     * user-key unless this option is set. This number specifies the number
     * of keys (with the same userkey) that will be sequentially
     * skipped before a reseek is issued.
     * Default: 8
     *
     * @param maxSequentialSkipInIterations the number of keys could
     * be skipped in a iteration.
     * @return the reference to the current options.
     */
    fun setMaxSequentialSkipInIterations(
        maxSequentialSkipInIterations: Long
    ): ColumnFamilyOptions

    /**
     * An iteration-&gt;Next() sequentially skips over keys with the same
     * user-key unless this option is set. This number specifies the number
     * of keys (with the same userkey) that will be sequentially
     * skipped before a reseek is issued.
     * Default: 8
     *
     * @return the number of keys could be skipped in a iteration.
     */
    fun maxSequentialSkipInIterations(): Long

    /**
     * Maximum number of successive merge operations on a key in the memtable.
     *
     * When a merge operation is added to the memtable and the maximum number of
     * successive merges is reached, the value of the key will be calculated and
     * inserted into the memtable instead of the merge operation. This will
     * ensure that there are never more than max_successive_merges merge
     * operations in the memtable.
     *
     * Default: 0 (disabled)
     *
     * @param maxSuccessiveMerges the maximum number of successive merges.
     * @return the reference to the current options.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setMaxSuccessiveMerges(
        maxSuccessiveMerges: Long
    ): ColumnFamilyOptions

    /**
     * Maximum number of successive merge operations on a key in the memtable.
     *
     * When a merge operation is added to the memtable and the maximum number of
     * successive merges is reached, the value of the key will be calculated and
     * inserted into the memtable instead of the merge operation. This will
     * ensure that there are never more than max_successive_merges merge
     * operations in the memtable.
     *
     * Default: 0 (disabled)
     *
     * @return the maximum number of successive merges.
     */
    fun maxSuccessiveMerges(): Long
}
