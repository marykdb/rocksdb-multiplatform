package maryk.rocksdb

expect class RateLimiter : RocksObject {
    /**
     * RateLimiter constructor
     *
     * @param rateBytesPerSecond this is the only parameter you want to set
     * most of the time. It controls the total write rate of compaction
     * and flush in bytes per second. Currently, RocksDB does not enforce
     * rate limit for anything other than flush and compaction, e.g. write to
     * WAL.
     */
    constructor(rateBytesPerSecond: Long)

    /**
     * RateLimiter constructor
     *
     * @param rateBytesPerSecond this is the only parameter you want to set
     * most of the time. It controls the total write rate of compaction
     * and flush in bytes per second. Currently, RocksDB does not enforce
     * rate limit for anything other than flush and compaction, e.g. write to
     * WAL.
     * @param refillPeriodMicros this controls how often tokens are refilled. For
     * example,
     * when rate_bytes_per_sec is set to 10MB/s and refill_period_us is set to
     * 100ms, then 1MB is refilled every 100ms internally. Larger value can
     * lead to burstier writes while smaller value introduces more CPU
     * overhead. The default of 100,000ms should work for most cases.
     */
    constructor(
        rateBytesPerSecond: Long,
        refillPeriodMicros: Long
    )

    /**
     * RateLimiter constructor
     *
     * @param rateBytesPerSecond this is the only parameter you want to set
     * most of the time. It controls the total write rate of compaction
     * and flush in bytes per second. Currently, RocksDB does not enforce
     * rate limit for anything other than flush and compaction, e.g. write to
     * WAL.
     * @param refillPeriodMicros this controls how often tokens are refilled. For
     * example,
     * when rate_bytes_per_sec is set to 10MB/s and refill_period_us is set to
     * 100ms, then 1MB is refilled every 100ms internally. Larger value can
     * lead to burstier writes while smaller value introduces more CPU
     * overhead. The default of 100,000ms should work for most cases.
     * @param fairness RateLimiter accepts high-pri requests and low-pri requests.
     * A low-pri request is usually blocked in favor of hi-pri request.
     * Currently, RocksDB assigns low-pri to request from compaction and
     * high-pri to request from flush. Low-pri requests can get blocked if
     * flush requests come in continuously. This fairness parameter grants
     * low-pri requests permission by fairness chance even though high-pri
     * requests exist to avoid starvation.
     * You should be good by leaving it at default 10.
     */
    constructor(
        rateBytesPerSecond: Long,
        refillPeriodMicros: Long, fairness: Int
    )

    /**
     * RateLimiter constructor
     *
     * @param rateBytesPerSecond this is the only parameter you want to set
     * most of the time. It controls the total write rate of compaction
     * and flush in bytes per second. Currently, RocksDB does not enforce
     * rate limit for anything other than flush and compaction, e.g. write to
     * WAL.
     * @param refillPeriodMicros this controls how often tokens are refilled. For
     * example,
     * when rate_bytes_per_sec is set to 10MB/s and refill_period_us is set to
     * 100ms, then 1MB is refilled every 100ms internally. Larger value can
     * lead to burstier writes while smaller value introduces more CPU
     * overhead. The default of 100,000ms should work for most cases.
     * @param fairness RateLimiter accepts high-pri requests and low-pri requests.
     * A low-pri request is usually blocked in favor of hi-pri request.
     * Currently, RocksDB assigns low-pri to request from compaction and
     * high-pri to request from flush. Low-pri requests can get blocked if
     * flush requests come in continuously. This fairness parameter grants
     * low-pri requests permission by fairness chance even though high-pri
     * requests exist to avoid starvation.
     * You should be good by leaving it at default 10.
     * @param rateLimiterMode indicates which types of operations count against
     * the limit.
     */
    constructor(
        rateBytesPerSecond: Long,
        refillPeriodMicros: Long, fairness: Int,
        rateLimiterMode: RateLimiterMode
    )

    /**
     * RateLimiter constructor
     *
     * @param rateBytesPerSecond this is the only parameter you want to set
     * most of the time. It controls the total write rate of compaction
     * and flush in bytes per second. Currently, RocksDB does not enforce
     * rate limit for anything other than flush and compaction, e.g. write to
     * WAL.
     * @param refillPeriodMicros this controls how often tokens are refilled. For
     * example,
     * when rate_bytes_per_sec is set to 10MB/s and refill_period_us is set to
     * 100ms, then 1MB is refilled every 100ms internally. Larger value can
     * lead to burstier writes while smaller value introduces more CPU
     * overhead. The default of 100,000ms should work for most cases.
     * @param fairness RateLimiter accepts high-pri requests and low-pri requests.
     * A low-pri request is usually blocked in favor of hi-pri request.
     * Currently, RocksDB assigns low-pri to request from compaction and
     * high-pri to request from flush. Low-pri requests can get blocked if
     * flush requests come in continuously. This fairness parameter grants
     * low-pri requests permission by fairness chance even though high-pri
     * requests exist to avoid starvation.
     * You should be good by leaving it at default 10.
     * @param rateLimiterMode indicates which types of operations count against
     * the limit.
     * @param autoTune Enables dynamic adjustment of rate limit within the range
     * `[rate_bytes_per_sec / 20, rate_bytes_per_sec]`, according to
     * the recent demand for background I/O.
     */
    constructor(
        rateBytesPerSecond: Long,
        refillPeriodMicros: Long, fairness: Int,
        rateLimiterMode: RateLimiterMode, autoTune: Boolean
    )

    /**
     * This API allows user to dynamically change rate limiter's bytes per second.
     * REQUIRED: bytes_per_second &gt; 0
     *
     * @param bytesPerSecond bytes per second.
     */
    fun setBytesPerSecond(bytesPerSecond: Long)

    /**
     * Returns the bytes per second.
     *
     * @return bytes per second.
     */
    fun getBytesPerSecond(): Long

    /**
     * Request for token to write bytes. If this request can not be satisfied,
     * the call is blocked. Caller is responsible to make sure
     * `bytes &lt; GetSingleBurstBytes()`.
     *
     * @param bytes requested bytes.
     */
    fun request(bytes: Long)

    /**
     * Max bytes can be granted in a single burst.
     * @return max bytes can be granted in a single burst.
     */
    fun getSingleBurstBytes(): Long

    /**
     * Total bytes that go through rate limiter.
     *
     * @return total bytes that go through rate limiter.
     */
    fun getTotalBytesThrough(): Long

    /**
     * Total # of requests that go through rate limiter.
     *
     * @return total # of requests that go through rate limiter.
     */
    fun getTotalRequests(): Long
}
