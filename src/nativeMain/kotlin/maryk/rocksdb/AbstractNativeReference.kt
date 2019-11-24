package maryk.rocksdb

import kotlin.native.concurrent.AtomicReference

actual abstract class AbstractNativeReference : AutoCloseable {
    private val isClosed = AtomicReference(false)

    open fun isOwningHandle(): Boolean {
        return !isClosed.value
    }

    override fun close() {
        isClosed.compareAndSet(expected = false, new = true)
    }
}
