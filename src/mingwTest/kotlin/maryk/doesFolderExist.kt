package maryk

import platform.windows.FILE_ATTRIBUTE_DIRECTORY
import platform.windows.GetFileAttributesW

actual fun doesFolderExist(path: String): Boolean {
    val attributes = GetFileAttributesW(path)
    return attributes != UInt.MAX_VALUE && (attributes and FILE_ATTRIBUTE_DIRECTORY.toUInt()) != 0u
}
