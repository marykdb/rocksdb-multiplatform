package maryk.rocksdb

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MutableColumnFamilyOptionsTest {
    @BeforeTest
    fun setup() {
        loadRocksDBLibrary()
    }

    @AfterTest
    fun tearDown() {
        // ensure builders can be garbage collected between tests
        System.gc()
    }

    @Test
    fun builderRoundTrip() {
        val builder = mutableColumnFamilyOptionsBuilder()
            .setWriteBufferSize(10)
            .setDisableAutoCompactions(true)
            .setLevel0FileNumCompactionTrigger(4)
            .setMaxCompactionBytes(512)
            .setMaxBytesForLevelBase(2048)
            .setCompressionType(CompressionType.LZ4_COMPRESSION)

        assertEquals(10, builder.writeBufferSize())
        assertTrue(builder.disableAutoCompactions())
        assertEquals(4, builder.level0FileNumCompactionTrigger())
        assertEquals(512, builder.maxCompactionBytes())
        assertEquals(2048, builder.maxBytesForLevelBase())
        assertEquals(CompressionType.LZ4_COMPRESSION, builder.compressionType())
    }

    @Test
    fun builderBuildsOptions() {
        val options = mutableColumnFamilyOptionsBuilder()
            .setWriteBufferSize(32)
            .setDisableAutoCompactions(false)
            .setCompressionType(CompressionType.ZLIB_COMPRESSION)
            .build()

        val rendered = options.toString()
        assertTrue(rendered.contains("write_buffer_size=32"))
        assertTrue(rendered.contains("disable_auto_compactions=false"))
        assertTrue(rendered.contains("compression=ZLIB_COMPRESSION"))
    }

    @Test
    fun parseOptionsString() {
        val optionsString = buildString {
            append("write_buffer_size=64;")
            append("disable_auto_compactions=true;")
            append("level0_file_num_compaction_trigger=8;")
            append("max_compaction_bytes=1024;")
            append("max_bytes_for_level_base=4096;")
            append("compression=kSnappyCompression")
        }

        val parsed = parseMutableColumnFamilyOptions(optionsString)

        assertEquals(64, parsed.writeBufferSize())
        assertTrue(parsed.disableAutoCompactions())
        assertEquals(8, parsed.level0FileNumCompactionTrigger())
        assertEquals(1024, parsed.maxCompactionBytes())
        assertEquals(4096, parsed.maxBytesForLevelBase())
        assertEquals(CompressionType.SNAPPY_COMPRESSION, parsed.compressionType())
    }
}
