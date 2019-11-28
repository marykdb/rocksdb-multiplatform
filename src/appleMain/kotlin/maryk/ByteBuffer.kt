package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set

private val MAX_BYTE = 0b1111_1111.toUByte()

actual abstract class ByteBuffer(
    internal val nativePointer: CPointer<ByteVar>,
    capacity: Int
) : Buffer(capacity) {
    actual final override fun array(): ByteArray {
        return nativePointer.readBytes(capacity)
    }

    actual fun put(src: ByteArray): ByteBuffer {
        for (byte in src) {
            nativePointer[position++] = byte
        }
        return this
    }

    actual operator fun get(dst: ByteArray): ByteBuffer {
        return this[dst, 0, dst.size]
    }

    actual operator fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer {
        if (length > capacity - position) {
            throw Exception("Not enough bytes left to fill destination byte array")
        }
        for (index in 0 until length) {
            dst[index + offset] = nativePointer[index + position]
        }
        position += dst.size
        return this
    }

    actual abstract fun put(index: Int, byte: Byte): ByteBuffer

    fun compareTo(other: ByteBuffer): Int {
        for (it in 0 until minOf(this.capacity, other.capacity)) {
            val a = nativePointer[it].toUByte() and MAX_BYTE
            val b = other.nativePointer[it].toUByte() and MAX_BYTE
            if (a != b) {
                return a.toInt() - b.toInt()
            }
        }
        return this.capacity - other.capacity
    }

    actual abstract operator fun get(index: Int): Byte
}

class DirectByteBuffer internal constructor(
    nativePointer: CPointer<ByteVar>,
    capacity: Int
) : ByteBuffer(nativePointer, capacity) {
    override fun get(index: Int) = nativePointer[index]

    override fun put(index: Int, byte: Byte): ByteBuffer {
        nativePointer[index] = byte
        return this
    }
}

class WrappedByteBuffer internal constructor(
    nativePointer: CPointer<ByteVar>,
    capacity: Int
) : ByteBuffer(nativePointer, capacity) {
    override fun get(index: Int) = nativePointer[index]

    override fun put(index: Int, byte: Byte): ByteBuffer {
        nativePointer[index] = byte
        return this
    }
}

actual fun allocateByteBuffer(capacity: Int): ByteBuffer {
    return WrappedByteBuffer(nativeHeap.allocArray(capacity), capacity)
}

actual fun allocateDirectByteBuffer(capacity: Int): ByteBuffer {
    return DirectByteBuffer(nativeHeap.allocArray(capacity), capacity)
}

actual fun wrapByteBuffer(bytes: ByteArray): ByteBuffer {
    return WrappedByteBuffer(bytes.toCPointer(), bytes.size)
}
