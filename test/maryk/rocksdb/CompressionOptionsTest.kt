package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class CompressionOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun windowBits() {
        val windowBits = 7
        CompressionOptions().use { options ->
            options.setWindowBits(windowBits)
            assertEquals(windowBits, options.windowBits())
        }
    }

    @Test
    fun level() {
        val level = 6
        CompressionOptions().use { options ->
            options.setLevel(level)
            assertEquals(level, options.level())
        }
    }

    @Test
    fun strategy() {
        val strategy = 2
        CompressionOptions().use { options ->
            options.setStrategy(strategy)
            assertEquals(strategy, options.strategy())
        }
    }

    @Test
    fun maxDictBytes() {
        val maxDictBytes = 999
        CompressionOptions().use { options ->
            options.setMaxDictBytes(maxDictBytes)
            assertEquals(maxDictBytes, options.maxDictBytes())
        }
    }
}
