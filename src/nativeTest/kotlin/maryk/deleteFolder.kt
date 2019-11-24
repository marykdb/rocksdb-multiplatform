package maryk

import platform.Foundation.NSFileManager

actual fun deleteFolder(path: String): Boolean {
    return Unit.wrapWithErrorThrower { error ->
        NSFileManager.defaultManager().removeItemAtPath(path, error)
    }
}
