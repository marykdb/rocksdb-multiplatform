package maryk

expect class AtomicInteger() {
    fun incrementAndGet(): Int
    fun get(): Int
}
