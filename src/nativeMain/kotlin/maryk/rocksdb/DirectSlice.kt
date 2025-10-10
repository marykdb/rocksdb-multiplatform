package maryk.rocksdb

import kotlinx.cinterop.Arena
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.plus
import kotlinx.cinterop.ptr
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
        data = DirectByteBuffer(pointer, str.length)
    }

    actual constructor(data: ByteBuffer) : this() {
        this.data = data
    }

    actual constructor(data: ByteBuffer, length: Int) : this(
        DirectByteBuffer(data.nativePointer, length)
    )

    override fun getData(): ByteBuffer = data

    actual override operator fun get(offset: Int): Byte {
        return data[offset]
    }

    actual override fun clear() {
        data = DirectByteBuffer(emptyByte.ptr, 0)
    }

    actual override fun removePrefix(n: Int) {
        require(n < data.capacity)
        data = DirectByteBuffer(data.nativePointer.plus(n)!!, data.capacity - n)
    }

    override fun size(): Int = data.capacity

    override fun empty(): Boolean = data.capacity == 0

    @OptIn(ExperimentalStdlibApi::class, UnsafeNumber::class)
    override fun toString(hex: Boolean): String {
        return data.nativePointer.toByteArray(data.capacity.asSizeT()).let { bytes ->
            if (hex) bytes.toHexString() else bytes.decodeToString().replace("\u0000", "")
        }
    }
}
private val emptyByte = nativeHeap.alloc<ByteVar>()
actual val DirectSliceNone = DirectSlice(DirectByteBuffer(emptyByte.ptr, 0))
