package maryk.rocksdb

import kotlinx.cinterop.Arena
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.plus
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import maryk.ByteBuffer
import maryk.DirectByteBuffer
import maryk.asSizeT
import maryk.toByteArray

actual class DirectSlice() : AbstractSlice<ByteBuffer>() {
    private val scope = Arena()
    override lateinit var data: ByteBuffer

    override fun disposeInternal() {
        scope.clear()
        super.disposeInternal()
    }
    actual constructor(str: String) : this() {
        val bytes = str.encodeToByteArray()
        val pointer = scope.allocArrayOf(bytes)
        data = DirectByteBuffer(pointer, bytes.size)
    }

    actual constructor(data: ByteBuffer) : this() {
        require(data is DirectByteBuffer) { "DirectSlice requires a direct ByteBuffer" }
        this.data = scope.copyBuffer(data, data.capacity)
    }

    actual constructor(data: ByteBuffer, length: Int) : this() {
        require(data is DirectByteBuffer) { "DirectSlice requires a direct ByteBuffer" }
        require(length <= data.capacity) { "length must not exceed buffer capacity" }
        this.data = scope.copyBuffer(data, length)
    }

    override fun getData(): ByteBuffer = data

    actual override operator fun get(offset: Int): Byte {
        return data[offset]
    }

    actual override fun clear() {
        data = DirectByteBuffer(emptyByte.ptr, 0)
    }

    actual override fun removePrefix(n: Int) {
        require(n <= data.capacity)
        data = DirectByteBuffer(data.nativePointer.plus(n)!!, data.capacity - n)
    }

    override fun size(): Int = data.capacity

    override fun empty(): Boolean = data.capacity == 0

    @OptIn(ExperimentalStdlibApi::class, UnsafeNumber::class)
    override fun toString(hex: Boolean): String {
        return data.nativePointer.toByteArray(data.capacity.asSizeT()).let { bytes ->
            if (hex) {
                bytes.toHexString()
            } else {
                bytes.takeWhile { it != 0.toByte() }.toByteArray().decodeToString()
            }
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
    for (index in 0 until length) {
        pointer[index] = buffer[index]
    }
    return DirectByteBuffer(pointer, length)
}
