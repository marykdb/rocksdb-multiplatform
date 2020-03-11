package maryk

expect abstract class ByteBuffer : Buffer {
    final override fun array(): ByteArray
    fun put(src: ByteArray): ByteBuffer
    abstract operator fun get(index: Int): Byte
    operator fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer
    operator fun get(dst: ByteArray): ByteBuffer
    abstract fun put(index: Int, byte: Byte): ByteBuffer
    abstract fun duplicate(): ByteBuffer
    abstract fun getInt(): Int
}

expect fun allocateByteBuffer(capacity: Int): ByteBuffer

expect fun allocateDirectByteBuffer(capacity: Int): ByteBuffer

expect fun wrapByteBuffer(bytes: ByteArray): ByteBuffer
