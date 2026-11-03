package maryk.rocksdb

actual enum class RateLimiterMode(internal val value: Int) {
    READS_ONLY(0), WRITES_ONLY(1), ALL_IO(2)
}
