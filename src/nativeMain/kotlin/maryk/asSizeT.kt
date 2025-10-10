package maryk

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.posix.size_t

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
fun Int.asSizeT(): size_t {
    require(this >= 0) { "Negative size" }
    return this.convert()
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
fun Long.asSizeT(): size_t {
    require(this >= 0) { "Negative size" }
    return this.convert()
}
