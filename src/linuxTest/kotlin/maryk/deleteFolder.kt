package maryk

import platform.posix.rmdir

actual fun deleteFolder(path: String) =
    when (rmdir(path)) {
        0 -> true
        else -> false
    }
