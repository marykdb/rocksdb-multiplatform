package maryk.rocksdb

import kotlin.concurrent.AtomicInt

actual abstract class AbstractImmutableNativeReference(): AbstractNativeReference()  {
    private val isClosed = AtomicInt(0)

    actual open fun isOwningHandle(): Boolean {
        return isClosed.value == 0
    }

    internal fun disownHandle() {
        isClosed.getAndSet(1)
    }

    actual override fun close() {
        isClosed.compareAndSet(0, 1)
    }
}
