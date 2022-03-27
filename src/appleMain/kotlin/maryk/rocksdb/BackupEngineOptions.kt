package maryk.rocksdb

import platform.Foundation.NSFileManager

actual class BackupEngineOptions
    actual constructor(private val path: String)
: RocksObject() {
    init {
        val pathToCheck = path.let {
            if (it.last() != '/') {
                "$path/"
            } else path
        }

        require(NSFileManager.defaultManager.isWritableFileAtPath(pathToCheck)) { "Path $path is not writable" }
    }

    actual fun backupDir(): String {
        return path
    }
}
