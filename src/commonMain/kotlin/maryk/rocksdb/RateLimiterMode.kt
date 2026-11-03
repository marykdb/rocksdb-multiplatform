package maryk.rocksdb

/** I/O counted against a rate limiter's budget. */
expect enum class RateLimiterMode { READS_ONLY, WRITES_ONLY, ALL_IO }
