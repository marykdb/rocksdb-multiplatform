package maryk

import kotlinx.cinterop.UnsafeNumber
import platform.posix.S_IRWXU
import platform.posix.closedir
import platform.posix.mkdir
import platform.posix.opendir
import platform.posix.rmdir

@OptIn(UnsafeNumber::class)
actual fun createFolder(path: String): Boolean {
    return mkdir(path, S_IRWXU.toUShort()) == 0
}

actual fun deleteFolder(path: String): Boolean {
    return rmdir(path) == 0
}

actual fun doesFolderExist(path: String): Boolean {
    val directory = opendir(path)
    if (directory != null) {
        closedir(directory)
        return true
    }
    return false
}
