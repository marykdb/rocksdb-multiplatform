package maryk

import platform.posix.S_IRWXU
import platform.posix.mkdir

actual fun createFolder(path: String) =
    when (mkdir(path, S_IRWXU)) {
        0 -> true
        else -> false
    }
