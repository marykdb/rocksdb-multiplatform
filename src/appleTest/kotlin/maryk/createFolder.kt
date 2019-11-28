package maryk

import platform.Foundation.NSFileManager

actual fun createFolder(path: String) = Unit.wrapWithErrorThrower { error ->
    NSFileManager.defaultManager().createDirectoryAtPath(
        path = path,
        withIntermediateDirectories = true,
        attributes = null,
        error = error
    )
}
