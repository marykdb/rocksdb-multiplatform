@file:OptIn(ExperimentalForeignApi::class)

package maryk

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.windows.DeleteFileW
import platform.windows.FILE_ATTRIBUTE_DIRECTORY
import platform.windows.FILE_ATTRIBUTE_REPARSE_POINT
import platform.windows.FindClose
import platform.windows.FindFirstFileW
import platform.windows.FindNextFileW
import platform.windows.GetFileAttributesW
import platform.windows.GetLastError
import platform.windows.ERROR_NO_MORE_FILES
import platform.windows.INVALID_HANDLE_VALUE
import platform.windows.RemoveDirectoryW
import platform.windows.WIN32_FIND_DATAW

actual fun deleteFolder(path: String): Boolean = memScoped {
    val attributes = GetFileAttributesW(path)
    if (attributes == UInt.MAX_VALUE) return false
    if ((attributes and FILE_ATTRIBUTE_DIRECTORY.toUInt()) == 0u) {
        return DeleteFileW(path) != 0
    }
    // Remove directory links themselves, without traversing their targets.
    if ((attributes and FILE_ATTRIBUTE_REPARSE_POINT.toUInt()) != 0u) {
        return RemoveDirectoryW(path) != 0
    }

    val entry = alloc<WIN32_FIND_DATAW>()
    val handle = FindFirstFileW("$path/*", entry.ptr)
    if (handle == null || handle == INVALID_HANDLE_VALUE) return false
    try {
        do {
            val name = entry.cFileName.toKString()
            if (name != "." && name != ".." && !deleteFolder("$path/$name")) return false
        } while (FindNextFileW(handle, entry.ptr) != 0)
        if (GetLastError() != ERROR_NO_MORE_FILES.toUInt()) return false
    } finally {
        FindClose(handle)
    }
    RemoveDirectoryW(path) != 0
}
