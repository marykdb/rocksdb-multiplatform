@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_ratelimiter_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.asUInt64
import maryk.toCheckedLong
import rocksdb.rocksdb_ratelimiter_bytes_per_second
import rocksdb.rocksdb_ratelimiter_create
import rocksdb.rocksdb_ratelimiter_destroy
import rocksdb.rocksdb_ratelimiter_set_bytes_per_second

actual class RateLimiter internal constructor(
    internal val native: CPointer<rocksdb_ratelimiter_t>
) : RocksObject() {
    actual constructor(rateBytesPerSecond: Long) : this(
        createRateLimiter(rateBytesPerSecond, 1000, 10)
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long) : this(
        createRateLimiter(rateBytesPerSecond, refillPeriodMicros, 10)
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int) : this(
        createRateLimiter(rateBytesPerSecond, refillPeriodMicros, fairness)
    )

    override fun close() {
        if (tryClose()) {
            rocksdb_ratelimiter_destroy(native)
            super.close()
        }
    }

    actual fun setBytesPerSecond(rateBytesPerSecond: Long) {
        checkOwningHandle()
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
): CPointer<rocksdb_ratelimiter_t> {
    require(rateBytesPerSecond >= 0) { "rateBytesPerSecond must be non-negative: $rateBytesPerSecond" }
    require(refillPeriodMicros >= 0) { "refillPeriodMicros must be non-negative: $refillPeriodMicros" }
    require(fairness >= 0) { "fairness must be non-negative: $fairness" }
    return requireNotNull(rocksdb_ratelimiter_create(rateBytesPerSecond, refillPeriodMicros, fairness)) {
        "Unable to allocate RocksDB rate limiter"
    }
}
