package maryk.rocksdb

import kotlin.concurrent.AtomicReference

actual abstract class AbstractImmutableNativeReference(): AbstractNativeReference()  {
    private val isClosed = AtomicReference(false)

    open fun isOwningHandle(): Boolean {
        return !isClosed.value
    }

    actual override fun close() {
        isClosed.compareAndSet(false, true)
    }
}
