package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.set

fun MemScope.byteArrayToCPointer(
    key: ByteArray,
    offset: Int,
    len: Int
): CArrayPointer<ByteVar> {
    if (offset < 0 || len < 0 || offset > key.size - len) {
        throw IndexOutOfBoundsException("offset=$offset, length=$len, array size=${key.size}")
    }
    val cKey = allocArray<ByteVar>(len)
    for (i in (0 until len)) {
        cKey[i] = key[i + offset]
    }
    return cKey
}
