package maryk

import java.io.File

actual fun createFile(path: String, fileName: String): String {
    val dir = File(path)
    dir.mkdirs()
    val file = File("$path/$fileName")
    file.createNewFile()
    return file.absolutePath
}
