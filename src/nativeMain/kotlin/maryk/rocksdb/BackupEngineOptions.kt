package maryk.rocksdb

import platform.posix.access
import platform.posix.W_OK
import rocksdb.rocksdb_backup_engine_options_create
import rocksdb.rocksdb_backup_engine_options_destroy

actual class BackupEngineOptions
actual constructor(private val path: String)
    : RocksObject() {

    init {
        val pathToCheck = if (!path.endsWith('/')) "$path/" else path
        // Use POSIX access function to check write permission
        require(access(pathToCheck, W_OK) == 0) { "Path $path is not writable" }
    }

    val native = rocksdb_backup_engine_options_create(path)

    actual fun backupDir(): String {
        return path
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_backup_engine_options_destroy(native)
            super.close()
        }
    }
}
