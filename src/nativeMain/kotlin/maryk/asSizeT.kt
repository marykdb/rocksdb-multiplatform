package maryk

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.posix.size_t

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
fun Int.asSizeT(): size_t {
    require(this >= 0) { "size_t value must be non-negative: $this" }
    return toLong().asSizeT()
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
fun Long.asSizeT(): size_t {
    require(this >= 0) { "size_t value must be non-negative: $this" }
    val converted = this.convert<size_t>()
    require(converted.convert<ULong>() == this.toULong()) { "size_t value is too large: $this" }
    return converted
}

fun Long.asUInt64(): ULong {
    require(this >= 0) { "uint64_t value must be non-negative: $this" }
    return this.toULong()
}

fun Int.asUInt32(): UInt {
    require(this >= 0) { "uint32_t value must be non-negative: $this" }
    return this.toUInt()
}

fun ULong.toCheckedLong(description: String): Long {
    require(this <= Long.MAX_VALUE.toULong()) { "$description is too large for Long: $this" }
    return toLong()
}

fun UInt.toCheckedInt(description: String): Int {
    require(this <= Int.MAX_VALUE.toUInt()) { "$description is too large for Int: $this" }
    return toInt()
}
