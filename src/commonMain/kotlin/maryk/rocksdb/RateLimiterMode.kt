package maryk.rocksdb

expect enum class RateLimiterMode {
    READS_ONLY,
    WRITES_ONLY,
    ALL_IO
}
