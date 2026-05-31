package maryk.rocksdb

import kotlin.concurrent.AtomicInt

actual abstract class RocksMutableObject : AbstractNativeReference() {
    private val isClosed = AtomicInt(0)

    protected abstract fun disposeInternal()

    protected fun checkOpen() {
        check(isClosed.value == 0) { "Native handle is already closed." }
    }

    actual override final fun close() {
        if (isClosed.compareAndSet(0, 1)) {
            disposeInternal()
        }
    }
}
