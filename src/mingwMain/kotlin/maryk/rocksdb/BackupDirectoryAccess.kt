package maryk.rocksdb

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.wcstr
import platform.posix.W_OK
import platform.posix._waccess

internal actual fun backupDirectoryAccess(path: String): Int = memScoped {
    _waccess(path.wcstr.ptr, W_OK)
}
