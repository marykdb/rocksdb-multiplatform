package maryk

import kotlinx.cinterop.alloc
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSFileManager
import platform.Foundation.NSError

actual fun createFolder(path: String): Boolean {
    memScoped {
        val errorRef = alloc<ObjCObjectVar<NSError?>>()
        val result = NSFileManager.defaultManager().createDirectoryAtPath(
            path = path,
            withIntermediateDirectories = true,
            attributes = null,
            error = errorRef.ptr
        )
        val error = errorRef.value

        if (error != null) {
            throw Exception(error.localizedDescription)
        }

        return result
    }
}
