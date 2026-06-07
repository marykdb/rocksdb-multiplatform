@file:OptIn(UnsafeNumber::class)

package maryk

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import maryk.asSizeT
import platform.posix.memcpy

private val MAX_BYTE = 0b1111_1111.toUByte()

private fun CPointer<ByteVar>.offsetBy(bytes: Int): CPointer<ByteVar> =
    requireNotNull(plus(bytes)) { "pointer offset produced a null pointer" }

actual abstract class ByteBuffer(
    internal val nativePointer: CPointer<ByteVar>,
    capacity: Int
) : Buffer(capacity, capacity) {
    internal fun checkIndex(index: Int) {
        if (index < 0 || index >= limit) {
            throw IndexOutOfBoundsException("Index $index out of bounds for buffer limit $limit")
        }
    }

    actual final override fun array(): ByteArray {
        return nativePointer.readBytes(capacity)
    }

    actual fun put(src: ByteArray): ByteBuffer {
        return put(src, 0, src.size)
    }

    actual fun put(src: ByteArray, offset: Int, length: Int): ByteBuffer {
        if (offset < 0 || length < 0 || length > src.size - offset) {
            throw IndexOutOfBoundsException("offset=$offset, length=$length, source size=${src.size}")
        }
        if (length > remaining()) {
            throw IndexOutOfBoundsException("length=$length, remaining=${remaining()}")
        }
        if (length > 0) {
            src.usePinned { pinned ->
                memcpy(nativePointer.offsetBy(position), pinned.addressOf(offset), length.asSizeT())
            }
            position += length
        }
        return this
    }

    actual operator fun get(dst: ByteArray): ByteBuffer {
        return this[dst, 0, dst.size]
    }

    actual operator fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer {
        if (offset < 0 || length < 0 || length > dst.size - offset) {
            throw IndexOutOfBoundsException("offset=$offset, length=$length, destination size=${dst.size}")
        }
        if (length > remaining()) {
            throw IndexOutOfBoundsException("length=$length, remaining=${remaining()}")
        }
        if (length > 0) {
            dst.usePinned { pinned ->
                memcpy(pinned.addressOf(offset), nativePointer.offsetBy(position), length.asSizeT())
            }
            position += length
        }
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

    actual abstract fun getInt(): Int

    internal fun readInt(): Int {
        if (position + 4 > limit) {
            throw IllegalStateException("Not enough bytes left for int")
        }
        var int = 0 xor (this[position++].toInt() and 0xFF)
        for (it in 1 until 4) {
            int = int shl 8
            int = int xor (this[position++].toInt() and 0xFF)
        }
        return int
    }
}

class DirectByteBuffer internal constructor(
    nativePointer: CPointer<ByteVar>,
    capacity: Int
) : ByteBuffer(nativePointer, capacity) {
    override fun get(index: Int): Byte {
        checkIndex(index)
        return nativePointer[index]
    }

    override fun getInt() = readInt()

    override fun put(index: Int, byte: Byte): ByteBuffer {
        checkIndex(index)
        nativePointer[index] = byte
        return this
    }
}

class HeapByteBuffer internal constructor(
    nativePointer: CPointer<ByteVar>,
    capacity: Int
) : ByteBuffer(nativePointer, capacity) {
    override fun get(index: Int): Byte {
        checkIndex(index)
        return nativePointer[index]
    }

    override fun getInt() = readInt()

    override fun put(index: Int, byte: Byte): ByteBuffer {
        checkIndex(index)
        nativePointer[index] = byte
        return this
    }
}

actual fun duplicateByteBuffer(byteBuffer: ByteBuffer, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) {
    memScoped {
        val pointer = if (byteBuffer.capacity == 0) {
            allocArray<ByteVar>(1)
        } else {
            val pointer = allocArray<ByteVar>(byteBuffer.capacity)
            memcpy(pointer, byteBuffer.nativePointer, byteBuffer.capacity.asSizeT())
            pointer
        }

        val duplicate = if (byteBuffer is DirectByteBuffer) {
            DirectByteBuffer(pointer, byteBuffer.capacity)
        } else {
            HeapByteBuffer(pointer, byteBuffer.capacity)
        }
        memSafeByteBuffer(duplicate)
    }
}

actual fun allocateByteBuffer(capacity: Int, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) {
    require(capacity >= 0) { "capacity must be non-negative" }
    memScoped {
        val pointer = allocArray<ByteVar>(if (capacity == 0) 1 else capacity)
        memSafeByteBuffer(HeapByteBuffer(pointer, capacity))
    }
}

actual fun allocateDirectByteBuffer(capacity: Int, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) {
    require(capacity >= 0) { "capacity must be non-negative" }
    memScoped {
        val pointer = allocArray<ByteVar>(if (capacity == 0) 1 else capacity)
        memSafeByteBuffer(DirectByteBuffer(pointer, capacity))
    }
}

actual fun wrapByteBuffer(bytes: ByteArray, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) {
    memScoped {
        val pointer = if (bytes.isEmpty()) {
            allocArray<ByteVar>(1)
        } else {
            val pointer = allocArray<ByteVar>(bytes.size)
            bytes.usePinned { pinned ->
                memcpy(pointer, pinned.addressOf(0), bytes.size.asSizeT())
            }
            pointer
        }
        memSafeByteBuffer(HeapByteBuffer(pointer, bytes.size))
    }
}

actual fun ByteBuffer.flip() {
    this.flip()
}

actual fun ByteBuffer.limit(newLimit: Int) {
    this.limit(newLimit)
}
