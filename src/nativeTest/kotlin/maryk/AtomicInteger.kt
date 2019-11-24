package maryk

import kotlin.native.concurrent.AtomicInt

actual class AtomicInteger actual constructor() {
    private val atomic = AtomicInt(0)
    actual fun incrementAndGet() = atomic.addAndGet(1)

    actual fun get() = atomic.value
}
