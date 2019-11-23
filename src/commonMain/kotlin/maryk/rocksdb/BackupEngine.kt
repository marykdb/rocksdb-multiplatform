package maryk.rocksdb


/**
 * BackupEngine allows you to backup
 * and restore the database
 *
 * Be aware, that `new BackupEngine` takes time proportional to the amount
 * of backups. So if you have a slow filesystem to backup (like HDFS)
 * and you have a lot of backups then restoring can take some time.
 * That's why we recommend to limit the number of backups.
 * Also we recommend to keep BackupEngine alive and not to recreate it every
 * time you need to do a backup.
 */
expect class BackupEngine : RocksObject, AutoCloseable {
    /**
     * Captures the state of the database in the latest backup
     *
     * Just a convenience for [.createNewBackup] with
     * the flushBeforeBackup parameter set to false
     *
     * @param db The database to backup
     *
     * Note - This method is not thread safe
     *
     * @throws RocksDBException thrown if a new backup could not be created
     */
    fun createNewBackup(db: RocksDB)

    /**
     * Captures the state of the database in the latest backup
     *
     * @param db The database to backup
     * @param flushBeforeBackup When true, the Backup Engine will first issue a
     * memtable flush and only then copy the DB files to
     * the backup directory. Doing so will prevent log
     * files from being copied to the backup directory
     * (since flush will delete them).
     * When false, the Backup Engine will not issue a
     * flush before starting the backup. In that case,
     * the backup will also include log files
     * corresponding to live memtables. If writes have
     * been performed with the write ahead log disabled,
     * set flushBeforeBackup to true to prevent those
     * writes from being lost. Otherwise, the backup will
     * always be consistent with the current state of the
     * database regardless of the flushBeforeBackup
     * parameter.
     *
     * Note - This method is not thread safe
     *
     * @throws RocksDBException thrown if a new backup could not be created
     */
    fun createNewBackup(
        db: RocksDB, flushBeforeBackup: Boolean
    )

    /**
     * Captures the state of the database in the latest backup along with
     * application specific metadata.
     *
     * @param db The database to backup
     * @param metadata Application metadata
     * @param flushBeforeBackup When true, the Backup Engine will first issue a
     * memtable flush and only then copy the DB files to
     * the backup directory. Doing so will prevent log
     * files from being copied to the backup directory
     * (since flush will delete them).
     * When false, the Backup Engine will not issue a
     * flush before starting the backup. In that case,
     * the backup will also include log files
     * corresponding to live memtables. If writes have
     * been performed with the write ahead log disabled,
     * set flushBeforeBackup to true to prevent those
     * writes from being lost. Otherwise, the backup will
     * always be consistent with the current state of the
     * database regardless of the flushBeforeBackup
     * parameter.
     *
     * Note - This method is not thread safe
     *
     * @throws RocksDBException thrown if a new backup could not be created
     */
    fun createNewBackupWithMetadata(
        db: RocksDB, metadata: String,
        flushBeforeBackup: Boolean
    )

    /**
     * Gets information about the available
     * backups
     *
     * @return A list of information about each available backup
     */
    fun getBackupInfo(): List<BackupInfo>

    /**
     * Returns a list of corrupted backup ids. If there
     * is no corrupted backup the method will return an
     * empty list.
     *
     * @return array of backup ids as int ids.
     */
    fun getCorruptedBackups(): IntArray

    /**
     * Will delete all the files we don't need anymore. It will
     * do the full scan of the files/ directory and delete all the
     * files that are not referenced.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun garbageCollect()

    /**
     * Deletes old backups, keeping just the latest numBackupsToKeep
     *
     * @param numBackupsToKeep The latest n backups to keep
     *
     * @throws RocksDBException thrown if the old backups could not be deleted
     */
    fun purgeOldBackups(
        numBackupsToKeep: Int
    )

    /**
     * Deletes a backup
     *
     * @param backupId The id of the backup to delete
     *
     * @throws RocksDBException thrown if the backup could not be deleted
     */
    fun deleteBackup(backupId: Int)

    /**
     * Restore the database from a backup
     *
     * IMPORTANT: if options.share_table_files == true and you restore the DB
     * from some backup that is not the latest, and you start creating new
     * backups from the new DB, they will probably fail!
     *
     * Example: Let's say you have backups 1, 2, 3, 4, 5 and you restore 3.
     * If you add new data to the DB and try creating a new backup now, the
     * database will diverge from backups 4 and 5 and the new backup will fail.
     * If you want to create new backup, you will first have to delete backups 4
     * and 5.
     *
     * @param backupId The id of the backup to restore
     * @param dbDir The directory to restore the backup to, i.e. where your
     * database is
     * @param walDir The location of the log files for your database,
     * often the same as dbDir
     * @param restoreOptions Options for controlling the restore
     *
     * @throws RocksDBException thrown if the database could not be restored
     */
    fun restoreDbFromBackup(
        backupId: Int, dbDir: String, walDir: String,
        restoreOptions: RestoreOptions
    )

    /**
     * Restore the database from the latest backup
     *
     * @param dbDir The directory to restore the backup to, i.e. where your
     * database is
     * @param walDir The location of the log files for your database, often the
     * same as dbDir
     * @param restoreOptions Options for controlling the restore
     *
     * @throws RocksDBException thrown if the database could not be restored
     */
    fun restoreDbFromLatestBackup(
        dbDir: String, walDir: String,
        restoreOptions: RestoreOptions
    )
}

/**
 * Opens a new Backup Engine
 *
 * @param env The environment that the backup engine should operate within
 * @param options Any options for the backup engine
 *
 * @return A new BackupEngine instance
 * @throws RocksDBException thrown if the backup engine could not be opened
 */
expect fun openBackupEngine(
    env: Env,
    options: BackupableDBOptions
): BackupEngine
