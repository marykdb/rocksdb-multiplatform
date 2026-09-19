package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class CompactionOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun compressionRoundTrip() {
        CompactionOptions().use { options ->
            val original = options.compression()
            options.setCompression(CompressionType.NO_COMPRESSION)
            assertEquals(CompressionType.NO_COMPRESSION, options.compression())

            options.setCompression(original)
            assertEquals(original, options.compression())
        }
    }

    @Test
    fun outputFileSizeLimit() {
        val limit = 250L * 1024 * 1024
        CompactionOptions().use { options ->
            assertEquals(-1, options.outputFileSizeLimit())

            options.setOutputFileSizeLimit(limit)
            assertEquals(limit, options.outputFileSizeLimit())
        }
    }

    @Test
    fun maxSubcompactions() {
        CompactionOptions().use { options ->
            assertEquals(0, options.maxSubcompactions())

            options.setMaxSubcompactions(9)
            assertEquals(9, options.maxSubcompactions())
        }
    }
}
