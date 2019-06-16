package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RateLimiterTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun bytesPerSecond() {
        RateLimiter(
            1000, RATE_LIMITER_DEFAULT_REFILL_PERIOD_MICROS,
            RATE_LIMITER_DEFAULT_FAIRNESS, RATE_LIMITER_DEFAULT_MODE, RATE_LIMITER_DEFAULT_AUTOTUNE
        ).use { rateLimiter ->
            assertTrue(0 < rateLimiter.getBytesPerSecond())
            rateLimiter.setBytesPerSecond(2000)
            assertTrue(0 < rateLimiter.getBytesPerSecond())
        }
    }

    @Test
    fun getSingleBurstBytes() {
        RateLimiter(
            1000, RATE_LIMITER_DEFAULT_REFILL_PERIOD_MICROS,
            RATE_LIMITER_DEFAULT_FAIRNESS, RATE_LIMITER_DEFAULT_MODE, RATE_LIMITER_DEFAULT_AUTOTUNE
        ).use { rateLimiter -> assertEquals(100, rateLimiter.getSingleBurstBytes()) }
    }

    @Test
    fun getTotalBytesThrough() {
        RateLimiter(
            1000, RATE_LIMITER_DEFAULT_REFILL_PERIOD_MICROS,
            RATE_LIMITER_DEFAULT_FAIRNESS, RATE_LIMITER_DEFAULT_MODE, RATE_LIMITER_DEFAULT_AUTOTUNE
        ).use { rateLimiter -> assertEquals(0, rateLimiter.getTotalBytesThrough()) }
    }

    @Test
    fun getTotalRequests() {
        RateLimiter(
            1000, RATE_LIMITER_DEFAULT_REFILL_PERIOD_MICROS,
            RATE_LIMITER_DEFAULT_FAIRNESS, RATE_LIMITER_DEFAULT_MODE, RATE_LIMITER_DEFAULT_AUTOTUNE
        ).use { rateLimiter -> assertEquals(0, rateLimiter.getTotalRequests()) }
    }

    @Test
    fun autoTune() {
        RateLimiter(
            1000, RATE_LIMITER_DEFAULT_REFILL_PERIOD_MICROS,
            RATE_LIMITER_DEFAULT_FAIRNESS, RATE_LIMITER_DEFAULT_MODE, true
        ).use { rateLimiter -> assertTrue(0 < rateLimiter.getBytesPerSecond()) }
    }
}
