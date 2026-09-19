package maryk.rocksdb.util

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
actual class ThreadSafeCounter actual constructor() {
    private val counter = AtomicInt(0)

    actual fun increment() {
        counter.incrementAndFetch()
    }

    actual fun value(): Int = counter.load()
}
