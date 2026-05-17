package maryk.rocksdb

import kotlin.concurrent.AtomicInt

actual abstract class RocksMutableObject : AbstractNativeReference() {
    private val isClosed = AtomicInt(0)

    protected abstract fun disposeInternal()

    actual override final fun close() {
        if (isClosed.compareAndSet(0, 1)) {
            disposeInternal()
        }
    }
}
