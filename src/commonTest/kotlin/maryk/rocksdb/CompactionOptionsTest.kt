package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class CompactionOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun compression() {
        CompactionOptions().use { compactionOptions ->
            assertEquals(CompressionType.SNAPPY_COMPRESSION, compactionOptions.compression())
            compactionOptions.setCompression(CompressionType.NO_COMPRESSION)
            assertEquals(CompressionType.NO_COMPRESSION, compactionOptions.compression())
        }
    }

    @Test
    fun outputFileSizeLimit() {
        val mb250 = (1024 * 1024 * 250).toLong()
        CompactionOptions().use { compactionOptions ->
            assertEquals(-1, compactionOptions.outputFileSizeLimit())
            compactionOptions.setOutputFileSizeLimit(mb250)
            assertEquals(mb250, compactionOptions.outputFileSizeLimit())
        }
    }

    @Test
    fun maxSubcompactions() {
        CompactionOptions().use { compactionOptions ->
            assertEquals(0, compactionOptions.maxSubcompactions())
            compactionOptions.setMaxSubcompactions(9)
            assertEquals(9, compactionOptions.maxSubcompactions())
        }
    }
}
