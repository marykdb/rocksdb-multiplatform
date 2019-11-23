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
}
