package maryk.rocksdb

/**
 * BackupableDBOptions to control the behavior of a backupable database.
 * It will be used during the creation of a [org.rocksdb.BackupEngine].
 *
 * Note that dispose() must be called before an Options instance
 * become out-of-scope to release the allocated memory in c++.
 *
 * @see org.rocksdb.BackupEngine
 */
expect class BackupableDBOptions
    /**
     * BackupableDBOptions constructor.
     *
     * @param path Where to keep the backup files. Has to be different than db
     * name. Best to set this to `db name_ + "/backups"`
     * @throws IllegalArgumentException if illegal path is used.
     */
    (path: String)
: RocksObject {
    /** Returns the path to the BackupableDB directory. */
    fun backupDir(): String

    /**
     * Backup Env object. It will be used for backup file I/O. If it's
     * null, backups will be written out using DBs Env. Otherwise
     * backup's I/O will be performed using this object.
     *
     * If you want to have backups on HDFS, use HDFS Env here!
     *
     * Default: null
     *
     * @param env The environment to use
     * @return instance of current BackupableDBOptions.
     */
    fun setBackupEnv(env: Env): BackupableDBOptions

    /**
     * Backup Env object. It will be used for backup file I/O. If it's
     * null, backups will be written out using DBs Env. Otherwise
     * backup's I/O will be performed using this object.
     *
     * If you want to have backups on HDFS, use HDFS Env here!
     *
     * Default: null
     *
     * @return The environment in use
     */
    fun backupEnv(): Env?

    /**
     * Share table files between backups.
     *
     * @param shareTableFiles If `share_table_files == true`, backup will
     * assume that table files with same name have the same contents. This
     * enables incremental backups and avoids unnecessary data copies. If
     * `share_table_files == false`, each backup will be on its own and
     * will not share any data with other backups.
     *
     * Default: true
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setShareTableFiles(shareTableFiles: Boolean): BackupableDBOptions

    /**
     *
     * Share table files between backups.
     *
     * @return boolean value indicating if SST files will be shared between
     * backups.
     */
    fun shareTableFiles(): Boolean

    /**
     * Set the logger to use for Backup info and error messages
     *
     * @param logger The logger to use for the backup
     * @return instance of current BackupableDBOptions.
     */
    fun setInfoLog(logger: Logger): BackupableDBOptions

    /**
     * Set the logger to use for Backup info and error messages
     *
     * Default: null
     *
     * @return The logger in use for the backup
     */
    fun infoLog(): Logger?

    /**
     * Set synchronous backups.
     *
     * @param sync If `sync == true`, we can guarantee you'll get consistent
     * backup even on a machine crash/reboot. Backup process is slower with sync
     * enabled. If `sync == false`, we don't guarantee anything on machine
     * reboot. However, chances are some of the backups are consistent.
     *
     * Default: true
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setSync(sync: Boolean): BackupableDBOptions

    /**
     * Are synchronous backups activated.
     *
     * @return boolean value if synchronous backups are configured.
     */
    fun sync(): Boolean

    /**
     * Set if old data will be destroyed.
     *
     * @param destroyOldData If true, it will delete whatever backups there are
     * already.
     *
     * Default: false
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setDestroyOldData(destroyOldData: Boolean): BackupableDBOptions

    /**
     * Returns if old data will be destroyed will performing new backups.
     */
    fun destroyOldData(): Boolean

    /**
     * Set if log files shall be persisted.
     *
     * @param backupLogFiles If false, we won't backup log files. This option can
     * be useful for backing up in-memory databases where log file are
     * persisted, but table files are in memory.
     *
     * Default: true
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setBackupLogFiles(backupLogFiles: Boolean): BackupableDBOptions

    /** Return boolean value indicating if log files will be persisted. */
    fun backupLogFiles(): Boolean

    /**
     * Set backup rate limit.
     *
     * @param backupRateLimit Max bytes that can be transferred in a second during
     * backup. If 0 or negative, then go as fast as you can.
     *
     * Default: 0
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setBackupRateLimit(backupRateLimit: Long): BackupableDBOptions

    /**
     *
     * Return backup rate limit which described the max bytes that can be
     * transferred in a second during backup.
     *
     * @return numerical value describing the backup transfer limit in bytes per
     * second.
     */
    fun backupRateLimit(): Long

    /**
     * Backup rate limiter. Used to control transfer speed for backup. If this is
     * not null, [.backupRateLimit] is ignored.
     *
     * Default: null
     *
     * @param backupRateLimiter The rate limiter to use for the backup
     * @return instance of current BackupableDBOptions.
     */
    fun setBackupRateLimiter(backupRateLimiter: RateLimiter): BackupableDBOptions

    /**
     * Backup rate limiter. Used to control transfer speed for backup. If this is
     * not null, [.backupRateLimit] is ignored.
     *
     * Default: null
     *
     * @return The rate limiter in use for the backup
     */
    fun backupRateLimiter(): RateLimiter?

    /**
     * Set restore rate limit.
     *
     * @param restoreRateLimit Max bytes that can be transferred in a second
     * during restore. If 0 or negative, then go as fast as you can.
     *
     * Default: 0
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setRestoreRateLimit(restoreRateLimit: Long): BackupableDBOptions

    /**
     * Return restore rate limit which described the max bytes that can be
     * transferred in a second during restore.
     *
     * @return numerical value describing the restore transfer limit in bytes per
     * second.
     */
    fun restoreRateLimit(): Long

    /**
     * Restore rate limiter. Used to control transfer speed during restore. If
     * this is not null, [.restoreRateLimit] is ignored.
     *
     * Default: null
     *
     * @param restoreRateLimiter The rate limiter to use during restore
     * @return instance of current BackupableDBOptions.
     */
    fun setRestoreRateLimiter(restoreRateLimiter: RateLimiter): BackupableDBOptions

    /**
     * Restore rate limiter. Used to control transfer speed during restore. If
     * this is not null, [.restoreRateLimit] is ignored.
     *
     * Default: null
     *
     * @return The rate limiter in use during restore
     */
    fun restoreRateLimiter(): RateLimiter?

    /**
     * Only used if share_table_files is set to true. If true, will consider
     * that backups can come from different databases, hence a sst is not uniquely
     * identified by its name, but by the triple (file name, crc32, file length)
     *
     * @param shareFilesWithChecksum boolean value indicating if SST files are
     * stored using the triple (file name, crc32, file length) and not its name.
     *
     * Note: this is an experimental option, and you'll need to set it manually
     * turn it on only if you know what you're doing*
     *
     * Default: false
     *
     * @return instance of current BackupableDBOptions.
     */
    fun setShareFilesWithChecksum(
        shareFilesWithChecksum: Boolean
    ): BackupableDBOptions

    /**
     * Return of share files with checksum is active.
     *
     * @return boolean value indicating if share files with checksum
     * is active.
     */
    fun shareFilesWithChecksum(): Boolean

    /**
     * Up to this many background threads will copy files for
     * [BackupEngine.createNewBackup] and
     * [BackupEngine.restoreDbFromBackup]
     *
     * Default: 1
     *
     * @param maxBackgroundOperations The maximum number of background threads
     * @return instance of current BackupableDBOptions.
     */
    fun setMaxBackgroundOperations(
        maxBackgroundOperations: Int
    ): BackupableDBOptions

    /**
     * Up to this many background threads will copy files for
     * [BackupEngine.createNewBackup] and
     * [BackupEngine.restoreDbFromBackup]
     *
     * Default: 1
     *
     * @return The maximum number of background threads
     */
    fun maxBackgroundOperations(): Int

    /**
     * During backup user can get callback every time next
     * [.callbackTriggerIntervalSize] bytes being copied.
     *
     * Default: 4194304
     *
     * @param callbackTriggerIntervalSize The interval size for the
     * callback trigger
     * @return instance of current BackupableDBOptions.
     */
    fun setCallbackTriggerIntervalSize(
        callbackTriggerIntervalSize: Long
    ): BackupableDBOptions

    /**
     * During backup user can get callback every time next
     * [.callbackTriggerIntervalSize] bytes being copied.
     *
     * Default: 4194304
     *
     * @return The interval size for the callback trigger
     */
    fun callbackTriggerIntervalSize(): Long
}
