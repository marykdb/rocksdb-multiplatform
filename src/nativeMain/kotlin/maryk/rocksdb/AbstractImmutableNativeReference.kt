package maryk.rocksdb

import kotlin.concurrent.AtomicReference
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.identityHashCode

actual abstract class AbstractImmutableNativeReference(): AbstractNativeReference()  {
    private val isClosed = AtomicReference(false)

    actual open fun isOwningHandle(): Boolean {
        return !isClosed.value
    }

    internal fun disownHandle() {
        isClosed.getAndSet(true)
    }

    actual override fun close() {
        isClosed.compareAndSet(false, true)
    }
}
