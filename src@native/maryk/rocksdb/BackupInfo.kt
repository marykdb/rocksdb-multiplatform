package maryk.rocksdb

actual class BackupInfo
    internal constructor(
        private val backupId: Int,
        private val timestamp: Long,
        private val size: Long,
        private val numberFiles: Int,
        private val appMetadata: String?
    )
{
    actual fun backupId() = backupId

    actual fun timestamp() = timestamp

    actual fun size() = size

    actual fun numberFiles(): Int = numberFiles

    actual fun appMetadata() = appMetadata
}
