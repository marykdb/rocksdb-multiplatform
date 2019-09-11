package maryk.rocksdb

import maryk.ByteBuffer

actual class DirectSlice internal constructor() : AbstractSlice<ByteBuffer>() {
    actual constructor(str: String) : this() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual constructor(data: ByteBuffer, length: Int) : this() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual constructor(data: ByteBuffer) : this() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual operator fun get(offset: Int): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun clear() {
    }

    actual override fun removePrefix(n: Int) {
    }
}

actual val DirectSliceNone = DirectSlice();
