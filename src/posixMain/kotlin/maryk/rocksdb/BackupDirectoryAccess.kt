package maryk.rocksdb

import platform.posix.W_OK
import platform.posix.access

internal actual fun backupDirectoryAccess(path: String): Int = access(path, W_OK)
