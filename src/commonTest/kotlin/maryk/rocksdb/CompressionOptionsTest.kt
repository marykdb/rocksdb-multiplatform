package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompressionOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun windowBits() {
        val windowBits = 7
        CompressionOptions().use { opt ->
            opt.setWindowBits(windowBits)
            assertEquals(windowBits, opt.windowBits())
        }
    }

    @Test
    fun level() {
        val level = 6
        CompressionOptions().use { opt ->
            opt.setLevel(level)
            assertEquals(level, opt.level())
        }
    }

    @Test
    fun strategy() {
        val strategy = 2
        CompressionOptions().use { opt ->
            opt.setStrategy(strategy)
            assertEquals(strategy, opt.strategy())
        }
    }

    @Test
    fun maxDictBytes() {
        val maxDictBytes = 999
        CompressionOptions().use { opt ->
            opt.setMaxDictBytes(maxDictBytes)
            assertEquals(maxDictBytes, opt.maxDictBytes())
        }
    }

    @Test
    fun zstdMaxTrainBytes() {
        val zstdMaxTrainBytes = 999
        CompressionOptions().use { opt ->
            opt.setZStdMaxTrainBytes(zstdMaxTrainBytes)
            assertEquals(zstdMaxTrainBytes, opt.zstdMaxTrainBytes())
        }
    }

    @Test
    fun enabled() {
        CompressionOptions().use { opt ->
            assertFalse(opt.enabled())
            opt.setEnabled(true)
            assertTrue(opt.enabled())
        }
    }
}
