package maryk.rocksdb

import kotlinx.cinterop.CPointer
import maryk.rocksdb.RateLimiterMode.WRITES_ONLY

private const val DEFAULT_REFILL_PERIOD_MICROS = (100 * 1000).toLong()
private const val DEFAULT_FAIRNESS = 10
private val DEFAULT_MODE = WRITES_ONLY
private const val DEFAULT_AUTOTUNE = false

actual class RateLimiter
    actual constructor(
        rateBytesPerSecond: Long,
        refillPeriodMicros: Long,
        fairness: Int,
        rateLimiterMode: RateLimiterMode,
        autoTune: Boolean
    )
: RocksObject(
    newRateLimiterHandle(
        rateBytesPerSecond, refillPeriodMicros, fairness, rateLimiterMode.value, autoTune
    )
) {
    actual constructor(rateBytesPerSecond: Long) : this(
        rateBytesPerSecond,
        DEFAULT_REFILL_PERIOD_MICROS,
        DEFAULT_FAIRNESS,
        DEFAULT_MODE,
        DEFAULT_AUTOTUNE
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long) : this(
        rateBytesPerSecond,
        refillPeriodMicros,
        DEFAULT_FAIRNESS,
        DEFAULT_MODE,
        DEFAULT_AUTOTUNE
    )

    actual constructor(rateBytesPerSecond: Long, refillPeriodMicros: Long, fairness: Int) : this(
        rateBytesPerSecond,
        refillPeriodMicros,
        fairness,
        DEFAULT_MODE,
        DEFAULT_AUTOTUNE
    )

    actual constructor(
        rateBytesPerSecond: Long,
        refillPeriodMicros: Long,
        fairness: Int,
        rateLimiterMode: RateLimiterMode
    ) : this(
        rateBytesPerSecond,
        refillPeriodMicros,
        fairness,
        rateLimiterMode,
        DEFAULT_AUTOTUNE
    )

    actual fun setBytesPerSecond(bytesPerSecond: Long) {
    }

    actual fun getBytesPerSecond(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun request(bytes: Long) {
    }

    actual fun getSingleBurstBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTotalBytesThrough(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTotalRequests(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun newRateLimiterHandle(
    rateBytesPerSecond: Long,
    refillPeriodMicros: Long,
    fairness: Int,
    value: Any,
    autoTune: Boolean
): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
