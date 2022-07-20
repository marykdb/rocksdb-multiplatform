package maryk

actual typealias ByteBuffer = java.nio.ByteBuffer

actual fun allocateByteBuffer(capacity: Int, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) = memSafeByteBuffer(ByteBuffer.allocate(capacity))
actual fun allocateDirectByteBuffer(capacity: Int, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) = memSafeByteBuffer(ByteBuffer.allocateDirect(capacity))
actual fun wrapByteBuffer(bytes: ByteArray, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) = memSafeByteBuffer(ByteBuffer.wrap(bytes))

// Separate function because Java 9 changes bytebuffer signature since it was added to Buffer
// If Android supports proper duplicate it could be added back to ByteBuffer
actual fun duplicateByteBuffer(byteBuffer: ByteBuffer, memSafeByteBuffer: (buffer: ByteBuffer) -> Unit) = memSafeByteBuffer(byteBuffer.duplicate())
