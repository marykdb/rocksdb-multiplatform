package maryk

import platform.Foundation.NSFileManager

actual fun doesFolderExist(path: String) =
    NSFileManager.defaultManager().fileExistsAtPath(path)
