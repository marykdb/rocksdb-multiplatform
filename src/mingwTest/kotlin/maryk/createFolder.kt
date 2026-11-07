package maryk

import platform.windows.CreateDirectoryW

actual fun createFolder(path: String): Boolean = CreateDirectoryW(path, null) != 0
