package maryk.rocksdb.util

import java.util.concurrent.atomic.AtomicInteger

actual class ThreadSafeCounter actual constructor() {
    private val counter = AtomicInteger(0)

    actual fun increment() {
        counter.incrementAndGet()
    }

    actual fun value(): Int = counter.get()
}
