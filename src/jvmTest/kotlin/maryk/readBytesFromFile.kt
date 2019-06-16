package maryk

import java.nio.file.Files.readAllBytes
import java.nio.file.Paths

actual fun readAllBytesFromFile(path: String): ByteArray = readAllBytes(
    Paths.get(
        path
    )
)
