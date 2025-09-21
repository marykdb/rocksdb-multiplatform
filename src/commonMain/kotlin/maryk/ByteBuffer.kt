package maryk

expect sealed class ByteBuffer : Buffer {
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

// because of java 1.8 and java 9 incompatibilities and Android using 1.8 interface, these two methods are needed
internal expect fun ByteBuffer.flip()
internal expect fun ByteBuffer.limit(newLimit: Int)
