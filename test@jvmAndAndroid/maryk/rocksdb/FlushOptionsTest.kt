package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FlushOptionsTest {
    init {
        RocksDB.loadLibrary()
    }

    @Test
    fun flushOptionsRoundTrip() {
        FlushOptions().use { options ->
            options.setWaitForFlush(true)
            options.setAllowWriteStall(false)

            assertTrue(options.waitForFlush())
            assertFalse(options.allowWriteStall())
        }
    }

    @Test
    fun bloomFilterConstructorsWork() {
        BloomFilter().use { assertNotNull(it) }
        BloomFilter(8.5).use { assertNotNull(it) }
        BloomFilter(10.0, true).use { assertNotNull(it) }
    }

    @Test
    fun compactionOptionsExposeToggles() {
        CompactionOptions().use { options ->
            options.setCompression(CompressionType.ZSTD_COMPRESSION)
            options.setOutputFileSizeLimit(16L * 1024 * 1024)
            options.setMaxSubcompactions(3)

            assertEquals(CompressionType.ZSTD_COMPRESSION, options.compression())
            assertEquals(16L * 1024 * 1024, options.outputFileSizeLimit())
            assertEquals(3, options.maxSubcompactions())
        }

        CompactionOptionsFIFO().use { fifo ->
            fifo.setMaxTableFilesSize(1_024)
            fifo.setAllowCompaction(true)
            assertEquals(1_024, fifo.maxTableFilesSize())
            assertTrue(fifo.allowCompaction())
        }

        CompactionOptionsUniversal().use { universal ->
            universal.setSizeRatio(20)
            universal.setMinMergeWidth(2)
            universal.setMaxMergeWidth(5)
            assertEquals(20, universal.sizeRatio())
            assertEquals(2, universal.minMergeWidth())
            assertEquals(5, universal.maxMergeWidth())
        }
    }

    @Test
    fun compressionOptionsRoundTrip() {
        CompressionOptions().use { options ->
            options.setWindowBits(32)
            options.setLevel(4)
            options.setStrategy(CompressionType.LZ4_COMPRESSION.value.toInt())
            options.setMaxDictBytes(2048)

            assertEquals(32, options.windowBits())
            assertEquals(4, options.level())
            assertEquals(CompressionType.LZ4_COMPRESSION.value.toInt(), options.strategy())
            assertEquals(2048, options.maxDictBytes())
        }
    }

    @Test
    fun rateLimiterAdjustsTarget() {
        RateLimiter(10_000).use { limiter ->
            assertEquals(10_000, limiter.getBytesPerSecond())
            limiter.setBytesPerSecond(50_000)
            assertEquals(50_000, limiter.getBytesPerSecond())
        }
    }
}
