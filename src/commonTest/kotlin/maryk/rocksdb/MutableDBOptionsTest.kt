package maryk.rocksdb

import maryk.rocksdb.DBOption.bytes_per_sync
import maryk.rocksdb.DBOption.max_background_jobs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MutableDBOptionsTest {
    @Test
    fun builder() {
        val builder = mutableDBOptionsBuilder()
        builder
            .setBytesPerSync((1024 * 1024 * 7).toLong())
            .setMaxBackgroundJobs(5)
            .setAvoidFlushDuringShutdown(false)

        assertEquals((1024 * 1024 * 7).toLong(), builder.bytesPerSync())
        assertEquals(5, builder.maxBackgroundJobs())
        assertEquals(false, builder.avoidFlushDuringShutdown())
    }

    @Test
        //(expected = NoSuchElementException::class)
    fun builder_getWhenNotSet() {
        val builder = mutableDBOptionsBuilder()

        assertFailsWith<NoSuchElementException> {
            builder.bytesPerSync()
        }
    }

    @Test
    fun builder_build() {
        val options = mutableDBOptionsBuilder()
            .setBytesPerSync((1024 * 1024 * 7).toLong())
            .setMaxBackgroundJobs(5)
            .build()

        assertEquals(2, options.getKeys().size)
        assertEquals(2, options.getValues().size)
        assertEquals(bytes_per_sync.name, options.getKeys()[0])
        assertEquals("7340032", options.getValues()[0])
        assertEquals(max_background_jobs.name, options.getKeys()[1])
        assertEquals("5", options.getValues()[1])
    }

    @Test
    fun mutableColumnFamilyOptions_toString() {
        val str = mutableDBOptionsBuilder()
            .setMaxOpenFiles(99)
            .setDelayedWriteRate(789)
            .setAvoidFlushDuringShutdown(true)
            .build()
            .toString()

        assertEquals("max_open_files=99;delayed_write_rate=789;avoid_flush_during_shutdown=true", str)
    }

    @Test
    fun mutableColumnFamilyOptions_parse() {
        val str = "max_open_files=99;delayed_write_rate=789;" + "avoid_flush_during_shutdown=true"

        val builder = parseMutableDBOptionsBuilder(str)

        assertEquals(99, builder.maxOpenFiles())
        assertEquals(789, builder.delayedWriteRate())
        assertEquals(true, builder.avoidFlushDuringShutdown())
    }
}
