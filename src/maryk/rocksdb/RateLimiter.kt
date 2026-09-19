package maryk.rocksdb

expect class RateLimiter : RocksObject {
    constructor(rateBytesPerSecond: Long)
    constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long)
    constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int)

    fun setBytesPerSecond(rateBytesPerSecond: Long)
    fun getBytesPerSecond(): Long
}
