@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_ratelimiter_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import rocksdb.rocksdb_ratelimiter_bytes_per_second
import rocksdb.rocksdb_ratelimiter_create
import rocksdb.rocksdb_ratelimiter_destroy
import rocksdb.rocksdb_ratelimiter_set_bytes_per_second

actual class RateLimiter internal constructor(
    internal val native: CPointer<rocksdb_ratelimiter_t>
) : RocksObject() {
    actual constructor(rateBytesPerSecond: Long) : this(
        rocksdb_ratelimiter_create(rateBytesPerSecond, 1000, 10)!!
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long) : this(
        rocksdb_ratelimiter_create(rateBytesPerSecond, refillPeriodMicros, 10)!!
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int) : this(
        rocksdb_ratelimiter_create(rateBytesPerSecond, refillPeriodMicros, fairness)!!
    )

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_ratelimiter_destroy(native)
            super.close()
        }
    }

    actual fun setBytesPerSecond(rateBytesPerSecond: Long) {
        rocksdb_ratelimiter_set_bytes_per_second(native, rateBytesPerSecond.toULong())
    }

    actual fun getBytesPerSecond(): Long = rocksdb_ratelimiter_bytes_per_second(native).toLong()
}
