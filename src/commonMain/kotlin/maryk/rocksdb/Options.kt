package maryk.rocksdb

expect class Options() : RocksObject {
    /**
     * Construct options for opening a RocksDB. Reusing database options
     * and column family options.
     *
     * @param dbOptions [org.rocksdb.DBOptions] instance
     * @param columnFamilyOptions [org.rocksdb.ColumnFamilyOptions]
     * instance
     */
    constructor(
        dbOptions: DBOptions,
        columnFamilyOptions: ColumnFamilyOptions
    )

    /**
     * Copy constructor for ColumnFamilyOptions.
     *
     * NOTE: This does a shallow copy, which means comparator, merge_operator
     * and other pointers will be cloned!
     *
     * @param other The Options to copy.
     */
    constructor(other: Options)

    /**
     * Specifies the maximum number of concurrent background jobs (both flushes
     * and compactions combined).
     * Default: 2
     *
     * @param maxBackgroundJobs number of max concurrent background jobs
     * @return the instance of the current object.
     */
    fun setMaxBackgroundJobs(maxBackgroundJobs: Int): Options

    /**
     * Returns the maximum number of concurrent background jobs (both flushes
     * and compactions combined).
     * Default: 2
     *
     * @return the maximum number of concurrent background jobs.
     */
    fun maxBackgroundJobs(): Int

    /**
     * Suggested number of concurrent background compaction jobs, submitted to
     * the default LOW priority thread pool.
     * Default: 1
     *
     * @return Suggested number of background compaction jobs
     */
    fun baseBackgroundCompactions(): Int

    /**
     * Specifies the maximum number of concurrent background compaction jobs,
     * submitted to the default LOW priority thread pool.
     * If you're increasing this, also consider increasing number of threads in
     * LOW priority thread pool. For more information, see
     * Default: 1
     *
     * @param maxBackgroundCompactions the maximum number of background
     * compaction jobs.
     * @return the instance of the current object.
     *
     * @see RocksEnv.setBackgroundThreads
     * @see RocksEnv.setBackgroundThreads
     * @see DBOptionsInterface.maxBackgroundFlushes
     */
    fun setMaxBackgroundCompactions(maxBackgroundCompactions: Int): Options

    /**
     * By default RocksDB will flush all memtables on DB close if there are
     * unpersisted data (i.e. with WAL disabled) The flush can be skip to speedup
     * DB close. Unpersisted data WILL BE LOST.
     *
     * DEFAULT: false
     *
     * Dynamically changeable through
     * [RocksDB.setOptions]
     * API.
     *
     * @param avoidFlushDuringShutdown true if we should avoid flush during
     * shutdown
     *
     * @return the reference to the current options.
     */
    fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): Options

    /**
     * By default RocksDB will flush all memtables on DB close if there are
     * unpersisted data (i.e. with WAL disabled) The flush can be skip to speedup
     * DB close. Unpersisted data WILL BE LOST.
     *
     * DEFAULT: false
     *
     * Dynamically changeable through
     * [RocksDB.setOptions]
     * API.
     *
     * @return true if we should avoid flush during shutdown
     */
    fun avoidFlushDuringShutdown(): Boolean

    /**
     * This is the maximum buffer size that is used by WritableFileWriter.
     * On Windows, we need to maintain an aligned buffer for writes.
     * We allow the buffer to grow until it's size hits the limit.
     *
     * Default: 1024 * 1024 (1 MB)
     *
     * @param writableFileMaxBufferSize the maximum buffer size
     *
     * @return the reference to the current options.
     */
    fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): Options

    /**
     * This is the maximum buffer size that is used by WritableFileWriter.
     * On Windows, we need to maintain an aligned buffer for writes.
     * We allow the buffer to grow until it's size hits the limit.
     *
     * Default: 1024 * 1024 (1 MB)
     *
     * @return the maximum buffer size
     */
    fun writableFileMaxBufferSize(): Long

    /**
     * The limited write rate to DB if
     * [ColumnFamilyOptions.softPendingCompactionBytesLimit] or
     * [ColumnFamilyOptions.level0SlowdownWritesTrigger] is triggered,
     * or we are writing to the last mem table allowed and we allow more than 3
     * mem tables. It is calculated using size of user write requests before
     * compression. RocksDB may decide to slow down more if the compaction still
     * gets behind further.
     *
     * Unit: bytes per second.
     *
     * Default: 16MB/s
     *
     * @param delayedWriteRate the rate in bytes per second
     *
     * @return the reference to the current options.
     */
    fun setDelayedWriteRate(delayedWriteRate: Long): Options

    /**
     * The limited write rate to DB if
     * [ColumnFamilyOptions.softPendingCompactionBytesLimit] or
     * [ColumnFamilyOptions.level0SlowdownWritesTrigger] is triggered,
     * or we are writing to the last mem table allowed and we allow more than 3
     * mem tables. It is calculated using size of user write requests before
     * compression. RocksDB may decide to slow down more if the compaction still
     * gets behind further.
     *
     * Unit: bytes per second.
     *
     * Default: 16MB/s
     *
     * @return the rate in bytes per second
     */
    fun delayedWriteRate(): Long

    /**
     * Once write-ahead logs exceed this size, we will start forcing the
     * flush of column families whose memtables are backed by the oldest live
     * WAL file (i.e. the ones that are causing all the space amplification).
     *
     * If set to 0 (default), we will dynamically choose the WAL size limit to
     * be [sum of all write_buffer_size * max_write_buffer_number] * 2
     *
     * This option takes effect only when there are more than one column family as
     * otherwise the wal size is dictated by the write_buffer_size.
     *
     * Default: 0
     *
     * @param maxTotalWalSize max total wal size.
     * @return the instance of the current object.
     */
    fun setMaxTotalWalSize(maxTotalWalSize: Long): Options

    /**
     * Returns the max total wal size. Once write-ahead logs exceed this size,
     * we will start forcing the flush of column families whose memtables are
     * backed by the oldest live WAL file (i.e. the ones that are causing all
     * the space amplification).
     *
     * If set to 0 (default), we will dynamically choose the WAL size limit
     * to be [sum of all write_buffer_size * max_write_buffer_number] * 2
     *
     * @return max total wal size
     */
    fun maxTotalWalSize(): Long

    /**
     * if not zero, dump rocksdb.stats to LOG every stats_dump_period_sec
     * Default: 600 (10 minutes)
     *
     * @param statsDumpPeriodSec time interval in seconds.
     * @return the instance of the current object.
     */
    fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): Options

    /**
     * If not zero, dump rocksdb.stats to LOG every stats_dump_period_sec
     * Default: 600 (10 minutes)
     *
     * @return time interval in seconds.
     */
    fun statsDumpPeriodSec(): Int

    /**
     * Number of open files that can be used by the DB.  You may need to
     * increase this if your database has a large working set. Value -1 means
     * files opened are always kept open. You can estimate number of files based
     * on `target_file_size_base` and `target_file_size_multiplier`
     * for level-based compaction. For universal-style compaction, you can usually
     * set it to -1.
     * Default: 5000
     *
     * @param maxOpenFiles the maximum number of open files.
     * @return the instance of the current object.
     */
    fun setMaxOpenFiles(maxOpenFiles: Int): Options

    /**
     * Number of open files that can be used by the DB.  You may need to
     * increase this if your database has a large working set. Value -1 means
     * files opened are always kept open. You can estimate number of files based
     * on `target_file_size_base` and `target_file_size_multiplier`
     * for level-based compaction. For universal-style compaction, you can usually
     * set it to -1.
     *
     * @return the maximum number of open files.
     */
    fun maxOpenFiles(): Int

    /**
     * Allows OS to incrementally sync files to disk while they are being
     * written, asynchronously, in the background.
     * Issue one request for every bytes_per_sync written. 0 turns it off.
     * Default: 0
     *
     * @param bytesPerSync size in bytes
     * @return the instance of the current object.
     */
    fun setBytesPerSync(bytesPerSync: Long): Options

    /**
     * Allows OS to incrementally sync files to disk while they are being
     * written, asynchronously, in the background.
     * Issue one request for every bytes_per_sync written. 0 turns it off.
     * Default: 0
     *
     * @return size in bytes
     */
    fun bytesPerSync(): Long

    /**
     * Same as [.setBytesPerSync] , but applies to WAL files
     *
     * Default: 0, turned off
     *
     * @param walBytesPerSync size in bytes
     * @return the instance of the current object.
     */
    fun setWalBytesPerSync(walBytesPerSync: Long): Options

    /**
     * Same as [.bytesPerSync] , but applies to WAL files
     *
     * Default: 0, turned off
     *
     * @return size in bytes
     */
    fun walBytesPerSync(): Long

    /**
     * If non-zero, we perform bigger reads when doing compaction. If you're
     * running RocksDB on spinning disks, you should set this to at least 2MB.
     *
     * That way RocksDB's compaction is doing sequential instead of random reads.
     * When non-zero, we also force
     * [DBOptionsInterface.newTableReaderForCompactionInputs] to true.
     *
     * Default: 0
     *
     * @param compactionReadaheadSize The compaction read-ahead size
     *
     * @return the reference to the current options.
     */
    fun setCompactionReadaheadSize(compactionReadaheadSize: Long): Options

    /**
     * If non-zero, we perform bigger reads when doing compaction. If you're
     * running RocksDB on spinning disks, you should set this to at least 2MB.
     *
     * That way RocksDB's compaction is doing sequential instead of random reads.
     * When non-zero, we also force
     * [DBOptionsInterface.newTableReaderForCompactionInputs] to true.
     *
     * Default: 0
     *
     * @return The compaction read-ahead size
     */
    fun compactionReadaheadSize(): Long

    /**
     * If this value is set to true, then the database will be created
     * if it is missing during {@code RocksDB.open()}.
     * Default: false
     * @return the instance of the current Options
     */
    fun setCreateIfMissing(flag: Boolean): Options

    /**
     * The maximum number of write buffers that are built up in memory.
     * The default is 2, so that when 1 write buffer is being flushed to
     * storage, new writes can continue to the other write buffer.
     * Default: 2
     *
     * @param maxWriteBufferNumber maximum number of write buffers.
     * @return the instance of the current options.
     */
    fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): Options

    /**
     * Set the merge operator to be used for merging two different key/value
     * pairs that share the same key. The merge function is invoked during
     * compaction and at lookup time, if multiple key/value pairs belonging
     * to the same key are found in the database.
     *
     * @param mergeOperator [MergeOperator] instance.
     * @return the instance of the current object.
     */
    fun setMergeOperator(mergeOperator: MergeOperator): Options

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
    ): Options

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
    ): Options

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
     * Put(key, new_value) will update inplace the existing_value iff
     * * key exists in current memtable
     * * new sizeof(new_value)  sizeof(existing_value)
     * * existing_value for that key is a put i.e. kTypeValue
     * If inplace_callback function is set, check doc for inplace_callback.
     * Default: false.
     *
     * @param inplaceUpdateSupport true if thread-safe inplace updates
     * are allowed.
     * @return the reference to the current options.
     */
    fun setInplaceUpdateSupport(
        inplaceUpdateSupport: Boolean
    ): Options

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
    fun setBloomLocality(bloomLocality: Int): Options

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
     * these override the value specified in the previous
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
     * then, for `i&gt;0`, `compression_per_level[i]`
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
     * **Default:** empty
     *
     * @param compressionLevels list of
     * [org.rocksdb.CompressionType] instances.
     *
     * @return the reference to the current options.
     */
    fun setCompressionPerLevel(
        compressionLevels: List<CompressionType>
    ): Options

    /**
     * Return the currently set [org.rocksdb.CompressionType]
     * per instances.
     *
     * See: [.setCompressionPerLevel]
     *
     * @return list of [org.rocksdb.CompressionType]
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
    fun setNumLevels(numLevels: Int): Options

    /**
     * If level-styled compaction is used, then this number determines
     * the total number of levels.
     *
     * @return the number of levels.
     */
    fun numLevels(): Int

    /**
     * If `true`, RocksDB will pick target size of each level
     * dynamically. We will pick a base level b &gt;= 1. L0 will be
     * directly merged into level b, instead of always into level 1.
     * Level 1 to b-1 need to be empty. We try to pick b and its target
     * size so that
     *
     *  1. target size is in the range of
     * (max_bytes_for_level_base / max_bytes_for_level_multiplier,
     * max_bytes_for_level_base]
     *  1. target size of the last level (level num_levels-1) equals to extra size
     * of the level.
     *
     * At the same time max_bytes_for_level_multiplier and
     * max_bytes_for_level_multiplier_additional are still satisfied.
     *
     * With this option on, from an empty DB, we make last level the base
     * level, which means merging L0 data into the last level, until it exceeds
     * max_bytes_for_level_base. And then we make the second last level to be
     * base level, to start to merge L0 data to second last level, with its
     * target size to be `1/max_bytes_for_level_multiplier` of the last
     * levels extra size. After the data accumulates more so that we need to
     * move the base level to the third last one, and so on.
     *
     * *Example*
     *
     * For example, assume `max_bytes_for_level_multiplier=10`,
     * `num_levels=6`, and `max_bytes_for_level_base=10MB`.
     *
     * Target sizes of level 1 to 5 starts with:
     * `[- - - - 10MB]`
     *
     * with base level is level. Target sizes of level 1 to 4 are not applicable
     * because they will not be used.
     * Until the size of Level 5 grows to more than 10MB, say 11MB, we make
     * base target to level 4 and now the targets looks like:
     * `[- - - 1.1MB 11MB]`
     *
     * While data are accumulated, size targets are tuned based on actual data
     * of level 5. When level 5 has 50MB of data, the target is like:
     * `[- - - 5MB 50MB]`
     *
     * Until level 5's actual size is more than 100MB, say 101MB. Now if we
     * keep level 4 to be the base level, its target size needs to be 10.1MB,
     * which doesn't satisfy the target size range. So now we make level 3
     * the target size and the target sizes of the levels look like:
     * `[- - 1.01MB 10.1MB 101MB]`
     *
     * In the same way, while level 5 further grows, all levels' targets grow,
     * like
     * `[- - 5MB 50MB 500MB]`
     *
     * Until level 5 exceeds 1000MB and becomes 1001MB, we make level 2 the
     * base level and make levels' target sizes like this:
     * `[- 1.001MB 10.01MB 100.1MB 1001MB]`
     *
     * and go on...
     *
     * By doing it, we give `max_bytes_for_level_multiplier` a priority
     * against `max_bytes_for_level_base`, for a more predictable LSM tree
     * shape. It is useful to limit worse case space amplification.
     *
     * `max_bytes_for_level_multiplier_additional` is ignored with
     * this flag on.
     *
     * Turning this feature on or off for an existing DB can cause unexpected
     * LSM tree structure so it's not recommended.
     *
     * **Caution**: this option is experimental
     *
     * Default: false
     *
     * @param enableLevelCompactionDynamicLevelBytes boolean value indicating
     * if `LevelCompactionDynamicLevelBytes` shall be enabled.
     * @return the reference to the current options.
     *
     * @Experimental("Turning this feature on or off for an existing DB can cause unexpected LSM tree structure so it's not recommended")
     */
    fun setLevelCompactionDynamicLevelBytes(enableLevelCompactionDynamicLevelBytes: Boolean): Options

    /**
     * Return if `LevelCompactionDynamicLevelBytes` is enabled.
     *
     * For further information see
     * [.setLevelCompactionDynamicLevelBytes]
     *
     * @return boolean value indicating if
     * `levelCompactionDynamicLevelBytes` is enabled.
     *
     * @Experimental("Caution: this option is experimental")
     */
    fun levelCompactionDynamicLevelBytes(): Boolean


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
    ): Options

    /**
     * Compaction style for DB.
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
    fun setCompactionPriority(compactionPriority: CompactionPriority): Options

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
    fun setCompactionOptionsUniversal(compactionOptionsUniversal: CompactionOptionsUniversal): Options

    /**
     * The options needed to support Universal Style compactions
     */
    fun compactionOptionsUniversal(): CompactionOptionsUniversal

    /**
     * The options for FIFO compaction style
     *
     * @param compactionOptionsFIFO The FIFO compaction options
     *
     * @return the reference to the current options.
     */
    fun setCompactionOptionsFIFO(compactionOptionsFIFO: CompactionOptionsFIFO): Options

    /**
     * The options for FIFO compaction style
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
    ): Options

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
    ): Options

    /**
     * In debug mode, RocksDB run consistency checks on the LSM every time the LSM
     * change (Flush, Compaction, AddFile). These checks are disabled in release
     * mode.
     *
     * @return true if consistency checks are enforced
     */
    fun forceConsistencyChecks(): Boolean


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
    fun setWriteBufferSize(writeBufferSize: Long): Options

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
    ): Options

    /**
     * Disable automatic compactions. Manual compactions can still
     * be issued on this column family
     *
     * @return true if auto-compactions are disabled.
     */
    fun disableAutoCompactions(): Boolean

    /**
     * Number of files to trigger level-0 compaction. A value &lt; 0 means that
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
    ): Options

    /**
     * Number of files to trigger level-0 compaction. A value &lt; 0 means that
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
    fun setMaxCompactionBytes(maxCompactionBytes: Long): Options

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
    ): Options

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
    ): Options

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
     * Use this if your DB is very small (like under 1GB) and you don't want to
     * spend lots of memory for memtables.
     *
     * @return the instance of the current object.
     */
    fun optimizeForSmallDb(): Options

    /**
     * Use this if you don't need to keep the data sorted, i.e. you'll never use
     * an iterator, only Put() and Get() API calls
     *
     * @param blockCacheSizeMb Block cache size in MB
     * @return the instance of the current object.
     */
    fun optimizeForPointLookup(blockCacheSizeMb: Long): Options

    /**
     *
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for level style compaction.
     *
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @return the instance of the current object.
     */
    fun optimizeLevelStyleCompaction(): Options

    /**
     *
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for level style compaction.
     *
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
    ): Options

    /**
     *
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for universal style compaction.
     *
     *
     * Universal style compaction is focused on reducing Write Amplification
     * Factor for big data sets, but increases Space Amplification.
     *
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @return the instance of the current object.
     */
    fun optimizeUniversalStyleCompaction(): Options

    /**
     *
     * Default values for some parameters in ColumnFamilyOptions are not
     * optimized for heavy workloads and big datasets, which means you might
     * observe write stalls under some conditions. As a starting point for tuning
     * RocksDB options, use the following for universal style compaction.
     *
     *
     * Universal style compaction is focused on reducing Write Amplification
     * Factor for big data sets, but increases Space Amplification.
     *
     *
     * Make sure to also call IncreaseParallelism(), which will provide the
     * biggest performance gains.
     *
     *
     * Note: we might use more memory than memtable_memory_budget during high
     * write rate period
     *
     * @param memtableMemoryBudget memory budget in bytes
     * @return the instance of the current object.
     */
    fun optimizeUniversalStyleCompaction(
        memtableMemoryBudget: Long
    ): Options

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
    ): Options

    /**
     * Use the specified comparator for key ordering.
     *
     * Comparator should not be disposed before options instances using this comparator is
     * disposed. If dispose() function is not called, then comparator object will be
     * GC'd automatically.
     *
     * Comparator instance can be re-used in multiple options instances.
     *
     * @param comparator java instance.
     * @return the instance of the current object.
     */
    fun setComparator(
        comparator: AbstractComparator<out AbstractSlice<*>>
    ): Options

    /**
     *
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
    fun setMergeOperatorName(name: String): Options

    /**
     * A single CompactionFilter instance to call into during compaction.
     * Allows an application to modify/delete a key-value during background
     * compaction.
     *
     * If the client requires a new compaction filter to be used for different
     * compaction runs, it can specify call
     * [.setCompactionFilterFactory]
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
    ): Options

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
    ): Options

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
    fun useFixedLengthPrefixExtractor(n: Int): Options

    /**
     * Same as fixed length prefix extractor, except that when slice is
     * shorter than the fixed length, it will use the full key.
     *
     * @param n use the first n bytes of a key as its prefix.
     * @return the reference to the current option.
     */
    fun useCappedPrefixExtractor(n: Int): Options

    /**
     * Number of files to trigger level-0 compaction. A value &lt; 0 means that
     * level-0 compaction will not be triggered by number of files at all.
     * Default: 4
     *
     * @param numFiles the number of files in level-0 to trigger compaction.
     * @return the reference to the current option.
     */
    fun setLevelZeroFileNumCompactionTrigger(
        numFiles: Int
    ): Options

    /**
     * The number of files in level 0 to trigger compaction from level-0 to
     * level-1.  A value &lt; 0 means that level-0 compaction will not be
     * triggered by number of files at all.
     * Default: 4
     *
     * @return the number of files in level 0 to trigger compaction.
     */
    fun levelZeroFileNumCompactionTrigger(): Int

    /**
     * Soft limit on number of level-0 files. We start slowing down writes at this
     * point. A value &lt; 0 means that no writing slow down will be triggered by
     * number of files in level-0.
     *
     * @param numFiles soft limit on number of level-0 files.
     * @return the reference to the current option.
     */
    fun setLevelZeroSlowdownWritesTrigger(
        numFiles: Int
    ): Options

    /**
     * Soft limit on the number of level-0 files. We start slowing down writes
     * at this point. A value &lt; 0 means that no writing slow down will be
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
    fun setLevelZeroStopWritesTrigger(numFiles: Int): Options

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @return the hard limit of the number of level-0 file.
     */
    fun levelZeroStopWritesTrigger(): Int

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
    ): Options

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
    ): Options

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
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setMemTableConfig(memTableConfig: MemTableConfig): Options

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
    fun setTableFormatConfig(config: TableFormatConfig): Options

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
    ): Options

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
    fun setBottommostCompressionOptions(compressionOptions: CompressionOptions): Options

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
    ): Options

    /**
     * Get the different options for compression algorithms
     *
     * @return The compression options
     */
    fun compressionOptions(): CompressionOptions

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
    ): Options

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
    ): Options

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
    ): Options

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
    fun setArenaBlockSize(arenaBlockSize: Long): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    ): Options

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
    fun setTtl(ttl: Long): Options

    /**
     * Get the TTL for Non-bottom-level files that will go through the compaction
     * process.
     *
     * See [.setTtl].
     *
     * @return the time-to-live.
     */
    fun ttl(): Long

    /**
     * Use the specified object to interact with the environment,
     * e.g. to read/write files, schedule background work, etc.
     * Default: [Env.getDefault]
     *
     * @param env [Env] instance.
     * @return the instance of the current Options.
     */
    fun setEnv(env: Env): Options

    /**
     * Returns the set RocksEnv instance.
     *
     * @return [RocksEnv] instance set in the options.
     */
    fun getEnv(): Env

    /**
     *
     * By default, RocksDB uses only one background thread for flush and
     * compaction. Calling this function will set it up such that total of
     * `total_threads` is used.
     *
     *
     * You almost definitely want to call this function if your system is
     * bottlenecked by RocksDB.
     *
     * @param totalThreads The total number of threads to be used by RocksDB.
     * A good value is the number of cores.
     *
     * @return the instance of the current Options
     */
    fun setIncreaseParallelism(totalThreads: Int): Options

    /**
     * Return true if the create_if_missing flag is set to true.
     * If true, the database will be created if it is missing.
     *
     * @return true if the createIfMissing option is set to true.
     * @see .setCreateIfMissing
     */
    fun createIfMissing(): Boolean

    /**
     *
     * If true, missing column families will be automatically created
     *
     *
     * Default: false
     *
     * @param flag a flag indicating if missing column families shall be
     * created automatically.
     * @return true if missing column families shall be created automatically
     * on open.
     */
    fun setCreateMissingColumnFamilies(flag: Boolean): Options

    /**
     * Return true if the create_missing_column_families flag is set
     * to true. If true column families be created if missing.
     *
     * @return true if the createMissingColumnFamilies is set to
     * true.
     * @see .setCreateMissingColumnFamilies
     */
    fun createMissingColumnFamilies(): Boolean

    /**
     * If true, an error will be thrown during RocksDB.open() if the
     * database already exists.
     * Default: false
     *
     * @param errorIfExists if true, an exception will be thrown
     * during `RocksDB.open()` if the database already exists.
     * @return the reference to the current option.
     * @see RocksDB.open
     */
    fun setErrorIfExists(errorIfExists: Boolean): Options

    /**
     * If true, an error will be thrown during RocksDB.open() if the
     * database already exists.
     *
     * @return if true, an error is raised when the specified database
     * already exists before open.
     */
    fun errorIfExists(): Boolean

    /**
     * If true, the implementation will do aggressive checking of the
     * data it is processing and will stop early if it detects any
     * errors.  This may have unforeseen ramifications: for example, a
     * corruption of one DB entry may cause a large number of entries to
     * become unreadable or for the entire DB to become unopenable.
     * If any of the  writes to the database fails (Put, Delete, Merge, Write),
     * the database will switch to read-only mode and fail all other
     * Write operations.
     * Default: true
     *
     * @param paranoidChecks a flag to indicate whether paranoid-check
     * is on.
     * @return the reference to the current option.
     */
    fun setParanoidChecks(paranoidChecks: Boolean): Options

    /**
     * If true, the implementation will do aggressive checking of the
     * data it is processing and will stop early if it detects any
     * errors.  This may have unforeseen ramifications: for example, a
     * corruption of one DB entry may cause a large number of entries to
     * become unreadable or for the entire DB to become unopenable.
     * If any of the  writes to the database fails (Put, Delete, Merge, Write),
     * the database will switch to read-only mode and fail all other
     * Write operations.
     *
     * @return a boolean indicating whether paranoid-check is on.
     */
    fun paranoidChecks(): Boolean

    /**
     * Use to control write rate of flush and compaction. Flush has higher
     * priority than compaction. Rate limiting is disabled if nullptr.
     * Default: nullptr
     *
     * @param rateLimiter [org.rocksdb.RateLimiter] instance.
     * @return the instance of the current object.
     *
     * @since 3.10.0
     */
    fun setRateLimiter(rateLimiter: RateLimiter): Options

    /**
     * Use to track SST files and control their file deletion rate.
     *
     * Features:
     * - Throttle the deletion rate of the SST files.
     * - Keep track the total size of all SST files.
     * - Set a maximum allowed space limit for SST files that when reached
     * the DB wont do any further flushes or compactions and will set the
     * background error.
     * - Can be shared between multiple dbs.
     *
     * Limitations:
     * - Only track and throttle deletes of SST files in
     * first db_path (db_name if db_paths is empty).
     *
     * @param sstFileManager The SST File Manager for the db.
     * @return the instance of the current object.
     */
    fun setSstFileManager(sstFileManager: SstFileManager): Options

    /**
     *
     * Any internal progress/error information generated by
     * the db will be written to the Logger if it is non-nullptr,
     * or to a file stored in the same directory as the DB
     * contents if info_log is nullptr.
     *
     *
     * Default: nullptr
     *
     * @param logger [Logger] instance.
     * @return the instance of the current object.
     */
    fun setLogger(logger: Logger): Options

    /**
     *
     * Sets the RocksDB log level. Default level is INFO
     *
     * @param infoLogLevel log level to set.
     * @return the instance of the current object.
     */
    fun setInfoLogLevel(infoLogLevel: InfoLogLevel): Options

    /**
     *
     * Returns currently set log level.
     * @return [org.rocksdb.InfoLogLevel] instance.
     */
    fun infoLogLevel(): InfoLogLevel

    /**
     * If [MutableDBOptionsInterface.maxOpenFiles] is -1, DB will open
     * all files on DB::Open(). You can use this option to increase the number
     * of threads used to open the files.
     *
     * Default: 16
     *
     * @param maxFileOpeningThreads the maximum number of threads to use to
     * open files
     *
     * @return the reference to the current options.
     */
    fun setMaxFileOpeningThreads(maxFileOpeningThreads: Int): Options

    /**
     * If [MutableDBOptionsInterface.maxOpenFiles] is -1, DB will open all
     * files on DB::Open(). You can use this option to increase the number of
     * threads used to open the files.
     *
     * Default: 16
     *
     * @return the maximum number of threads to use to open files
     */
    fun maxFileOpeningThreads(): Int

    /**
     *
     * Sets the statistics object which collects metrics about database operations.
     * Statistics objects should not be shared between DB instances as
     * it does not use any locks to prevent concurrent updates.
     *
     * @param statistics The statistics to set
     *
     * @return the instance of the current object.
     *
     * @see RocksDB.open
     */
    fun setStatistics(statistics: Statistics): Options

    /**
     *
     * Returns statistics object.
     *
     * @return the instance of the statistics object or null if there is no
     * statistics object.
     *
     * @see .setStatistics
     */
    fun statistics(): Statistics

    /**
     *
     * If true, then every store to stable storage will issue a fsync.
     *
     * If false, then every store to stable storage will issue a fdatasync.
     * This parameter should be set to true while storing data to
     * filesystem like ext3 that can lose files after a reboot.
     *
     * Default: false
     *
     * @param useFsync a boolean flag to specify whether to use fsync
     * @return the instance of the current object.
     */
    fun setUseFsync(useFsync: Boolean): Options

    /**
     *
     * If true, then every store to stable storage will issue a fsync.
     *
     * If false, then every store to stable storage will issue a fdatasync.
     * This parameter should be set to true while storing data to
     * filesystem like ext3 that can lose files after a reboot.
     *
     * @return boolean value indicating if fsync is used.
     */
    fun useFsync(): Boolean

    /**
     * A list of paths where SST files can be put into, with its target size.
     * Newer data is placed into paths specified earlier in the vector while
     * older data gradually moves to paths specified later in the vector.
     *
     * For example, you have a flash device with 10GB allocated for the DB,
     * as well as a hard drive of 2TB, you should config it to be:
     * [{"/flash_path", 10GB}, {"/hard_drive", 2TB}]
     *
     * The system will try to guarantee data under each path is close to but
     * not larger than the target size. But current and future file sizes used
     * by determining where to place a file are based on best-effort estimation,
     * which means there is a chance that the actual size under the directory
     * is slightly more than target size under some workloads. User should give
     * some buffer room for those cases.
     *
     * If none of the paths has sufficient room to place a file, the file will
     * be placed to the last path anyway, despite to the target size.
     *
     * Placing newer data to earlier paths is also best-efforts. User should
     * expect user files to be placed in higher levels in some extreme cases.
     *
     * If left empty, only one path will be used, which is db_name passed when
     * opening the DB.
     *
     * Default: empty
     *
     * @param dbPaths the paths and target sizes
     *
     * @return the reference to the current options
     */
    fun setDbPaths(dbPaths: Collection<DbPath>): Options

    /**
     * A list of paths where SST files can be put into, with its target size.
     * Newer data is placed into paths specified earlier in the vector while
     * older data gradually moves to paths specified later in the vector.
     *
     * For example, you have a flash device with 10GB allocated for the DB,
     * as well as a hard drive of 2TB, you should config it to be:
     * [{"/flash_path", 10GB}, {"/hard_drive", 2TB}]
     *
     * The system will try to guarantee data under each path is close to but
     * not larger than the target size. But current and future file sizes used
     * by determining where to place a file are based on best-effort estimation,
     * which means there is a chance that the actual size under the directory
     * is slightly more than target size under some workloads. User should give
     * some buffer room for those cases.
     *
     * If none of the paths has sufficient room to place a file, the file will
     * be placed to the last path anyway, despite to the target size.
     *
     * Placing newer data to earlier paths is also best-efforts. User should
     * expect user files to be placed in higher levels in some extreme cases.
     *
     * If left empty, only one path will be used, which is db_name passed when
     * opening the DB.
     *
     * Default: [java.util.Collections.emptyList]
     *
     * @return dbPaths the paths and target sizes
     */
    fun dbPaths(): List<DbPath>

    /**
     * This specifies the info LOG dir.
     * If it is empty, the log files will be in the same dir as data.
     * If it is non empty, the log files will be in the specified dir,
     * and the db data dir's absolute path will be used as the log file
     * name's prefix.
     *
     * @param dbLogDir the path to the info log directory
     * @return the instance of the current object.
     */
    fun setDbLogDir(dbLogDir: String): Options

    /**
     * Returns the directory of info log.
     *
     * If it is empty, the log files will be in the same dir as data.
     * If it is non empty, the log files will be in the specified dir,
     * and the db data dir's absolute path will be used as the log file
     * name's prefix.
     *
     * @return the path to the info log directory
     */
    fun dbLogDir(): String

    /**
     * This specifies the absolute dir path for write-ahead logs (WAL).
     * If it is empty, the log files will be in the same dir as data,
     * dbname is used as the data dir by default
     * If it is non empty, the log files will be in kept the specified dir.
     * When destroying the db,
     * all log files in wal_dir and the dir itself is deleted
     *
     * @param walDir the path to the write-ahead-log directory.
     * @return the instance of the current object.
     */
    fun setWalDir(walDir: String): Options

    /**
     * Returns the path to the write-ahead-logs (WAL) directory.
     *
     * If it is empty, the log files will be in the same dir as data,
     * dbname is used as the data dir by default
     * If it is non empty, the log files will be in kept the specified dir.
     * When destroying the db,
     * all log files in wal_dir and the dir itself is deleted
     *
     * @return the path to the write-ahead-logs (WAL) directory.
     */
    fun walDir(): String

    /**
     * The periodicity when obsolete files get deleted. The default
     * value is 6 hours. The files that get out of scope by compaction
     * process will still get automatically delete on every compaction,
     * regardless of this setting
     *
     * @param micros the time interval in micros
     * @return the instance of the current object.
     */
    fun setDeleteObsoleteFilesPeriodMicros(micros: Long): Options

    /**
     * The periodicity when obsolete files get deleted. The default
     * value is 6 hours. The files that get out of scope by compaction
     * process will still get automatically delete on every compaction,
     * regardless of this setting
     *
     * @return the time interval in micros when obsolete files will be deleted.
     */
    fun deleteObsoleteFilesPeriodMicros(): Long

    /**
     * This value represents the maximum number of threads that will
     * concurrently perform a compaction job by breaking it into multiple,
     * smaller ones that are run simultaneously.
     * Default: 1 (i.e. no subcompactions)
     *
     * @param maxSubcompactions The maximum number of threads that will
     * concurrently perform a compaction job
     *
     * @return the instance of the current object.
     */
    fun setMaxSubcompactions(maxSubcompactions: Int): Options

    /**
     * This value represents the maximum number of threads that will
     * concurrently perform a compaction job by breaking it into multiple,
     * smaller ones that are run simultaneously.
     * Default: 1 (i.e. no subcompactions)
     *
     * @return The maximum number of threads that will concurrently perform a
     * compaction job
     */
    fun maxSubcompactions(): Int

    /**
     * Specifies the maximum number of concurrent background flush jobs.
     * If you're increasing this, also consider increasing number of threads in
     * HIGH priority thread pool. For more information, see
     * Default: 1
     *
     * @param maxBackgroundFlushes number of max concurrent flush jobs
     * @return the instance of the current object.
     *
     * @see RocksEnv.setBackgroundThreads
     * @see RocksEnv.setBackgroundThreads
     * @see MutableDBOptionsInterface.maxBackgroundCompactions
     */
    @Deprecated("Use {@link MutableDBOptionsInterface#setMaxBackgroundJobs(int)}")
    fun setMaxBackgroundFlushes(maxBackgroundFlushes: Int): Options

    /**
     * Returns the maximum number of concurrent background flush jobs.
     * If you're increasing this, also consider increasing number of threads in
     * HIGH priority thread pool. For more information, see
     * Default: 1
     *
     * @return the maximum number of concurrent background flush jobs.
     * @see RocksEnv.setBackgroundThreads
     * @see RocksEnv.setBackgroundThreads
     */
    @Deprecated("")
    fun maxBackgroundFlushes(): Int

    /**
     * Specifies the maximum size of a info log file. If the current log file
     * is larger than `max_log_file_size`, a new info log file will
     * be created.
     * If 0, all logs will be written to one log file.
     *
     * @param maxLogFileSize the maximum size of a info log file.
     * @return the instance of the current object.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setMaxLogFileSize(maxLogFileSize: Long): Options

    /**
     * Returns the maximum size of a info log file. If the current log file
     * is larger than this size, a new info log file will be created.
     * If 0, all logs will be written to one log file.
     *
     * @return the maximum size of the info log file.
     */
    fun maxLogFileSize(): Long

    /**
     * Specifies the time interval for the info log file to roll (in seconds).
     * If specified with non-zero value, log file will be rolled
     * if it has been active longer than `log_file_time_to_roll`.
     * Default: 0 (disabled)
     *
     * @param logFileTimeToRoll the time interval in seconds.
     * @return the instance of the current object.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setLogFileTimeToRoll(logFileTimeToRoll: Long): Options

    /**
     * Returns the time interval for the info log file to roll (in seconds).
     * If specified with non-zero value, log file will be rolled
     * if it has been active longer than `log_file_time_to_roll`.
     * Default: 0 (disabled)
     *
     * @return the time interval in seconds.
     */
    fun logFileTimeToRoll(): Long

    /**
     * Specifies the maximum number of info log files to be kept.
     * Default: 1000
     *
     * @param keepLogFileNum the maximum number of info log files to be kept.
     * @return the instance of the current object.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setKeepLogFileNum(keepLogFileNum: Long): Options

    /**
     * Returns the maximum number of info log files to be kept.
     * Default: 1000
     *
     * @return the maximum number of info log files to be kept.
     */
    fun keepLogFileNum(): Long

    /**
     * Recycle log files.
     *
     * If non-zero, we will reuse previously written log files for new
     * logs, overwriting the old data.  The value indicates how many
     * such files we will keep around at any point in time for later
     * use.
     *
     * This is more efficient because the blocks are already
     * allocated and fdatasync does not need to update the inode after
     * each write.
     *
     * Default: 0
     *
     * @param recycleLogFileNum the number of log files to keep for recycling
     *
     * @return the reference to the current options
     */
    fun setRecycleLogFileNum(recycleLogFileNum: Long): Options

    /**
     * Recycle log files.
     *
     * If non-zero, we will reuse previously written log files for new
     * logs, overwriting the old data.  The value indicates how many
     * such files we will keep around at any point in time for later
     * use.
     *
     * This is more efficient because the blocks are already
     * allocated and fdatasync does not need to update the inode after
     * each write.
     *
     * Default: 0
     *
     * @return the number of log files kept for recycling
     */
    fun recycleLogFileNum(): Long

    /**
     * Manifest file is rolled over on reaching this limit.
     * The older manifest file be deleted.
     * The default value is MAX_INT so that roll-over does not take place.
     *
     * @param maxManifestFileSize the size limit of a manifest file.
     * @return the instance of the current object.
     */
    fun setMaxManifestFileSize(maxManifestFileSize: Long): Options

    /**
     * Manifest file is rolled over on reaching this limit.
     * The older manifest file be deleted.
     * The default value is MAX_INT so that roll-over does not take place.
     *
     * @return the size limit of a manifest file.
     */
    fun maxManifestFileSize(): Long

    /**
     * Number of shards used for table cache.
     *
     * @param tableCacheNumshardbits the number of chards
     * @return the instance of the current object.
     */
    fun setTableCacheNumshardbits(tableCacheNumshardbits: Int): Options

    /**
     * Number of shards used for table cache.
     *
     * @return the number of shards used for table cache.
     */
    fun tableCacheNumshardbits(): Int

    /**
     * [.walTtlSeconds] and [.walSizeLimitMB] affect how archived logs
     * will be deleted.
     *
     *  1. If both set to 0, logs will be deleted asap and will not get into
     * the archive.
     *  1. If WAL_ttl_seconds is 0 and WAL_size_limit_MB is not 0,
     * WAL files will be checked every 10 min and if total size is greater
     * then WAL_size_limit_MB, they will be deleted starting with the
     * earliest until size_limit is met. All empty files will be deleted.
     *  1. If WAL_ttl_seconds is not 0 and WAL_size_limit_MB is 0, then
     * WAL files will be checked every WAL_ttl_secondsi / 2 and those that
     * are older than WAL_ttl_seconds will be deleted.
     *  1. If both are not 0, WAL files will be checked every 10 min and both
     * checks will be performed with ttl being first.
     *
     *
     * @param walTtlSeconds the ttl seconds
     * @return the instance of the current object.
     * @see .setWalSizeLimitMB
     */
    fun setWalTtlSeconds(walTtlSeconds: Long): Options

    /**
     * WalTtlSeconds() and walSizeLimitMB() affect how archived logs
     * will be deleted.
     *
     *  1. If both set to 0, logs will be deleted asap and will not get into
     * the archive.
     *  1. If WAL_ttl_seconds is 0 and WAL_size_limit_MB is not 0,
     * WAL files will be checked every 10 min and if total size is greater
     * then WAL_size_limit_MB, they will be deleted starting with the
     * earliest until size_limit is met. All empty files will be deleted.
     *  1. If WAL_ttl_seconds is not 0 and WAL_size_limit_MB is 0, then
     * WAL files will be checked every WAL_ttl_secondsi / 2 and those that
     * are older than WAL_ttl_seconds will be deleted.
     *  1. If both are not 0, WAL files will be checked every 10 min and both
     * checks will be performed with ttl being first.
     *
     *
     * @return the wal-ttl seconds
     * @see .walSizeLimitMB
     */
    fun walTtlSeconds(): Long

    /**
     * WalTtlSeconds() and walSizeLimitMB() affect how archived logs
     * will be deleted.
     *
     *  1. If both set to 0, logs will be deleted asap and will not get into
     * the archive.
     *  1. If WAL_ttl_seconds is 0 and WAL_size_limit_MB is not 0,
     * WAL files will be checked every 10 min and if total size is greater
     * then WAL_size_limit_MB, they will be deleted starting with the
     * earliest until size_limit is met. All empty files will be deleted.
     *  1. If WAL_ttl_seconds is not 0 and WAL_size_limit_MB is 0, then
     * WAL files will be checked every WAL_ttl_secondsi / 2 and those that
     * are older than WAL_ttl_seconds will be deleted.
     *  1. If both are not 0, WAL files will be checked every 10 min and both
     * checks will be performed with ttl being first.
     *
     *
     * @param sizeLimitMB size limit in mega-bytes.
     * @return the instance of the current object.
     * @see .setWalSizeLimitMB
     */
    fun setWalSizeLimitMB(sizeLimitMB: Long): Options

    /**
     * [.walTtlSeconds] and `#walSizeLimitMB()` affect how archived logs
     * will be deleted.
     *
     *  1. If both set to 0, logs will be deleted asap and will not get into
     * the archive.
     *  1. If WAL_ttl_seconds is 0 and WAL_size_limit_MB is not 0,
     * WAL files will be checked every 10 min and if total size is greater
     * then WAL_size_limit_MB, they will be deleted starting with the
     * earliest until size_limit is met. All empty files will be deleted.
     *  1. If WAL_ttl_seconds is not 0 and WAL_size_limit_MB is 0, then
     * WAL files will be checked every WAL_ttl_seconds i / 2 and those that
     * are older than WAL_ttl_seconds will be deleted.
     *  1. If both are not 0, WAL files will be checked every 10 min and both
     * checks will be performed with ttl being first.
     *
     * @return size limit in mega-bytes.
     * @see .walSizeLimitMB
     */
    fun walSizeLimitMB(): Long

    /**
     * Number of bytes to preallocate (via fallocate) the manifest
     * files.  Default is 4mb, which is reasonable to reduce random IO
     * as well as prevent overallocation for mounts that preallocate
     * large amounts of data (such as xfs's allocsize option).
     *
     * @param size the size in byte
     * @return the instance of the current object.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setManifestPreallocationSize(size: Long): Options

    /**
     * Number of bytes to preallocate (via fallocate) the manifest
     * files.  Default is 4mb, which is reasonable to reduce random IO
     * as well as prevent overallocation for mounts that preallocate
     * large amounts of data (such as xfs's allocsize option).
     *
     * @return size in bytes.
     */
    fun manifestPreallocationSize(): Long

    /**
     * Enable the OS to use direct I/O for reading sst tables.
     * Default: false
     *
     * @param useDirectReads if true, then direct read is enabled
     * @return the instance of the current object.
     */
    fun setUseDirectReads(useDirectReads: Boolean): Options

    /**
     * Enable the OS to use direct I/O for reading sst tables.
     * Default: false
     *
     * @return if true, then direct reads are enabled
     */
    fun useDirectReads(): Boolean

    /**
     * Enable the OS to use direct reads and writes in flush and
     * compaction
     * Default: false
     *
     * @param useDirectIoForFlushAndCompaction if true, then direct
     * I/O will be enabled for background flush and compactions
     * @return the instance of the current object.
     */
    fun setUseDirectIoForFlushAndCompaction(useDirectIoForFlushAndCompaction: Boolean): Options

    /**
     * Enable the OS to use direct reads and writes in flush and
     * compaction
     *
     * @return if true, then direct I/O is enabled for flush and
     * compaction
     */
    fun useDirectIoForFlushAndCompaction(): Boolean

    /**
     * Whether fallocate calls are allowed
     *
     * @param allowFAllocate false if fallocate() calls are bypassed
     *
     * @return the reference to the current options.
     */
    fun setAllowFAllocate(allowFAllocate: Boolean): Options

    /**
     * Whether fallocate calls are allowed
     *
     * @return false if fallocate() calls are bypassed
     */
    fun allowFAllocate(): Boolean

    /**
     * Allow the OS to mmap file for reading sst tables.
     * Default: false
     *
     * @param allowMmapReads true if mmap reads are allowed.
     * @return the instance of the current object.
     */
    fun setAllowMmapReads(allowMmapReads: Boolean): Options

    /**
     * Allow the OS to mmap file for reading sst tables.
     * Default: false
     *
     * @return true if mmap reads are allowed.
     */
    fun allowMmapReads(): Boolean

    /**
     * Allow the OS to mmap file for writing. Default: false
     *
     * @param allowMmapWrites true if mmap writes are allowd.
     * @return the instance of the current object.
     */
    fun setAllowMmapWrites(allowMmapWrites: Boolean): Options

    /**
     * Allow the OS to mmap file for writing. Default: false
     *
     * @return true if mmap writes are allowed.
     */
    fun allowMmapWrites(): Boolean

    /**
     * Disable child process inherit open files. Default: true
     *
     * @param isFdCloseOnExec true if child process inheriting open
     * files is disabled.
     * @return the instance of the current object.
     */
    fun setIsFdCloseOnExec(isFdCloseOnExec: Boolean): Options

    /**
     * Disable child process inherit open files. Default: true
     *
     * @return true if child process inheriting open files is disabled.
     */
    fun isFdCloseOnExec(): Boolean

    /**
     * If set true, will hint the underlying file system that the file
     * access pattern is random, when a sst file is opened.
     * Default: true
     *
     * @param adviseRandomOnOpen true if hinting random access is on.
     * @return the instance of the current object.
     */
    fun setAdviseRandomOnOpen(adviseRandomOnOpen: Boolean): Options

    /**
     * If set true, will hint the underlying file system that the file
     * access pattern is random, when a sst file is opened.
     * Default: true
     *
     * @return true if hinting random access is on.
     */
    fun adviseRandomOnOpen(): Boolean

    /**
     * Amount of data to build up in memtables across all column
     * families before writing to disk.
     *
     * This is distinct from [ColumnFamilyOptions.writeBufferSize],
     * which enforces a limit for a single memtable.
     *
     * This feature is disabled by default. Specify a non-zero value
     * to enable it.
     *
     * Default: 0 (disabled)
     *
     * @param dbWriteBufferSize the size of the write buffer
     *
     * @return the reference to the current options.
     */
    fun setDbWriteBufferSize(dbWriteBufferSize: Long): Options

    /**
     * Use passed [WriteBufferManager] to control memory usage across
     * multiple column families and/or DB instances.
     *
     * Check [
 * https://github.com/facebook/rocksdb/wiki/Write-Buffer-Manager](https://github.com/facebook/rocksdb/wiki/Write-Buffer-Manager)
     * for more details on when to use it
     *
     * @param writeBufferManager The WriteBufferManager to use
     * @return the reference of the current options.
     */
    fun setWriteBufferManager(writeBufferManager: WriteBufferManager): Options

    /**
     * Reference to [WriteBufferManager] used by it. <br></br>
     *
     * Default: null (Disabled)
     *
     * @return a reference to WriteBufferManager
     */
    fun writeBufferManager(): WriteBufferManager

    /**
     * Amount of data to build up in memtables across all column
     * families before writing to disk.
     *
     * This is distinct from [ColumnFamilyOptions.writeBufferSize],
     * which enforces a limit for a single memtable.
     *
     * This feature is disabled by default. Specify a non-zero value
     * to enable it.
     *
     * Default: 0 (disabled)
     *
     * @return the size of the write buffer
     */
    fun dbWriteBufferSize(): Long

    /**
     * Specify the file access pattern once a compaction is started.
     * It will be applied to all input files of a compaction.
     *
     * Default: [AccessHint.NORMAL]
     *
     * @param accessHint The access hint
     *
     * @return the reference to the current options.
     */
    fun setAccessHintOnCompactionStart(accessHint: AccessHint): Options

    /**
     * Specify the file access pattern once a compaction is started.
     * It will be applied to all input files of a compaction.
     *
     * Default: [AccessHint.NORMAL]
     *
     * @return The access hint
     */
    fun accessHintOnCompactionStart(): AccessHint

    /**
     * If true, always create a new file descriptor and new table reader
     * for compaction inputs. Turn this parameter on may introduce extra
     * memory usage in the table reader, if it allocates extra memory
     * for indexes. This will allow file descriptor prefetch options
     * to be set for compaction input files and not to impact file
     * descriptors for the same file used by user queries.
     * Suggest to enable [BlockBasedTableConfig.cacheIndexAndFilterBlocks]
     * for this mode if using block-based table.
     *
     * Default: false
     *
     * @param newTableReaderForCompactionInputs true if a new file descriptor and
     * table reader should be created for compaction inputs
     *
     * @return the reference to the current options.
     */
    fun setNewTableReaderForCompactionInputs(
        newTableReaderForCompactionInputs: Boolean
    ): Options

    /**
     * If true, always create a new file descriptor and new table reader
     * for compaction inputs. Turn this parameter on may introduce extra
     * memory usage in the table reader, if it allocates extra memory
     * for indexes. This will allow file descriptor prefetch options
     * to be set for compaction input files and not to impact file
     * descriptors for the same file used by user queries.
     * Suggest to enable [BlockBasedTableConfig.cacheIndexAndFilterBlocks]
     * for this mode if using block-based table.
     *
     * Default: false
     *
     * @return true if a new file descriptor and table reader are created for
     * compaction inputs
     */
    fun newTableReaderForCompactionInputs(): Boolean

    /**
     * This is a maximum buffer size that is used by WinMmapReadableFile in
     * unbuffered disk I/O mode. We need to maintain an aligned buffer for
     * reads. We allow the buffer to grow until the specified value and then
     * for bigger requests allocate one shot buffers. In unbuffered mode we
     * always bypass read-ahead buffer at ReadaheadRandomAccessFile
     * When read-ahead is required we then make use of
     * [MutableDBOptionsInterface.compactionReadaheadSize] value and
     * always try to read ahead.
     * With read-ahead we always pre-allocate buffer to the size instead of
     * growing it up to a limit.
     *
     * This option is currently honored only on Windows
     *
     * Default: 1 Mb
     *
     * Special value: 0 - means do not maintain per instance buffer. Allocate
     * per request buffer and avoid locking.
     *
     * @param randomAccessMaxBufferSize the maximum size of the random access
     * buffer
     *
     * @return the reference to the current options.
     */
    fun setRandomAccessMaxBufferSize(randomAccessMaxBufferSize: Long): Options

    /**
     * This is a maximum buffer size that is used by WinMmapReadableFile in
     * unbuffered disk I/O mode. We need to maintain an aligned buffer for
     * reads. We allow the buffer to grow until the specified value and then
     * for bigger requests allocate one shot buffers. In unbuffered mode we
     * always bypass read-ahead buffer at ReadaheadRandomAccessFile
     * When read-ahead is required we then make use of
     * [MutableDBOptionsInterface.compactionReadaheadSize] value and
     * always try to read ahead. With read-ahead we always pre-allocate buffer
     * to the size instead of growing it up to a limit.
     *
     * This option is currently honored only on Windows
     *
     * Default: 1 Mb
     *
     * Special value: 0 - means do not maintain per instance buffer. Allocate
     * per request buffer and avoid locking.
     *
     * @return the maximum size of the random access buffer
     */
    fun randomAccessMaxBufferSize(): Long

    /**
     * Use adaptive mutex, which spins in the user space before resorting
     * to kernel. This could reduce context switch when the mutex is not
     * heavily contended. However, if the mutex is hot, we could end up
     * wasting spin time.
     * Default: false
     *
     * @param useAdaptiveMutex true if adaptive mutex is used.
     * @return the instance of the current object.
     */
    fun setUseAdaptiveMutex(useAdaptiveMutex: Boolean): Options

    /**
     * Use adaptive mutex, which spins in the user space before resorting
     * to kernel. This could reduce context switch when the mutex is not
     * heavily contended. However, if the mutex is hot, we could end up
     * wasting spin time.
     * Default: false
     *
     * @return true if adaptive mutex is used.
     */
    fun useAdaptiveMutex(): Boolean

    /**
     * If true, then the status of the threads involved in this DB will
     * be tracked and available via GetThreadList() API.
     *
     * Default: false
     *
     * @param enableThreadTracking true to enable tracking
     *
     * @return the reference to the current options.
     */
    fun setEnableThreadTracking(enableThreadTracking: Boolean): Options

    /**
     * If true, then the status of the threads involved in this DB will
     * be tracked and available via GetThreadList() API.
     *
     * Default: false
     *
     * @return true if tracking is enabled
     */
    fun enableThreadTracking(): Boolean

    /**
     * By default, a single write thread queue is maintained. The thread gets
     * to the head of the queue becomes write batch group leader and responsible
     * for writing to WAL and memtable for the batch group.
     *
     * If [.enablePipelinedWrite] is true, separate write thread queue is
     * maintained for WAL write and memtable write. A write thread first enter WAL
     * writer queue and then memtable writer queue. Pending thread on the WAL
     * writer queue thus only have to wait for previous writers to finish their
     * WAL writing but not the memtable writing. Enabling the feature may improve
     * write throughput and reduce latency of the prepare phase of two-phase
     * commit.
     *
     * Default: false
     *
     * @param enablePipelinedWrite true to enabled pipelined writes
     *
     * @return the reference to the current options.
     */
    fun setEnablePipelinedWrite(enablePipelinedWrite: Boolean): Options

    /**
     * Returns true if pipelined writes are enabled.
     * See [.setEnablePipelinedWrite].
     *
     * @return true if pipelined writes are enabled, false otherwise.
     */
    fun enablePipelinedWrite(): Boolean

    /**
     * If true, allow multi-writers to update mem tables in parallel.
     * Only some memtable factorys support concurrent writes; currently it
     * is implemented only for SkipListFactory.  Concurrent memtable writes
     * are not compatible with inplace_update_support or filter_deletes.
     * It is strongly recommended to set
     * [.setEnableWriteThreadAdaptiveYield] if you are going to use
     * this feature.
     * Default: false
     *
     * @param allowConcurrentMemtableWrite true to enable concurrent writes
     * for the memtable
     *
     * @return the reference to the current options.
     */
    fun setAllowConcurrentMemtableWrite(allowConcurrentMemtableWrite: Boolean): Options

    /**
     * If true, allow multi-writers to update mem tables in parallel.
     * Only some memtable factorys support concurrent writes; currently it
     * is implemented only for SkipListFactory.  Concurrent memtable writes
     * are not compatible with inplace_update_support or filter_deletes.
     * It is strongly recommended to set
     * [.setEnableWriteThreadAdaptiveYield] if you are going to use
     * this feature.
     * Default: false
     *
     * @return true if concurrent writes are enabled for the memtable
     */
    fun allowConcurrentMemtableWrite(): Boolean

    /**
     * If true, threads synchronizing with the write batch group leader will
     * wait for up to [.writeThreadMaxYieldUsec] before blocking on a
     * mutex. This can substantially improve throughput for concurrent workloads,
     * regardless of whether [.allowConcurrentMemtableWrite] is enabled.
     * Default: false
     *
     * @param enableWriteThreadAdaptiveYield true to enable adaptive yield for the
     * write threads
     *
     * @return the reference to the current options.
     */
    fun setEnableWriteThreadAdaptiveYield(
        enableWriteThreadAdaptiveYield: Boolean
    ): Options

    /**
     * If true, threads synchronizing with the write batch group leader will
     * wait for up to [.writeThreadMaxYieldUsec] before blocking on a
     * mutex. This can substantially improve throughput for concurrent workloads,
     * regardless of whether [.allowConcurrentMemtableWrite] is enabled.
     * Default: false
     *
     * @return true if adaptive yield is enabled
     * for the writing threads
     */
    fun enableWriteThreadAdaptiveYield(): Boolean

    /**
     * The maximum number of microseconds that a write operation will use
     * a yielding spin loop to coordinate with other write threads before
     * blocking on a mutex.  (Assuming [.writeThreadSlowYieldUsec] is
     * set properly) increasing this value is likely to increase RocksDB
     * throughput at the expense of increased CPU usage.
     * Default: 100
     *
     * @param writeThreadMaxYieldUsec maximum number of microseconds
     *
     * @return the reference to the current options.
     */
    fun setWriteThreadMaxYieldUsec(writeThreadMaxYieldUsec: Long): Options

    /**
     * The maximum number of microseconds that a write operation will use
     * a yielding spin loop to coordinate with other write threads before
     * blocking on a mutex.  (Assuming [.writeThreadSlowYieldUsec] is
     * set properly) increasing this value is likely to increase RocksDB
     * throughput at the expense of increased CPU usage.
     * Default: 100
     *
     * @return the maximum number of microseconds
     */
    fun writeThreadMaxYieldUsec(): Long

    /**
     * The latency in microseconds after which a std::this_thread::yield
     * call (sched_yield on Linux) is considered to be a signal that
     * other processes or threads would like to use the current core.
     * Increasing this makes writer threads more likely to take CPU
     * by spinning, which will show up as an increase in the number of
     * involuntary context switches.
     * Default: 3
     *
     * @param writeThreadSlowYieldUsec the latency in microseconds
     *
     * @return the reference to the current options.
     */
    fun setWriteThreadSlowYieldUsec(writeThreadSlowYieldUsec: Long): Options

    /**
     * The latency in microseconds after which a std::this_thread::yield
     * call (sched_yield on Linux) is considered to be a signal that
     * other processes or threads would like to use the current core.
     * Increasing this makes writer threads more likely to take CPU
     * by spinning, which will show up as an increase in the number of
     * involuntary context switches.
     * Default: 3
     *
     * @return writeThreadSlowYieldUsec the latency in microseconds
     */
    fun writeThreadSlowYieldUsec(): Long

    /**
     * If true, then DB::Open() will not update the statistics used to optimize
     * compaction decision by loading table properties from many files.
     * Turning off this feature will improve DBOpen time especially in
     * disk environment.
     *
     * Default: false
     *
     * @param skipStatsUpdateOnDbOpen true if updating stats will be skipped
     *
     * @return the reference to the current options.
     */
    fun setSkipStatsUpdateOnDbOpen(skipStatsUpdateOnDbOpen: Boolean): Options

    /**
     * If true, then DB::Open() will not update the statistics used to optimize
     * compaction decision by loading table properties from many files.
     * Turning off this feature will improve DBOpen time especially in
     * disk environment.
     *
     * Default: false
     *
     * @return true if updating stats will be skipped
     */
    fun skipStatsUpdateOnDbOpen(): Boolean

    /**
     * Recovery mode to control the consistency while replaying WAL
     *
     * Default: [WALRecoveryMode.PointInTimeRecovery]
     *
     * @param walRecoveryMode The WAL recover mode
     *
     * @return the reference to the current options.
     */
    fun setWalRecoveryMode(walRecoveryMode: WALRecoveryMode): Options

    /**
     * Recovery mode to control the consistency while replaying WAL
     *
     * Default: [WALRecoveryMode.PointInTimeRecovery]
     *
     * @return The WAL recover mode
     */
    fun walRecoveryMode(): WALRecoveryMode

    /**
     * if set to false then recovery will fail when a prepared
     * transaction is encountered in the WAL
     *
     * Default: false
     *
     * @param allow2pc true if two-phase-commit is enabled
     *
     * @return the reference to the current options.
     */
    fun setAllow2pc(allow2pc: Boolean): Options

    /**
     * if set to false then recovery will fail when a prepared
     * transaction is encountered in the WAL
     *
     * Default: false
     *
     * @return true if two-phase-commit is enabled
     */
    fun allow2pc(): Boolean

    /**
     * A global cache for table-level rows.
     *
     * Default: null (disabled)
     *
     * @param rowCache The global row cache
     *
     * @return the reference to the current options.
     */
    fun setRowCache(rowCache: Cache): Options

    /**
     * A global cache for table-level rows.
     *
     * Default: null (disabled)
     *
     * @return The global row cache
     */
    fun rowCache(): Cache

    /**
     * A filter object supplied to be invoked while processing write-ahead-logs
     * (WALs) during recovery. The filter provides a way to inspect log
     * records, ignoring a particular record or skipping replay.
     * The filter is invoked at startup and is invoked from a single-thread
     * currently.
     *
     * @param walFilter the filter for processing WALs during recovery.
     *
     * @return the reference to the current options.
     */
    fun setWalFilter(walFilter: AbstractWalFilter): Options

    /**
     * Get's the filter for processing WALs during recovery.
     * See [.setWalFilter].
     *
     * @return the filter used for processing WALs during recovery.
     */
    fun walFilter(): WalFilter

    /**
     * If true, then DB::Open / CreateColumnFamily / DropColumnFamily
     * / SetOptions will fail if options file is not detected or properly
     * persisted.
     *
     * DEFAULT: false
     *
     * @param failIfOptionsFileError true if we should fail if there is an error
     * in the options file
     *
     * @return the reference to the current options.
     */
    fun setFailIfOptionsFileError(failIfOptionsFileError: Boolean): Options

    /**
     * If true, then DB::Open / CreateColumnFamily / DropColumnFamily
     * / SetOptions will fail if options file is not detected or properly
     * persisted.
     *
     * DEFAULT: false
     *
     * @return true if we should fail if there is an error in the options file
     */
    fun failIfOptionsFileError(): Boolean

    /**
     * If true, then print malloc stats together with rocksdb.stats
     * when printing to LOG.
     *
     * DEFAULT: false
     *
     * @param dumpMallocStats true if malloc stats should be printed to LOG
     *
     * @return the reference to the current options.
     */
    fun setDumpMallocStats(dumpMallocStats: Boolean): Options

    /**
     * If true, then print malloc stats together with rocksdb.stats
     * when printing to LOG.
     *
     * DEFAULT: false
     *
     * @return true if malloc stats should be printed to LOG
     */
    fun dumpMallocStats(): Boolean

    /**
     * By default RocksDB replay WAL logs and flush them on DB open, which may
     * create very small SST files. If this option is enabled, RocksDB will try
     * to avoid (but not guarantee not to) flush during recovery. Also, existing
     * WAL logs will be kept, so that if crash happened before flush, we still
     * have logs to recover from.
     *
     * DEFAULT: false
     *
     * @param avoidFlushDuringRecovery true to try to avoid (but not guarantee
     * not to) flush during recovery
     *
     * @return the reference to the current options.
     */
    fun setAvoidFlushDuringRecovery(avoidFlushDuringRecovery: Boolean): Options

    /**
     * By default RocksDB replay WAL logs and flush them on DB open, which may
     * create very small SST files. If this option is enabled, RocksDB will try
     * to avoid (but not guarantee not to) flush during recovery. Also, existing
     * WAL logs will be kept, so that if crash happened before flush, we still
     * have logs to recover from.
     *
     * DEFAULT: false
     *
     * @return true to try to avoid (but not guarantee not to) flush during
     * recovery
     */
    fun avoidFlushDuringRecovery(): Boolean

    /**
     * Set this option to true during creation of database if you want
     * to be able to ingest behind (call IngestExternalFile() skipping keys
     * that already exist, rather than overwriting matching keys).
     * Setting this option to true will affect 2 things:
     * 1) Disable some internal optimizations around SST file compression
     * 2) Reserve bottom-most level for ingested files only.
     * 3) Note that num_levels should be &gt;= 3 if this option is turned on.
     *
     * DEFAULT: false
     *
     * @param allowIngestBehind true to allow ingest behind, false to disallow.
     *
     * @return the reference to the current options.
     */
    fun setAllowIngestBehind(allowIngestBehind: Boolean): Options

    /**
     * Returns true if ingest behind is allowed.
     * See [.setAllowIngestBehind].
     *
     * @return true if ingest behind is allowed, false otherwise.
     */
    fun allowIngestBehind(): Boolean

    /**
     * Needed to support differential snapshots.
     * If set to true then DB will only process deletes with sequence number
     * less than what was set by SetPreserveDeletesSequenceNumber(uint64_t ts).
     * Clients are responsible to periodically call this method to advance
     * the cutoff time. If this method is never called and preserve_deletes
     * is set to true NO deletes will ever be processed.
     * At the moment this only keeps normal deletes, SingleDeletes will
     * not be preserved.
     *
     * DEFAULT: false
     *
     * @param preserveDeletes true to preserve deletes.
     *
     * @return the reference to the current options.
     */
    fun setPreserveDeletes(preserveDeletes: Boolean): Options

    /**
     * Returns true if deletes are preserved.
     * See [.setPreserveDeletes].
     *
     * @return true if deletes are preserved, false otherwise.
     */
    fun preserveDeletes(): Boolean

    /**
     * If enabled it uses two queues for writes, one for the ones with
     * disable_memtable and one for the ones that also write to memtable. This
     * allows the memtable writes not to lag behind other writes. It can be used
     * to optimize MySQL 2PC in which only the commits, which are serial, write to
     * memtable.
     *
     * DEFAULT: false
     *
     * @param twoWriteQueues true to enable two write queues, false otherwise.
     *
     * @return the reference to the current options.
     */
    fun setTwoWriteQueues(twoWriteQueues: Boolean): Options

    /**
     * Returns true if two write queues are enabled.
     *
     * @return true if two write queues are enabled, false otherwise.
     */
    fun twoWriteQueues(): Boolean

    /**
     * If true WAL is not flushed automatically after each write. Instead it
     * relies on manual invocation of FlushWAL to write the WAL buffer to its
     * file.
     *
     * DEFAULT: false
     *
     * @param manualWalFlush true to set disable automatic WAL flushing,
     * false otherwise.
     *
     * @return the reference to the current options.
     */
    fun setManualWalFlush(manualWalFlush: Boolean): Options

    /**
     * Returns true if automatic WAL flushing is disabled.
     * See [.setManualWalFlush].
     *
     * @return true if automatic WAL flushing is disabled, false otherwise.
     */
    fun manualWalFlush(): Boolean

    /**
     * If true, RocksDB supports flushing multiple column families and committing
     * their results atomically to MANIFEST. Note that it is not
     * necessary to set atomic_flush to true if WAL is always enabled since WAL
     * allows the database to be restored to the last persistent state in WAL.
     * This option is useful when there are column families with writes NOT
     * protected by WAL.
     * For manual flush, application has to specify which column families to
     * flush atomically in [RocksDB.flush].
     * For auto-triggered flush, RocksDB atomically flushes ALL column families.
     *
     * Currently, any WAL-enabled writes after atomic flush may be replayed
     * independently if the process crashes later and tries to recover.
     *
     * @param atomicFlush true to enable atomic flush of multiple column families.
     *
     * @return the reference to the current options.
     */
    fun setAtomicFlush(atomicFlush: Boolean): Options

    /**
     * Determine if atomic flush of multiple column families is enabled.
     *
     * See [.setAtomicFlush].
     *
     * @return true if atomic flush is enabled.
     */
    fun atomicFlush(): Boolean


    /**
     * Set appropriate parameters for bulk loading.
     * The reason that this is a function that returns "this" instead of a
     * constructor is to enable chaining of multiple similar calls in the future.
     *
     *  All data will be in level 0 without any automatic compaction.
     * It's recommended to manually call CompactRange(NULL, NULL) before reading
     * from the database, because otherwise the read can be very slow.
     *
     * @return the instance of the current Options.
     */
    fun prepareForBulkLoad(): Options
}
