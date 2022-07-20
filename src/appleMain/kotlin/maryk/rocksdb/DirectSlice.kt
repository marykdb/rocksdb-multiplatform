package maryk.rocksdb

import kotlinx.cinterop.AutofreeScope
import maryk.ByteBuffer
import maryk.DirectByteBuffer
import maryk.WrappedByteBuffer
import rocksdb.RocksDBSlice

actual class DirectSlice internal constructor(
    native: (scope: AutofreeScope) -> RocksDBSlice = { RocksDBSlice() }
) : AbstractSlice<ByteBuffer>(native) {
    actual constructor(str: String) : this({ RocksDBSlice(str) })

    actual constructor(data: ByteBuffer, length: Int) : this({ RocksDBSlice(data.nativePointer, length.toULong()) }) {
        if (data is WrappedByteBuffer) {
            throw IllegalArgumentException("Cannot use non direct byte buffer with DirectSlice")
        }
    }

    actual constructor(data: ByteBuffer) : this({ RocksDBSlice(data.nativePointer) }) {
        if (data is WrappedByteBuffer) {
            throw IllegalArgumentException("Cannot use non direct byte buffer with DirectSlice")
        }
    }

    override fun getData(): ByteBuffer {
        return DirectByteBuffer(native.data()!!, native.size().toInt())
    }

    actual operator fun get(offset: Int): Byte {
        return native.get(offset)
    }

    actual override fun clear() {
        native.clear()
    }

    actual override fun removePrefix(n: Int) {
        native.removePrefix(n.toULong())
    }
}

actual val DirectSliceNone = DirectSlice()
