package maryk.rocksdb

expect class ColumnFamilyOptions() : RocksObject {
    /**
     * Copy constructor for ColumnFamilyOptions.
     *
     * NOTE: This does a shallow copy, which means comparator, merge_operator, compaction_filter,
     * compaction_filter_factory and other pointers will be cloned!
     *
     * @param other The ColumnFamilyOptions to copy.
     */
    constructor(other: ColumnFamilyOptions)

    /**
     * Constructor from Options
     *
     * @param options The options.
     */
    constructor(options: Options)

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
     * Allows thread-safe inplace updates.
     * If inplace_callback function is not set,
     * Put(key, new_value) will update inplace the existing_value if
     * - key exists in current memtable
     * - new sizeof(new_value)  sizeof(existing_value)
     * - existing_value for that key is a put i.e. kTypeValue
     * If in place_callback function is set, check doc for inplace_callback.
     * Default: false.
     *
     * @param inplaceUpdateSupport true if thread-safe inplace updates
     * are allowed.
     * @return the reference to the current options.
     */
    fun setInplaceUpdateSupport(
        inplaceUpdateSupport: Boolean
    ): ColumnFamilyOptions

    /**
     * Allows thread-safe inplace updates.
     * If inplace_callback function is not set,
     * Put(key, new_value) will update inplace the existing_value iff
     * * key exists in current memtable
     * * new sizeof(new_value)  sizeof(existing_value)
     * * existing_value for that key is a put i.e. kTypeValue
     * If inplace_callback function is set, check doc for inplace_callback.
     * Default: false.
     *
     * @return true if thread-safe inplace updates are allowed.
     */
    fun inplaceUpdateSupport(): Boolean

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
     * Different levels can have different compression
     * policies. There are cases where most lower levels
     * would like to use quick compression algorithms while
     * the higher levels (which have more data) use
     * compression algorithms that have better compression
     * but could be slower. This array, if non-empty, should
     * have an entry for each level of the database;
     * these the value specified in the previous
     * field 'compression'.
     *
     * **NOTICE**
     *
     * If `level_compaction_dynamic_level_bytes=true`,
     * `compression_per_level[0]` still determines `L0`,
     * but other elements of the array are based on base level
     * (the level `L0` files are merged to), and may not
     * match the level users see from info log for metadata.
     *
     * If `L0` files are merged to `level - n`,
     * then, for `i>0`, `compression_per_level[i]`
     * determines compaction type for level `n+i-1`.
     *
     * **Example**
     *
     * For example, if we have 5 levels, and we determine to
     * merge `L0` data to `L4` (which means `L1..L3`
     * will be empty), then the new files go to `L4` uses
     * compression type `compression_per_level[1]`.
     *
     * If now `L0` is merged to `L2`. Data goes to
     * `L2` will be compressed according to
     * `compression_per_level[1]`, `L3` using
     * `compression_per_level[2]`and `L4` using
     * `compression_per_level[3]`. Compaction for each
     * level can change when data grows.
     *
     *
     * **Default:** empty
     *
     * @param compressionLevels list of
     * [maryk.rocksdb.CompressionType] instances.
     *
     * @return the reference to the current options.
     */
    fun setCompressionPerLevel(
        compressionLevels: List<CompressionType>
    ): ColumnFamilyOptions

    /**
     * Return the currently set [maryk.rocksdb.CompressionType]
     * per instances.
     *
     * See: [.setCompressionPerLevel]
     *
     * @return list of [maryk.rocksdb.CompressionType]
     * instances.
     */
    fun compressionPerLevel(): List<CompressionType>

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
     * If level [.compactionStyle] == [CompactionStyle.LEVEL],
     * for each level, which files are prioritized to be picked to compact.
     *
     * Default: [CompactionPriority.ByCompensatedSize]
     *
     * @param compactionPriority The compaction priority
     *
     * @return the reference to the current options.
     */
    fun setCompactionPriority(
        compactionPriority: CompactionPriority
    ): ColumnFamilyOptions

    /**
     * Get the Compaction priority if level compaction
     * is used for all levels
     *
     * @return The compaction priority
     */
    fun compactionPriority(): CompactionPriority

    /**
     * Set the options needed to support Universal Style compactions
     *
     * @param compactionOptionsUniversal The Universal Style compaction options
     *
     * @return the reference to the current options.
     */
    fun setCompactionOptionsUniversal(
        compactionOptionsUniversal: CompactionOptionsUniversal
    ): ColumnFamilyOptions

    /**
     * The options needed to support Universal Style compactions
     *
     * @return The Universal Style compaction options
     */
    fun compactionOptionsUniversal(): CompactionOptionsUniversal

    /**
     * The options for FIFO compaction style
     *
     * @param compactionOptionsFIFO The FIFO compaction options
     *
     * @return the reference to the current options.
     */
    fun setCompactionOptionsFIFO(
        compactionOptionsFIFO: CompactionOptionsFIFO
    ): ColumnFamilyOptions

    /**
     * The options for FIFO compaction style
     *
     * @return The FIFO compaction options
     */
    fun compactionOptionsFIFO(): CompactionOptionsFIFO

    /**
     * This flag specifies that the implementation should optimize the filters
     * mainly for cases where keys are found rather than also optimize for keys
     * missed. This would be used in cases where the application knows that
     * there are very few misses or the performance in the case of misses is not
     * important.
     *
     * For now, this flag allows us to not store filters for the last level i.e
     * the largest level which contains data of the LSM store. For keys which
     * are hits, the filters in this level are not useful because we will search
     * for the data anyway.
     *
     * **NOTE**: the filters in other levels are still useful
     * even for key hit because they tell us whether to look in that level or go
     * to the higher level.
     *
     * Default: false
     *
     * @param optimizeFiltersForHits boolean value indicating if this flag is set.
     * @return the reference to the current options.
     */
    fun setOptimizeFiltersForHits(
        optimizeFiltersForHits: Boolean
    ): ColumnFamilyOptions

    /**
     * Returns the current state of the `optimize_filters_for_hits`
     * setting.
     *
     * @return boolean value indicating if the flag
     * `optimize_filters_for_hits` was set.
     */
    fun optimizeFiltersForHits(): Boolean

    /**
     * In debug mode, RocksDB run consistency checks on the LSM every time the LSM
     * change (Flush, Compaction, AddFile). These checks are disabled in release
     * mode, use this option to enable them in release mode as well.
     *
     * Default: false
     *
     * @param forceConsistencyChecks true to force consistency checks
     *
     * @return the reference to the current options.
     */
    fun setForceConsistencyChecks(
        forceConsistencyChecks: Boolean
    ): ColumnFamilyOptions

    /**
     * In debug mode, RocksDB run consistency checks on the LSM every time the LSM
     * change (Flush, Compaction, AddFile). These checks are disabled in release
     * mode.
     *
     * @return true if consistency checks are enforced
     */
    fun forceConsistencyChecks(): Boolean


    /**
     * Use this if your DB is very small (like under 1GB) and you don't want to
     * spend lots of memory for memtables.
     *
     * @return the instance of the current object.
     */
    fun optimizeForSmallDb(): ColumnFamilyOptions

    /**
     * Use this if you don't need to keep the data sorted, i.e. you'll never use
     * an iterator, only Put() and Get() API calls
     *
     * @param blockCacheSizeMb Block cache size in MB
     * @return the instance of the current object.
     */
    fun optimizeForPointLookup(blockCacheSizeMb: Long): ColumnFamilyOptions

    /**
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for level style compaction.
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @return the instance of the current object.
     */
    fun optimizeLevelStyleCompaction(): ColumnFamilyOptions

    /**
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for level style compaction.
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @param memtableMemoryBudget memory budget in bytes
     * @return the instance of the current object.
     */
    fun optimizeLevelStyleCompaction(
        memtableMemoryBudget: Long
    ): ColumnFamilyOptions

    /**
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for universal style compaction.
     *
     * Universal style compaction is focused on reducing Write Amplification
     * Factor for big data sets, but increases Space Amplification.
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @return the instance of the current object.
     */
    fun optimizeUniversalStyleCompaction(): ColumnFamilyOptions

    /**
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for universal style compaction.
     *
     * Universal style compaction is focused on reducing Write Amplification
     * Factor for big data sets, but increases Space Amplification.
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @param memtableMemoryBudget memory budget in bytes
     * @return the instance of the current object.
     */
    fun optimizeUniversalStyleCompaction(
        memtableMemoryBudget: Long
    ): ColumnFamilyOptions

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
        comparator: AbstractComparator<out AbstractSlice<*>>
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
     * @param mergeOperator [MergeOperator] instance.
     * @return the instance of the current object.
     */
    fun setMergeOperator(mergeOperator: MergeOperator): ColumnFamilyOptions

    /**
     * A single CompactionFilter instance to call into during compaction.
     * Allows an application to modify/delete a key-value during background
     * compaction.
     *
     * If the client requires a new compaction filter to be used for different
     * compaction runs, it can specify call [.setCompactionFilterFactory]
     * instead.
     *
     * The client should specify only set one of the two.
     * [.setCompactionFilter] takes precedence
     * over [.setCompactionFilterFactory]
     * if the client specifies both.
     *
     * If multithreaded compaction is being used, the supplied CompactionFilter
     * instance may be used from different threads concurrently and so should be thread-safe.
     *
     * @param compactionFilter [AbstractCompactionFilter] instance.
     * @return the instance of the current object.
     */
    fun setCompactionFilter(
        compactionFilter: AbstractCompactionFilter<out AbstractSlice<*>>
    ): ColumnFamilyOptions

    /**
     * Accessor for the CompactionFilter instance in use.
     *
     * @return  Reference to the CompactionFilter, or null if one hasn't been set.
     */
    fun compactionFilter(): AbstractCompactionFilter<out AbstractSlice<*>>

    /**
     * This is a factory that provides [AbstractCompactionFilter] objects
     * which allow an application to modify/delete a key-value during background
     * compaction.
     *
     * A new filter will be created on each compaction run.  If multithreaded
     * compaction is being used, each created CompactionFilter will only be used
     * from a single thread and so does not need to be thread-safe.
     *
     * @param compactionFilterFactory [AbstractCompactionFilterFactory] instance.
     * @return the instance of the current object.
     */
    fun setCompactionFilterFactory(
        compactionFilterFactory: AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>>
    ): ColumnFamilyOptions

    /**
     * Accessor for the CompactionFilterFactory instance in use.
     *
     * @return  Reference to the CompactionFilterFactory, or null if one hasn't been set.
     */
    fun compactionFilterFactory(): AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>>

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
     * Same as fixed length prefix extractor, except that when slice is
     * shorter than the fixed length, it will use the full key.
     *
     * @param n use the first n bytes of a key as its prefix.
     * @return the reference to the current option.
     */
    fun useCappedPrefixExtractor(n: Int): ColumnFamilyOptions

    /**
     * Number of files to trigger level-0 compaction. A value < 0 means that
     * level-0 compaction will not be triggered by number of files at all.
     * Default: 4
     *
     * @param numFiles the number of files in level-0 to trigger compaction.
     * @return the reference to the current option.
     */
    fun setLevelZeroFileNumCompactionTrigger(
        numFiles: Int
    ): ColumnFamilyOptions

    /**
     * The number of files in level 0 to trigger compaction from level-0 to
     * level-1.  A value < 0 means that level-0 compaction will not be
     * triggered by number of files at all.
     * Default: 4
     *
     * @return the number of files in level 0 to trigger compaction.
     */
    fun levelZeroFileNumCompactionTrigger(): Int

    /**
     * Soft limit on number of level-0 files. We start slowing down writes at this
     * point. A value < 0 means that no writing slow down will be triggered by
     * number of files in level-0.
     *
     * @param numFiles soft limit on number of level-0 files.
     * @return the reference to the current option.
     */
    fun setLevelZeroSlowdownWritesTrigger(
        numFiles: Int
    ): ColumnFamilyOptions

    /**
     * Soft limit on the number of level-0 files. We start slowing down writes
     * at this point. A value < 0 means that no writing slow down will be
     * triggered by number of files in level-0.
     *
     * @return the soft limit on the number of level-0 files.
     */
    fun levelZeroSlowdownWritesTrigger(): Int

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @param numFiles the hard limit of the number of level-0 files.
     * @return the reference to the current option.
     */
    fun setLevelZeroStopWritesTrigger(numFiles: Int): ColumnFamilyOptions

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @return the hard limit of the number of level-0 file.
     */
    fun levelZeroStopWritesTrigger(): Int

    /**
     * FIFO compaction option.
     * The oldest table file will be deleted
     * once the sum of table files reaches this size.
     * The default value is 1GB (1 * 1024 * 1024 * 1024).
     *
     * @param maxTableFilesSize the size limit of the total sum of table files.
     * @return the instance of the current object.
     */
    fun setMaxTableFilesSizeFIFO(
        maxTableFilesSize: Long
    ): ColumnFamilyOptions

    /**
     * FIFO compaction option.
     * The oldest table file will be deleted
     * once the sum of table files reaches this size.
     * The default value is 1GB (1 * 1024 * 1024 * 1024).
     *
     * @return the size limit of the total sum of table files.
     */
    fun maxTableFilesSizeFIFO(): Long

    /**
     * Get the config for mem-table.
     *
     * @return the mem-table config.
     */
    fun memTableConfig(): MemTableConfig

    /**
     * Set the config for mem-table.
     *
     * @param memTableConfig the mem-table config.
     * @return the instance of the current object.
     * @throws IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setMemTableConfig(memTableConfig: MemTableConfig): ColumnFamilyOptions

    /**
     * Returns the name of the current mem table representation.
     * Memtable format can be set using setTableFormatConfig.
     *
     * @return the name of the currently-used memtable factory.
     * @see .setTableFormatConfig
     */
    fun memTableFactoryName(): String

    /**
     * Get the config for table format.
     *
     * @return the table format config.
     */
    fun tableFormatConfig(): TableFormatConfig

    /**
     * Set the config for table format.
     *
     * @param config the table format config.
     * @return the reference of the current options.
     */
    fun setTableFormatConfig(config: TableFormatConfig): ColumnFamilyOptions

    /**
     * @return the name of the currently used table factory.
     */
    fun tableFactoryName(): String

    /**
     * Compression algorithm that will be used for the bottommost level that
     * contain files. If level-compaction is used, this option will only affect
     * levels after base level.
     *
     * Default: [CompressionType.DISABLE_COMPRESSION_OPTION]
     *
     * @param bottommostCompressionType  The compression type to use for the
     * bottommost level
     *
     * @return the reference of the current options.
     */
    fun setBottommostCompressionType(
        bottommostCompressionType: CompressionType
    ): ColumnFamilyOptions

    /**
     * Compression algorithm that will be used for the bottommost level that
     * contain files. If level-compaction is used, this option will only affect
     * levels after base level.
     *
     * Default: [CompressionType.DISABLE_COMPRESSION_OPTION]
     *
     * @return The compression type used for the bottommost level
     */
    fun bottommostCompressionType(): CompressionType

    /**
     * Set the options for compression algorithms used by
     * [.bottommostCompressionType] if it is enabled.
     *
     * To enable it, please see the definition of
     * [CompressionOptions].
     *
     * @param compressionOptions the bottom most compression options.
     *
     * @return the reference of the current options.
     */
    fun setBottommostCompressionOptions(
        compressionOptions: CompressionOptions
    ): ColumnFamilyOptions

    /**
     * Get the bottom most compression options.
     *
     * See [.setBottommostCompressionOptions].
     *
     * @return the bottom most compression options.
     */
    fun bottommostCompressionOptions(): CompressionOptions

    /**
     * Set the different options for compression algorithms
     *
     * @param compressionOptions The compression options
     *
     * @return the reference of the current options.
     */
    fun setCompressionOptions(
        compressionOptions: CompressionOptions
    ): ColumnFamilyOptions

    /**
     * Get the different options for compression algorithms
     *
     * @return The compression options
     */
    fun compressionOptions(): CompressionOptions

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
     * We try to limit number of bytes in one compaction to be lower than this
     * threshold. But it's not guaranteed.
     * Value 0 will be sanitized.
     *
     * @param maxCompactionBytes max bytes in a compaction
     * @return the reference to the current option.
     * @see .maxCompactionBytes
     */
    fun setMaxCompactionBytes(maxCompactionBytes: Long): ColumnFamilyOptions

    /**
     * We try to limit number of bytes in one compaction to be lower than this
     * threshold. But it's not guaranteed.
     * Value 0 will be sanitized.
     *
     * @return the maximum number of bytes in for a compaction.
     * @see .setMaxCompactionBytes
     */
    fun maxCompactionBytes(): Long

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
     * If {@code true}, RocksDB will pick target size of each level
     * dynamically. We will pick a base level b >= 1. L0 will be
     * directly merged into level b, instead of always into level 1.
     * Level 1 to b-1 need to be empty. We try to pick b and its target
     * size so that
     *
     * 1. target size is in the range of
     *   (max_bytes_for_level_base / max_bytes_for_level_multiplier,
     *    max_bytes_for_level_base]
     * 2. target size of the last level (level num_levels-1) equals to extra size
     *    of the level.
     *
     * At the same time max_bytes_for_level_multiplier and
     * max_bytes_for_level_multiplier_additional are still satisfied.
     *
     * With this option on, from an empty DB, we make last level the base
     * level, which means merging L0 data into the last level, until it exceeds
     * max_bytes_for_level_base. And then we make the second last level to be
     * base level, to start to merge L0 data to second last level, with its
     * target size to be {@code 1/max_bytes_for_level_multiplier} of the last
     * levels extra size. After the data accumulates more so that we need to
     * move the base level to the third last one, and so on.
     *
     * #Example
     * For example, assume {@code max_bytes_for_level_multiplier=10},
     * {@code num_levels=6}, and {@code max_bytes_for_level_base=10MB}.
     *
     * Target sizes of level 1 to 5 starts with:
     * {@code [- - - - 10MB]}
     * with base level is level. Target sizes of level 1 to 4 are not applicable
     * because they will not be used.
     * Until the size of Level 5 grows to more than 10MB, say 11MB, we make
     * base target to level 4 and now the targets looks like:</p>
     * {@code [- - - 1.1MB 11MB]}
     * While data are accumulated, size targets are tuned based on actual data
     * of level 5. When level 5 has 50MB of data, the target is like:
     * {@code [- - - 5MB 50MB]}
     * Until level 5's actual size is more than 100MB, say 101MB. Now if we
     * keep level 4 to be the base level, its target size needs to be 10.1MB,
     * which doesn't satisfy the target size range. So now we make level 3
     * the target size and the target sizes of the levels look like:
     * {@code [- - 1.01MB 10.1MB 101MB]}
     * In the same way, while level 5 further grows, all levels' targets grow,
     * like
     * {@code [- - 5MB 50MB 500MB]}
     * Until level 5 exceeds 1000MB and becomes 1001MB, we make level 2 the
     * base level and make levels' target sizes like this:
     * {@code [- 1.001MB 10.01MB 100.1MB 1001MB]}
     * and go on...
     *
     * By doing it, we give {@code max_bytes_for_level_multiplier} a priority
     * against {@code max_bytes_for_level_base}, for a more predictable LSM tree
     * shape. It is useful to limit worse case space amplification.
     *
     * {@code max_bytes_for_level_multiplier_additional} is ignored with
     * this flag on.
     *
     * Turning this feature on or off for an existing DB can cause unexpected
     * LSM tree structure so it's not recommended.
     *
     * *Caution*: this option is experimental</p>
     *
     * Default: false
     *
     * @param enableLevelCompactionDynamicLevelBytes boolean value indicating
     *     if {@code LevelCompactionDynamicLevelBytes} shall be enabled.
     * @return the reference to the current options.
     */
    //@Experimental("Turning this feature on or off for an existing DB can cause unexpected LSM tree structure so it's not recommended")
    fun setLevelCompactionDynamicLevelBytes(
        enableLevelCompactionDynamicLevelBytes: Boolean
    ): ColumnFamilyOptions

    /**
     * Return if {@code LevelCompactionDynamicLevelBytes} is enabled.
     *
     * For further information see
     * {@link #setLevelCompactionDynamicLevelBytes(boolean)}
     *
     * @return boolean value indicating if {@code levelCompactionDynamicLevelBytes} is enabled.
     */
    // @Experimental("Caution: this option is experimental")
    fun levelCompactionDynamicLevelBytes(): Boolean

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
     * Number of locks used for inplace update
     * Default: 10000, if inplace_update_support = true, else 0.
     *
     * @param inplaceUpdateNumLocks the number of locks used for
     * inplace updates.
     * @return the reference to the current options.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setInplaceUpdateNumLocks(
        inplaceUpdateNumLocks: Long
    ): ColumnFamilyOptions

    /**
     * Number of locks used for inplace update
     * Default: 10000, if inplace_update_support = true, else 0.
     *
     * @return the number of locks used for inplace update.
     */
    fun inplaceUpdateNumLocks(): Long

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
     * The ratio between the total size of level-(L+1) files and the total
     * size of level-L files for all L.
     * DEFAULT: 10
     *
     * @param multiplier the ratio between the total size of level-(L+1)
     * files and the total size of level-L files for all L.
     * @return the reference to the current options.
     *
     * See [MutableColumnFamilyOptionsInterface.setMaxBytesForLevelBase]
     */
    fun setMaxBytesForLevelMultiplier(multiplier: Double): ColumnFamilyOptions

    /**
     * The ratio between the total size of level-(L+1) files and the total
     * size of level-L files for all L.
     * DEFAULT: 10
     *
     * @return the ratio between the total size of level-(L+1) files and
     * the total size of level-L files for all L.
     *
     * See [MutableColumnFamilyOptionsInterface.maxBytesForLevelBase]
     */
    fun maxBytesForLevelMultiplier(): Double

    /**
     * Different max-size multipliers for different levels.
     * These are multiplied by max_bytes_for_level_multiplier to arrive
     * at the max-size of each level.
     *
     * Default: 1
     *
     * @param maxBytesForLevelMultiplierAdditional The max-size multipliers
     * for each level
     * @return the reference to the current options.
     */
    fun setMaxBytesForLevelMultiplierAdditional(
        maxBytesForLevelMultiplierAdditional: IntArray
    ): ColumnFamilyOptions

    /**
     * Different max-size multipliers for different levels.
     * These are multiplied by max_bytes_for_level_multiplier to arrive
     * at the max-size of each level.
     *
     * Default: 1
     *
     * @return The max-size multipliers for each level
     */
    fun maxBytesForLevelMultiplierAdditional(): IntArray

    /**
     * All writes will be slowed down to at least delayed_write_rate if estimated
     * bytes needed to be compaction exceed this threshold.
     *
     * Default: 64GB
     *
     * @param softPendingCompactionBytesLimit The soft limit to impose on
     * compaction
     * @return the reference to the current options.
     */
    fun setSoftPendingCompactionBytesLimit(
        softPendingCompactionBytesLimit: Long
    ): ColumnFamilyOptions

    /**
     * All writes will be slowed down to at least delayed_write_rate if estimated
     * bytes needed to be compaction exceed this threshold.
     *
     * Default: 64GB
     *
     * @return The soft limit to impose on compaction
     */
    fun softPendingCompactionBytesLimit(): Long

    /**
     * All writes are stopped if estimated bytes needed to be compaction exceed
     * this threshold.
     *
     * Default: 256GB
     *
     * @param hardPendingCompactionBytesLimit The hard limit to impose on
     * compaction
     * @return the reference to the current options.
     */
    fun setHardPendingCompactionBytesLimit(
        hardPendingCompactionBytesLimit: Long
    ): ColumnFamilyOptions

    /**
     * All writes are stopped if estimated bytes needed to be compaction exceed
     * this threshold.
     *
     * Default: 256GB
     *
     * @return The hard limit to impose on compaction
     */
    fun hardPendingCompactionBytesLimit(): Long

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

    /**
     * After writing every SST file, reopen it and read all the keys.
     *
     * Default: false
     *
     * @param paranoidFileChecks true to enable paranoid file checks
     * @return the reference to the current options.
     */
    fun setParanoidFileChecks(
        paranoidFileChecks: Boolean
    ): ColumnFamilyOptions

    /**
     * After writing every SST file, reopen it and read all the keys.
     *
     * Default: false
     *
     * @return true if paranoid file checks are enabled
     */
    fun paranoidFileChecks(): Boolean

    /**
     * Measure IO stats in compactions and flushes, if true.
     *
     * Default: false
     *
     * @param reportBgIoStats true to enable reporting
     * @return the reference to the current options.
     */
    fun setReportBgIoStats(
        reportBgIoStats: Boolean
    ): ColumnFamilyOptions

    /**
     * Determine whether IO stats in compactions and flushes are being measured
     *
     * @return true if reporting is enabled
     */
    fun reportBgIoStats(): Boolean

    /**
     * Non-bottom-level files older than TTL will go through the compaction
     * process. This needs [MutableDBOptionsInterface.maxOpenFiles] to be
     * set to -1.
     *
     * Enabled only for level compaction for now.
     *
     * Default: 0 (disabled)
     *
     * Dynamically changeable through
     * [RocksDB.setOptions].
     *
     * @param ttl the time-to-live.
     *
     * @return the reference to the current options.
     */
    fun setTtl(ttl: Long): ColumnFamilyOptions

    /**
     * Get the TTL for Non-bottom-level files that will go through the compaction
     * process.
     *
     * See [.setTtl].
     *
     * @return the time-to-live.
     */
    fun ttl(): Long
}
