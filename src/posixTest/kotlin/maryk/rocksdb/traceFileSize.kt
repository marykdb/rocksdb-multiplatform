package maryk.rocksdb

import platform.posix.SEEK_END
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fseek
import platform.posix.ftell

internal actual fun traceFileSize(path: String): Long {
    val file = requireNotNull(fopen(path, "rb")) { "Unable to open trace file: $path" }
    return try {
        check(fseek(file, 0, SEEK_END) == 0) { "Unable to seek trace file: $path" }
        ftell(file).toLong().also { check(it >= 0) { "Unable to measure trace file: $path" } }
    } finally {
        fclose(file)
    }
}
