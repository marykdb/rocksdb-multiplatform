@file:OptIn(UnsafeNumber::class)

package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.readBytes
import platform.posix.size_t

fun CPointer<ByteVar>.toByteArray(value: size_t) =
    this.readBytes(value.toInt())
