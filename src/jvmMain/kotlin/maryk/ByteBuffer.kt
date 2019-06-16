package maryk

actual typealias ByteBuffer = java.nio.ByteBuffer

actual fun allocateByteBuffer(capacity: Int)  = ByteBuffer.allocate(capacity)
actual fun allocateDirectByteBuffer(capacity: Int) = ByteBuffer.allocateDirect(capacity)
actual fun wrapByteBuffer(bytes: ByteArray) = ByteBuffer.wrap(bytes)
