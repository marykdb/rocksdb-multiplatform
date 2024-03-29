package maryk

expect abstract class ByteBuffer : Buffer {
    final override fun array(): ByteArray
    fun put(src: ByteArray): ByteBuffer
    abstract operator fun get(index: Int): Byte
    operator fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer
    operator fun get(dst: ByteArray): ByteBuffer
    abstract fun put(index: Int, byte: Byte): ByteBuffer
    abstract fun getInt(): Int
}

internal expect fun duplicateByteBuffer(byteBuffer: ByteBuffer, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit)

internal expect fun allocateByteBuffer(capacity: Int, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit)

internal expect fun allocateDirectByteBuffer(capacity: Int, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit)

internal expect fun wrapByteBuffer(bytes: ByteArray, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit)
