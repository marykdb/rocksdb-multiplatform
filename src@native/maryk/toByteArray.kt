@file:OptIn(UnsafeNumber::class)

package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlinx.cinterop.readBytes
import platform.posix.size_t

fun sizeTToInt(value: size_t, description: String = "native allocation"): Int {
    if (value > Int.MAX_VALUE.convert<size_t>()) {
        throw IllegalStateException("$description is too large for a Kotlin ByteArray: $value bytes")
    }
    return value.convert()
}

fun sizeTToLong(value: size_t, description: String = "native value"): Long {
    val asULong = value.convert<ULong>()
    if (asULong > Long.MAX_VALUE.toULong()) {
        throw IllegalStateException("$description is too large for Long: $value")
    }
    return asULong.toLong()
}

fun CPointer<ByteVar>.toByteArray(value: size_t) =
    this.readBytes(sizeTToInt(value))
