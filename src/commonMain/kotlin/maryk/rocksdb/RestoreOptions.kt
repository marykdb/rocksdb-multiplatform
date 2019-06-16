package maryk.rocksdb

/**
 * RestoreOptions to control the behavior of restore.
 *
 * Note that dispose() must be called before this instance become out-of-scope
 * to release the allocated memory in c++.
 */
expect class RestoreOptions
    /**
     * Constructor
     *
     * @param keepLogFiles If true, restore won't overwrite the existing log files
     * in wal_dir. It will also move all log files from archive directory to
     * wal_dir. Use this option in combination with
     * BackupableDBOptions::backup_log_files = false for persisting in-memory
     * databases.
     * Default: false
     */
    constructor(keepLogFiles: Boolean) : RocksObject
