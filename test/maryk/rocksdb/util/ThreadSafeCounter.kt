package maryk.rocksdb.util

expect class ThreadSafeCounter() {
    fun increment()
    fun value(): Int
}
