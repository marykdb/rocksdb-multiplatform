package maryk.rocksdb

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/** Serializes creation and destruction of native children with destruction of their owner. */
internal class NativeLifecycleGuard : SynchronizedObject() {
    internal inline fun <T> withLock(block: () -> T): T = synchronized(this, block)
}
