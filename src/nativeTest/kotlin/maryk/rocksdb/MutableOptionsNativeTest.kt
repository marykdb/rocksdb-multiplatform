package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MutableOptionsNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun mutableDbOptionsParseBuildAndClosedGuards() {
        val builder = parseMutableDBOptions(
            "max_open_files=55;delayed_write_rate=3000;" +
                "avoid_flush_during_shutdown=true;daily_offpeak_time_utc=01\\:00-05\\:30"
        )

        builder.use {
            assertEquals(55, it.maxOpenFiles())
            assertEquals(3000, it.delayedWriteRate())
            assertTrue(it.avoidFlushDuringShutdown())
            assertEquals("01:00-05:30", it.dailyOffpeakTimeUTC())

            it.build().close()

            assertFailsWith<IllegalArgumentException> {
                it.setBytesPerSync(-1)
            }
        }

        assertFailsWith<IllegalStateException> {
            builder.maxOpenFiles()
        }
    }

    @Test
    fun mutableColumnFamilyOptionsParseBuildAndClosedGuards() {
        val builder = parseMutableColumnFamilyOptions(
            "write_buffer_size=64;disable_auto_compactions=true;" +
                "level0_file_num_compaction_trigger=8;max_compaction_bytes=1024;" +
                "max_bytes_for_level_base=4096;compression=kSnappyCompression"
        )

        builder.use {
            assertEquals(64, it.writeBufferSize())
            assertTrue(it.disableAutoCompactions())
            assertEquals(8, it.level0FileNumCompactionTrigger())
            assertEquals(1024, it.maxCompactionBytes())
            assertEquals(4096, it.maxBytesForLevelBase())
            assertEquals(CompressionType.SNAPPY_COMPRESSION, it.compressionType())

            it.build().close()

            assertFailsWith<IllegalArgumentException> {
                it.setMaxCompactionBytes(-1)
            }
        }

        assertFailsWith<IllegalStateException> {
            builder.writeBufferSize()
        }
    }
}
