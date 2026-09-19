package maryk.rocksdb

/**
 * Instances of this class describe a Backup made by
 * [BackupEngine].
 */
expect class BackupInfo {
    /** @return the backup id.*/
    fun backupId(): Int

    /** @return the timestamp of the backup. */
    fun timestamp(): Long

    /** @return the size of the backup */
    fun size(): Long

    /** @return the number of files of this backup. */
    fun numberFiles(): Int

    /** @return the associated application metadata, or null */
    fun appMetadata(): String?
}
