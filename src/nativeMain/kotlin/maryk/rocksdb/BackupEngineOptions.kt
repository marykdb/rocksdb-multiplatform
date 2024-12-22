package maryk.rocksdb

import platform.posix.access
import platform.posix.W_OK

actual class BackupEngineOptions
actual constructor(private val path: String) : RocksObject() {

    init {
        val pathToCheck = if (!path.endsWith('/')) "$path/" else path
        // Use POSIX access function to check write permission
        require(access(pathToCheck, W_OK) == 0) { "Path $path is not writable" }
    }

    actual fun backupDir(): String {
        return path
    }
}
