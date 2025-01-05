package maryk

import platform.posix.opendir

actual fun doesFolderExist(path: String): Boolean {
    val directory = opendir(path)
    return directory != null
}
