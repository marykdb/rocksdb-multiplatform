@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_ratelimiter_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.asUInt64
import maryk.toCheckedLong
import rocksdb.rocksdb_ratelimiter_bytes_per_second
import rocksdb.rocksdb_ratelimiter_destroy
import rocksdb.rocksdb_ratelimiter_set_bytes_per_second

actual class RateLimiter internal constructor(
    internal val native: CPointer<rocksdb_ratelimiter_t>
) : RocksObject() {
    actual constructor(rateBytesPerSecond: Long) : this(
        createRateLimiter(rateBytesPerSecond, 100_000, 10)
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long) : this(
        createRateLimiter(rateBytesPerSecond, refillPeriodMicros, 10)
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int) : this(
        createRateLimiter(rateBytesPerSecond, refillPeriodMicros, fairness)
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int, rateLimiterMode: RateLimiterMode) : this(
        createRateLimiter(rateBytesPerSecond, refillPeriodMicros, fairness, rateLimiterMode, false)
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int, rateLimiterMode: RateLimiterMode, autoTune: Boolean) : this(
        createRateLimiter(rateBytesPerSecond, refillPeriodMicros, fairness, rateLimiterMode, autoTune)
    )

    actual fun getSingleBurstBytes(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_ratelimiter_get_single_burst_bytes(native)
    }

    actual fun getTotalBytesThrough(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_ratelimiter_get_total_bytes_through(native)
    }

    actual fun getTotalRequests(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_ratelimiter_get_total_requests(native)
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_ratelimiter_destroy(native)
            super.close()
        }
    }

    actual fun setBytesPerSecond(rateBytesPerSecond: Long) {
        checkOwningHandle()
        require(rateBytesPerSecond > 0) { "rateBytesPerSecond must be positive" }
        rocksdb_ratelimiter_set_bytes_per_second(native, rateBytesPerSecond.asUInt64())
    }

    actual fun getBytesPerSecond(): Long {
        checkOwningHandle()
        return rocksdb_ratelimiter_bytes_per_second(native).toCheckedLong("rate limiter bytes per second")
    }
}

private fun createRateLimiter(
    rateBytesPerSecond: Long,
    refillPeriodMicros: Long,
    fairness: Int,
    mode: RateLimiterMode = RateLimiterMode.WRITES_ONLY,
    autoTune: Boolean = false,
): CPointer<rocksdb_ratelimiter_t> {
    require(rateBytesPerSecond > 0) { "rateBytesPerSecond must be positive: $rateBytesPerSecond" }
    require(refillPeriodMicros > 0) { "refillPeriodMicros must be positive: $refillPeriodMicros" }
    require(fairness > 0) { "fairness must be positive: $fairness" }
    return requireNotNull(rocksdb.rocksdb_ratelimiter_create_with_mode(rateBytesPerSecond, refillPeriodMicros, fairness, mode.value, autoTune)) {
        "Unable to allocate RocksDB rate limiter"
    }
}
