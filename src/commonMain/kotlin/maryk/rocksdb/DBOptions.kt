package maryk.rocksdb

expect class DBOptions() : RocksObject {
    /**
     * Copy constructor for DBOptions.
     *
     * NOTE: This does a shallow copy, which means env, rate_limiter, sst_file_manager,
     * info_log and other pointers will be cloned!
     *
     * @param other The DBOptions to copy.
     */
    constructor(other: DBOptions)

    /**
     * Constructor from Options
     *
     * @param options The options.
     */
    constructor(options: Options)

    /**
     * Use this if your DB is very small (like under 1GB) and you don't want to
     * spend lots of memory for memtables.
     *
     * @return the instance of the current object.
     */
    fun optimizeForSmallDb(): DBOptions

    /**
     * Use the specified object to interact with the environment,
     * e.g. to read/write files, schedule background work, etc.
     * Default: [Env.getDefault]
     *
     * @param env [Env] instance.
     * @return the instance of the current Options.
     */
    fun setEnv(env: Env): DBOptions

    /**
     * Returns the set RocksEnv instance.
     *
     * @return [RocksEnv] instance set in the options.
     */
    fun getEnv(): Env

    /**
     * By default, RocksDB uses only one background thread for flush and
     * compaction. Calling this function will set it up such that total of
     * `total_threads` is used.
     *
     * You almost definitely want to call this function if your system is
     * bottlenecked by RocksDB.
     *
     * @param totalThreads The total number of threads to be used by RocksDB.
     * A good value is the number of cores.
     *
     * @return the instance of the current Options
     */
    fun setIncreaseParallelism(totalThreads: Int): DBOptions

    /**
     * If this value is set to true, then the database will be created
     * if it is missing during `RocksDB.open()`.
     * Default: false
     *
     * @param flag a flag indicating whether to create a database the
     * specified database in [RocksDB.open] operation
     * is missing.
     * @return the instance of the current Options
     * @see RocksDB.open
     */
    fun setCreateIfMissing(flag: Boolean): DBOptions

    /**
     * Return true if the create_if_missing flag is set to true.
     * If true, the database will be created if it is missing.
     *
     * @return true if the createIfMissing option is set to true.
     * @see .setCreateIfMissing
     */
    fun createIfMissing(): Boolean

    /**
     * If true, missing column families will be automatically created
     *
     * Default: false
     *
     * @param flag a flag indicating if missing column families shall be
     * created automatically.
     * @return true if missing column families shall be created automatically
     * on open.
     */
    fun setCreateMissingColumnFamilies(flag: Boolean): DBOptions

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
    fun setErrorIfExists(errorIfExists: Boolean): DBOptions

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
    fun setParanoidChecks(paranoidChecks: Boolean): DBOptions

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
    fun setRateLimiter(rateLimiter: RateLimiter): DBOptions

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
    fun setSstFileManager(sstFileManager: SstFileManager): DBOptions

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
    fun setLogger(logger: Logger): DBOptions

    /**
     *
     * Sets the RocksDB log level. Default level is INFO
     *
     * @param infoLogLevel log level to set.
     * @return the instance of the current object.
     */
    fun setInfoLogLevel(infoLogLevel: InfoLogLevel): DBOptions

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
    fun setMaxFileOpeningThreads(maxFileOpeningThreads: Int): DBOptions

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
    fun setStatistics(statistics: Statistics): DBOptions

    /**
     * Returns statistics object.
     *
     * @return the instance of the statistics object or null if there is no
     * statistics object.
     *
     * @see .setStatistics
     */
    fun statistics(): Statistics

    /**
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
    fun setUseFsync(useFsync: Boolean): DBOptions

    /**
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
    fun setDbPaths(dbPaths: Collection<DbPath>): DBOptions

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
    fun setDbLogDir(dbLogDir: String): DBOptions

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
    fun setWalDir(walDir: String): DBOptions

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
    fun setDeleteObsoleteFilesPeriodMicros(micros: Long): DBOptions

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
    fun setMaxSubcompactions(maxSubcompactions: Int): DBOptions

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
    fun setMaxLogFileSize(maxLogFileSize: Long): DBOptions

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
    fun setLogFileTimeToRoll(logFileTimeToRoll: Long): DBOptions

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
    fun setKeepLogFileNum(keepLogFileNum: Long): DBOptions

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
    fun setRecycleLogFileNum(recycleLogFileNum: Long): DBOptions

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
    fun setMaxManifestFileSize(maxManifestFileSize: Long): DBOptions

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
    fun setTableCacheNumshardbits(tableCacheNumshardbits: Int): DBOptions

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
    fun setWalTtlSeconds(walTtlSeconds: Long): DBOptions

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
     * @param sizeLimitMB size limit in mega-bytes.
     * @return the instance of the current object.
     * @see .setWalSizeLimitMB
     */
    fun setWalSizeLimitMB(sizeLimitMB: Long): DBOptions

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
    fun setManifestPreallocationSize(size: Long): DBOptions

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
    fun setUseDirectReads(useDirectReads: Boolean): DBOptions

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
    fun setUseDirectIoForFlushAndCompaction(useDirectIoForFlushAndCompaction: Boolean): DBOptions

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
    fun setAllowFAllocate(allowFAllocate: Boolean): DBOptions

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
    fun setAllowMmapReads(allowMmapReads: Boolean): DBOptions

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
    fun setAllowMmapWrites(allowMmapWrites: Boolean): DBOptions

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
    fun setIsFdCloseOnExec(isFdCloseOnExec: Boolean): DBOptions

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
    fun setAdviseRandomOnOpen(adviseRandomOnOpen: Boolean): DBOptions

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
    fun setDbWriteBufferSize(dbWriteBufferSize: Long): DBOptions

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
    fun setWriteBufferManager(writeBufferManager: WriteBufferManager): DBOptions

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
    fun setAccessHintOnCompactionStart(accessHint: AccessHint): DBOptions

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
    ): DBOptions

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
    fun setRandomAccessMaxBufferSize(randomAccessMaxBufferSize: Long): DBOptions

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
    fun setUseAdaptiveMutex(useAdaptiveMutex: Boolean): DBOptions

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
    fun setEnableThreadTracking(enableThreadTracking: Boolean): DBOptions

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
    fun setEnablePipelinedWrite(enablePipelinedWrite: Boolean): DBOptions

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
    fun setAllowConcurrentMemtableWrite(allowConcurrentMemtableWrite: Boolean): DBOptions

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
    ): DBOptions

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
    fun setWriteThreadMaxYieldUsec(writeThreadMaxYieldUsec: Long): DBOptions

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
    fun setWriteThreadSlowYieldUsec(writeThreadSlowYieldUsec: Long): DBOptions

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
    fun setSkipStatsUpdateOnDbOpen(skipStatsUpdateOnDbOpen: Boolean): DBOptions

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
    fun setWalRecoveryMode(walRecoveryMode: WALRecoveryMode): DBOptions

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
    fun setAllow2pc(allow2pc: Boolean): DBOptions

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
    fun setRowCache(rowCache: Cache): DBOptions

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
    fun setWalFilter(walFilter: AbstractWalFilter): DBOptions

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
    fun setFailIfOptionsFileError(failIfOptionsFileError: Boolean): DBOptions

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
    fun setDumpMallocStats(dumpMallocStats: Boolean): DBOptions

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
    fun setAvoidFlushDuringRecovery(avoidFlushDuringRecovery: Boolean): DBOptions

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
    fun setAllowIngestBehind(allowIngestBehind: Boolean): DBOptions

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
    fun setPreserveDeletes(preserveDeletes: Boolean): DBOptions

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
    fun setTwoWriteQueues(twoWriteQueues: Boolean): DBOptions

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
    fun setManualWalFlush(manualWalFlush: Boolean): DBOptions

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
    fun setAtomicFlush(atomicFlush: Boolean): DBOptions

    /**
     * Determine if atomic flush of multiple column families is enabled.
     *
     * See [.setAtomicFlush].
     *
     * @return true if atomic flush is enabled.
     */
    fun atomicFlush(): Boolean


    /**
     * Specifies the maximum number of concurrent background jobs (both flushes
     * and compactions combined).
     * Default: 2
     *
     * @param maxBackgroundJobs number of max concurrent background jobs
     * @return the instance of the current object.
     */
    fun setMaxBackgroundJobs(maxBackgroundJobs: Int): DBOptions

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
    fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): DBOptions

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
    fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): DBOptions

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
    fun setDelayedWriteRate(delayedWriteRate: Long): DBOptions

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
    fun setMaxTotalWalSize(maxTotalWalSize: Long): DBOptions

    /**
     *
     * Returns the max total wal size. Once write-ahead logs exceed this size,
     * we will start forcing the flush of column families whose memtables are
     * backed by the oldest live WAL file (i.e. the ones that are causing all
     * the space amplification).
     *
     *
     * If set to 0 (default), we will dynamically choose the WAL size limit
     * to be [sum of all write_buffer_size * max_write_buffer_number] * 2
     *
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
    fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): DBOptions

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
    fun setMaxOpenFiles(maxOpenFiles: Int): DBOptions

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
    fun setBytesPerSync(bytesPerSync: Long): DBOptions

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
    fun setWalBytesPerSync(walBytesPerSync: Long): DBOptions

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
    fun setCompactionReadaheadSize(compactionReadaheadSize: Long): DBOptions

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
}
