package maryk

import kotlinx.cinterop.alloc
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSFileManager
import platform.Foundation.NSError
import platform.posix.RLIMIT_NOFILE
import platform.posix.getrlimit
import platform.posix.rlimit
import platform.posix.setrlimit

private val configureTestFileLimit by lazy {
    // Simulator launchd defaults to 256 descriptors. The lifecycle race tests
    // intentionally keep 1,024 WAL iterators open in this process at once.
    memScoped {
        val limit = alloc<rlimit>()
        check(getrlimit(RLIMIT_NOFILE, limit.ptr) == 0) { "Cannot read test file descriptor limit" }
        if (limit.rlim_cur < 4096uL) {
            limit.rlim_cur = minOf(4096uL, limit.rlim_max)
            check(limit.rlim_cur >= 4096uL && setrlimit(RLIMIT_NOFILE, limit.ptr) == 0) {
                "RocksDB lifecycle tests require a file descriptor limit of at least 4096"
            }
        }
    }
}

actual fun createFolder(path: String): Boolean {
    configureTestFileLimit
    memScoped {
        val errorRef = alloc<ObjCObjectVar<NSError?>>()
        val result = NSFileManager.defaultManager().createDirectoryAtPath(
            path = path,
            withIntermediateDirectories = true,
            attributes = null,
            error = errorRef.ptr
        )
        val error = errorRef.value

        if (error != null) {
            throw Exception(error.localizedDescription)
        }

        return result
    }
}
