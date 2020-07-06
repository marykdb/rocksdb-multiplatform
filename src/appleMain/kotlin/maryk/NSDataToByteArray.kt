package maryk

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get
import platform.Foundation.NSData
import platform.posix.uint8_tVar

@SharedImmutable
private val emptyByteArray = byteArrayOf()

fun NSData.toByteArray(): ByteArray {
    @Suppress("UNCHECKED_CAST")
    val bytePtr = (this.bytes as? CPointer<uint8_tVar>)
        ?: return emptyByteArray

    return ByteArray(this.length.toInt()) { index ->
        bytePtr[index].toByte()
    }
}

/**
 * Write NSData bytes into given [byteArray] and returns length of NSData size
 * If byteArray is smaller than the NSData length it will only fill available
 * space but still return the needed size.
 */
fun NSData.intoByteArray(byteArray: ByteArray, offset: Int = 0, length: Int = byteArray.size): Int {
    @Suppress("UNCHECKED_CAST")
    val bytePtr = (this.bytes as CPointer<uint8_tVar>)

    val amountToFill = minOf(this.length.toInt(), length)

    for (index in offset until amountToFill) {
        byteArray[index] = bytePtr[index].toByte()
    }

    return this.length.toInt()
}
