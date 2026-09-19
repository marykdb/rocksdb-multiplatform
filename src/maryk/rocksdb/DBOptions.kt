package maryk.rocksdb

expect fun DBOptions.addEventListener(listener: EventListener): DBOptions

expect fun Options.addEventListener(listener: EventListener): Options

expect class DBOptions() : RocksObject, DBOptionsInterface<DBOptions> {
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
    fun statistics(): Statistics?

    /**
     * Use the specified environment implementation for interacting with the
     * filesystem, scheduling background work and other OS dependent
     * operations.
     *
     * Default: [getDefaultEnv]
     *
     * @param env the environment instance to use.
     * @return the current options instance.
     */
    override fun setEnv(env: Env): DBOptions

    /**
     * Returns the environment configured on this options object.
     */
    override fun getEnv(): Env

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
     * Sets the recovery policy when replaying the WAL during open.
     */
    fun setWalRecoveryMode(mode: WALRecoveryMode): DBOptions

    /**
     * Returns the configured recovery policy for WAL replay.
     */
    fun walRecoveryMode(): WALRecoveryMode
}
