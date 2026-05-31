package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

fun MemScope.byteArrayToCPointer(
    key: ByteArray,
    offset: Int,
    len: Int
): CArrayPointer<ByteVar> {
    if (offset < 0 || len < 0 || offset > key.size - len) {
        throw IndexOutOfBoundsException("offset=$offset, length=$len, array size=${key.size}")
    }
    val cKey = allocArray<ByteVar>(if (len == 0) 1 else len)
    if (len > 0) {
        key.usePinned { pinned ->
            memcpy(cKey, pinned.addressOf(offset), len.asSizeT())
        }
    }
    return cKey
}
