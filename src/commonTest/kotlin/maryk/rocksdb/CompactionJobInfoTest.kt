package maryk.rocksdb

import maryk.rocksdb.CompactionReason.kUnknown
import maryk.rocksdb.CompressionType.NO_COMPRESSION
import maryk.rocksdb.StatusCode.Ok
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CompactionJobInfoTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun columnFamilyName() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertTrue(compactionJobInfo.columnFamilyName().isEmpty())
        }
    }

    @Test
    fun status() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(Ok, compactionJobInfo.status().getCode())
        }
    }

    @Test
    fun threadId() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(0, compactionJobInfo.threadId())
        }
    }

    @Test
    fun jobId() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(0, compactionJobInfo.jobId())
        }
    }

    @Test
    fun baseInputLevel() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(0, compactionJobInfo.baseInputLevel())
        }
    }

    @Test
    fun outputLevel() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(0, compactionJobInfo.outputLevel())
        }
    }

    @Test
    fun inputFiles() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertTrue(compactionJobInfo.inputFiles().isEmpty())
        }
    }

    @Test
    fun outputFiles() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertTrue(compactionJobInfo.outputFiles().isEmpty())
        }
    }

    @Test
    fun tableProperties() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertTrue(compactionJobInfo.tableProperties().isEmpty())
        }
    }

    @Test
    fun compactionReason() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(kUnknown, compactionJobInfo.compactionReason())
        }
    }

    @Test
    fun compression() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertEquals(NO_COMPRESSION, compactionJobInfo.compression())
        }
    }

    @Test
    fun stats() {
        CompactionJobInfo().use { compactionJobInfo ->
            assertNotNull(compactionJobInfo.stats())
        }
    }
}
