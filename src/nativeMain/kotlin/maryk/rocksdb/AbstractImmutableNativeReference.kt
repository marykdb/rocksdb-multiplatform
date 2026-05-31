package maryk.rocksdb

import kotlin.concurrent.AtomicInt

actual abstract class AbstractImmutableNativeReference(): AbstractNativeReference()  {
    // 0 = open/owning, 1 = closed, 2 = transferred, 3 = borrowed wrapper.
    private val ownershipState = AtomicInt(0)

    actual open fun isOwningHandle(): Boolean {
        return ownershipState.value == 0
    }

    internal fun checkOwningHandle() {
        check(isOwningHandle()) { "Native handle is already closed or transferred." }
    }

    internal fun checkOpenHandle() {
        check(ownershipState.value == 0 || ownershipState.value == 3) {
            "Native handle is already closed or transferred."
        }
    }

    internal fun disownHandle(): Boolean {
        return ownershipState.compareAndSet(0, 2)
    }

    internal fun borrowHandle(): Boolean {
        return ownershipState.compareAndSet(0, 3)
    }

    internal fun tryClose(): Boolean {
        return ownershipState.compareAndSet(0, 1)
    }

    internal fun tryCloseTransferred(): Boolean {
        return ownershipState.compareAndSet(2, 1)
    }

    internal fun tryCloseBorrowed(): Boolean {
        return ownershipState.compareAndSet(3, 1)
    }

    actual override fun close() {
        tryClose()
    }
}
