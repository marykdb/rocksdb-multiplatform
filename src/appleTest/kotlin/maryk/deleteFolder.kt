package maryk

import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import kotlinx.cinterop.*

actual fun deleteFolder(path: String): Boolean {
    memScoped {
        val errorRef = alloc<ObjCObjectVar<NSError?>>()
        val result = NSFileManager.defaultManager().removeItemAtPath(
            path = path,
            error = errorRef.ptr
        )
        val error = errorRef.value

        if (error != null) {
            throw Exception(error.localizedDescription)
        }

        return result
    }
}
