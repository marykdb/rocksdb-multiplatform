package maryk

import kotlinx.cinterop.*
import platform.Foundation.*

actual fun doesFolderExist(path: String): Boolean {
    return memScoped {
        val isDir = alloc<BooleanVar>()
        val fileManager = NSFileManager.defaultManager()
        val exists = fileManager.fileExistsAtPath(path, isDirectory = isDir.ptr)
        exists && isDir.value
    }
}
