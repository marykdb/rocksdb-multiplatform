package maryk

import java.io.File

actual fun createFolder(path: String) {
    File(path).mkdirs();
}
