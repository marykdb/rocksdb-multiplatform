package maryk

import java.io.File

actual fun deleteFolder(path: String): Boolean {
    return File(path).deleteRecursively()
}
