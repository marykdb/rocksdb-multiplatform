package maryk

import platform.posix.mkdir

actual fun createFolder(path: String) =
    when (mkdir(path)) {
        0 -> true
        else -> false
    }
