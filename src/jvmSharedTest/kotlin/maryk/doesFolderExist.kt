package maryk

import java.io.File

actual fun doesFolderExist(path: String) =
    File(path).exists()
