package maryk.rocksdb

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.wcstr
import platform.posix.SEEK_END
import platform.posix._wfopen
import platform.posix.fclose
import platform.posix.fseek
import platform.posix.ftell

internal actual fun traceFileSize(path: String): Long = memScoped {
    val file = requireNotNull(_wfopen(path.wcstr.ptr, "rb".wcstr.ptr)) { "Unable to open trace file: $path" }
    try {
        check(fseek(file, 0, SEEK_END) == 0) { "Unable to seek trace file: $path" }
        ftell(file).toLong().also { check(it >= 0) { "Unable to measure trace file: $path" } }
    } finally {
        fclose(file)
    }
}
