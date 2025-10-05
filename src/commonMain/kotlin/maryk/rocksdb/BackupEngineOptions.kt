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
expect class BackupEngineOptions
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
     * null, backups will be written out using DBs Env. Otherwise,
     * backup's I/O will be performed using this object.
     *
     * Default: null
     *
     * @param env The environment to use
     * @return instance of current BackupEngineOptions.
     */
    fun setBackupEnv(env: Env): BackupEngineOptions

    /** Returns the environment used for backup I/O. */
    fun backupEnv(): Env?

    /** Share table files between backups. */
    fun setShareTableFiles(shareTableFiles: Boolean): BackupEngineOptions

    /** Returns true if SST files are shared between backups. */
    fun shareTableFiles(): Boolean

    /** Enable or disable synchronous backups. */
    fun setSync(sync: Boolean): BackupEngineOptions

    /** Returns true if synchronous backups are enabled. */
    fun sync(): Boolean

    /** Configure whether existing backups should be destroyed. */
    fun setDestroyOldData(destroyOldData: Boolean): BackupEngineOptions

    /** Returns true if old backup data will be destroyed. */
    fun destroyOldData(): Boolean

    /**
     * Configure whether log files are copied into the backup.
     */
    fun setBackupLogFiles(backupLogFiles: Boolean): BackupEngineOptions

    /** Returns true if log files are backed up. */
    fun backupLogFiles(): Boolean

    /**
     * Limit the rate at which data is copied during a backup. A value of 0
     * disables throttling.
     */
    fun setBackupRateLimit(backupRateLimit: Long): BackupEngineOptions

    /** Returns the configured backup rate limit in bytes per second. */
    fun backupRateLimit(): Long

    /** Attach a rate limiter that governs backup throughput. */
    fun setBackupRateLimiter(rateLimiter: RateLimiter): BackupEngineOptions

    /** Returns the configured backup rate limiter, if any. */
    fun backupRateLimiter(): RateLimiter?

    /** Configure the restore rate limit in bytes per second. */
    fun setRestoreRateLimit(restoreRateLimit: Long): BackupEngineOptions

    /** Returns the configured restore rate limit in bytes per second. */
    fun restoreRateLimit(): Long

    /** Attach a rate limiter that governs restore throughput. */
    fun setRestoreRateLimiter(rateLimiter: RateLimiter): BackupEngineOptions

    /** Returns the configured restore rate limiter, if any. */
    fun restoreRateLimiter(): RateLimiter?

    /** Configure whether SSTs are identified by checksum metadata. */
    fun setShareFilesWithChecksum(shareFilesWithChecksum: Boolean): BackupEngineOptions

    /** Returns true if file sharing is keyed by checksum metadata. */
    fun shareFilesWithChecksum(): Boolean

    /** Sets the number of background threads used for backup/restore. */
    fun setMaxBackgroundOperations(maxBackgroundOperations: Int): BackupEngineOptions

    /** Returns the number of background threads used for backup/restore. */
    fun maxBackgroundOperations(): Int

    /**
     * Configures how many bytes are copied between callbacks when a
     * progress listener is attached.
     */
    fun setCallbackTriggerIntervalSize(callbackTriggerIntervalSize: Long): BackupEngineOptions

    /** Returns the current callback trigger interval size. */
    fun callbackTriggerIntervalSize(): Long
}
