package maryk.rocksdb

import kotlinx.cinterop.Arena
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.plus
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set
import maryk.ByteBuffer
import maryk.DirectByteBuffer
import maryk.asSizeT
import maryk.toByteArray
import platform.posix.memcpy

actual class DirectSlice() : AbstractSlice<ByteBuffer>() {
    private val scope = Arena()
    private var scopeCleared = false
    override lateinit var data: ByteBuffer

    override fun disposeInternal() {
        clearScope()
        super.disposeInternal()
    }

    private fun clearScope() {
        if (!scopeCleared) {
            scope.clear()
            scopeCleared = true
        }
    }
    actual constructor(str: String) : this() {
        val bytes = str.encodeToByteArray()
        data = if (bytes.isEmpty()) {
            DirectByteBuffer(emptyByte.ptr, 0)
        } else {
            DirectByteBuffer(scope.allocArrayOf(bytes), bytes.size)
        }
    }

    actual constructor(data: ByteBuffer) : this() {
        require(data is DirectByteBuffer) { "DirectSlice requires a direct ByteBuffer" }
        this.data = scope.copyBuffer(data, data.capacity)
    }

    actual constructor(data: ByteBuffer, length: Int) : this() {
        require(data is DirectByteBuffer) { "DirectSlice requires a direct ByteBuffer" }
        require(length >= 0) { "length must be non-negative" }
        require(length <= data.capacity) { "length must not exceed buffer capacity" }
        this.data = scope.copyBuffer(data, length)
    }

    override fun getData(): ByteBuffer {
        checkOpen()
        return data
    }

    actual override operator fun get(offset: Int): Byte {
        checkOpen()
        return data[offset]
    }

    actual override fun clear() {
        checkOpen()
        clearScope()
        data = DirectByteBuffer(emptyByte.ptr, 0)
    }

    actual override fun removePrefix(n: Int) {
        checkOpen()
        require(n >= 0) { "prefix length must be non-negative" }
        require(n <= data.capacity) { "prefix length must not exceed slice length" }
        data = DirectByteBuffer(data.nativePointer.plus(n)!!, data.capacity - n)
    }

    override fun size(): Int {
        checkOpen()
        return data.capacity
    }

    override fun empty(): Boolean {
        checkOpen()
        return data.capacity == 0
    }

    @OptIn(ExperimentalStdlibApi::class, UnsafeNumber::class)
    override fun toString(hex: Boolean): String {
        checkOpen()
        return if (hex) {
            data.nativePointer.toByteArray(data.capacity.asSizeT()).toHexString()
        } else {
            val length = data.nativePointer.lengthUntilNullByte(data.capacity)
            data.nativePointer.readBytes(length).decodeToString()
        }
    }
}
private val emptyByte = nativeHeap.alloc<ByteVar>()
actual val DirectSliceNone = DirectSlice(DirectByteBuffer(emptyByte.ptr, 0))

private fun Arena.copyBuffer(buffer: ByteBuffer, length: Int): DirectByteBuffer {
    if (length == 0) {
        return DirectByteBuffer(emptyByte.ptr, 0)
    }

    val pointer: CPointer<ByteVar> = allocArray(length)
    memcpy(pointer, buffer.nativePointer, length.asSizeT())
    return DirectByteBuffer(pointer, length)
}

private fun CPointer<ByteVar>.lengthUntilNullByte(maxLength: Int): Int {
    for (index in 0 until maxLength) {
        if (this[index] == 0.toByte()) {
            return index
        }
    }
    return maxLength
}
