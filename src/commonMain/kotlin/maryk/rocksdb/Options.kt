package maryk.rocksdb

expect class Options() : RocksObject, DBOptionsInterface<Options> {
    /**
     * Configure the table factory backing SSTs written for this instance.
     */
    fun setTableFormatConfig(tableFormatConfig: TableFormatConfig): Options

    /** Attach an [SstFileManager] so RocksDB can coordinate file deletion with other instances. */
    fun setSstFileManager(sstFileManager: SstFileManager): Options

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
     * Set the number of levels for this database
     * If level-styled compaction is used, then this number determines
     * the total number of levels.
     *
     * @param numLevels the number of levels.
     * @return the reference to the current options.
     */
    fun setNumLevels(numLevels: Int): Options

    /**
     * Maximum size of each compaction (not guarantee)
     *
     * @param maxCompactionBytes the compaction size limit
     * @return the reference to the current options.
     */
    fun setMaxCompactionBytes(maxCompactionBytes: Long): Options

    /**
     * Control maximum size of each compaction (not guaranteed)
     *
     * @return compaction size threshold
     */
    fun maxCompactionBytes(): Long

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
     *
     * @return Compaction style.
     */
    fun compactionStyle(): CompactionStyle

    /**
     * If level-styled compaction is used, then this number determines
     * the total number of levels.
     *
     * @return the number of levels.
     */
    fun numLevels(): Int

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
     * @param comparator instance.
     * @return the instance of the current object.
     */
    fun setComparator(
        comparator: AbstractComparator
    ): Options

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
     * Returns maximum number of write buffers.
     *
     * @return maximum number of write buffers.
     * @see .setMaxWriteBufferNumber
     */
    fun maxWriteBufferNumber(): Int

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
    fun statistics(): Statistics?

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
     * Use the specified object to interact with the environment,
     * e.g. to read/write files, schedule background work, etc.
     * Default: [Env.getDefault]
     *
     * @param env [Env] instance.
     * @return the instance of the current Options.
     */
    override fun setEnv(env: Env): Options

    /**
     * Returns the set RocksEnv instance.
     *
     * @return [RocksEnv] instance set in the options.
     */
    override fun getEnv(): Env
}
