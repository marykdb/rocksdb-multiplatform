package maryk

actual abstract class ByteBuffer : Buffer() {
    actual final override fun array(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun put(src: ByteArray): ByteBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual abstract fun put(index: Int, byte: Byte): ByteBuffer
    actual operator fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual operator fun get(dst: ByteArray): ByteBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual abstract fun putLong(l: Long): ByteBuffer
    actual abstract fun getLong(): Long
    actual abstract fun putInt(i: Int): ByteBuffer
}

actual fun allocateByteBuffer(capacity: Int): ByteBuffer {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun allocateDirectByteBuffer(capacity: Int): ByteBuffer {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun wrapByteBuffer(bytes: ByteArray): ByteBuffer {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
