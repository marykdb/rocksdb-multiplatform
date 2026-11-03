package maryk.rocksdb

expect class RateLimiter : RocksObject {
    constructor(rateBytesPerSecond: Long)
    constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long)
    constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int)

    constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int, rateLimiterMode: RateLimiterMode)
    constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int, rateLimiterMode: RateLimiterMode, autoTune: Boolean)

    /** Maximum number of bytes the limiter can grant in a single request. */
    fun getSingleBurstBytes(): Long
    /** Total bytes and requests processed by real RocksDB I/O through this limiter. */
    fun getTotalBytesThrough(): Long
    fun getTotalRequests(): Long

    fun setBytesPerSecond(rateBytesPerSecond: Long)
    fun getBytesPerSecond(): Long
}
