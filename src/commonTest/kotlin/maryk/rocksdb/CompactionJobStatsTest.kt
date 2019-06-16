package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompactionJobStatsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun reset() {
        CompactionJobStats().use { compactionJobStats ->
            compactionJobStats.reset()
            assertEquals(0, compactionJobStats.elapsedMicros())
        }
    }

    @Test
    fun add() {
        CompactionJobStats().use { compactionJobStats ->
            CompactionJobStats().use { otherCompactionJobStats ->
                compactionJobStats.add(
                    otherCompactionJobStats
                )
            }
        }
    }

    @Test
    fun elapsedMicros() {
        CompactionJobStats().use { compactionJobStats -> assertEquals(0, compactionJobStats.elapsedMicros()) }
    }

    @Test
    fun numInputRecords() {
        CompactionJobStats().use { compactionJobStats -> assertEquals(0, compactionJobStats.numInputRecords()) }
    }

    @Test
    fun numInputFiles() {
        CompactionJobStats().use { compactionJobStats -> assertEquals(0, compactionJobStats.numInputFiles()) }
    }

    @Test
    fun numInputFilesAtOutputLevel() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numInputFilesAtOutputLevel())
        }
    }

    @Test
    fun numOutputRecords() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numOutputRecords())
        }
    }

    @Test
    fun numOutputFiles() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numOutputFiles())
        }
    }

    @Test
    fun isManualCompaction() {
        CompactionJobStats().use { compactionJobStats ->
            assertFalse(compactionJobStats.isManualCompaction())
        }
    }

    @Test
    fun totalInputBytes() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.totalInputBytes())
        }
    }

    @Test
    fun totalOutputBytes() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.totalOutputBytes())
        }
    }


    @Test
    fun numRecordsReplaced() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numRecordsReplaced())
        }
    }

    @Test
    fun totalInputRawKeyBytes() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.totalInputRawKeyBytes())
        }
    }

    @Test
    fun totalInputRawValueBytes() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.totalInputRawValueBytes())
        }
    }

    @Test
    fun numInputDeletionRecords() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numInputDeletionRecords())
        }
    }

    @Test
    fun numExpiredDeletionRecords() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numExpiredDeletionRecords())
        }
    }

    @Test
    fun numCorruptKeys() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numCorruptKeys())
        }
    }

    @Test
    fun fileWriteNanos() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.fileWriteNanos())
        }
    }

    @Test
    fun fileRangeSyncNanos() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.fileRangeSyncNanos())
        }
    }

    @Test
    fun fileFsyncNanos() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.fileFsyncNanos())
        }
    }

    @Test
    fun filePrepareWriteNanos() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.filePrepareWriteNanos())
        }
    }

    @Test
    fun smallestOutputKeyPrefix() {
        CompactionJobStats().use { compactionJobStats ->
            assertTrue(compactionJobStats.smallestOutputKeyPrefix().isEmpty())
        }
    }

    @Test
    fun largestOutputKeyPrefix() {
        CompactionJobStats().use { compactionJobStats ->
            assertTrue(compactionJobStats.largestOutputKeyPrefix().isEmpty())
        }
    }

    @Test
    fun numSingleDelFallthru() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numSingleDelFallthru())
        }
    }

    @Test
    fun numSingleDelMismatch() {
        CompactionJobStats().use { compactionJobStats ->
            assertEquals(0, compactionJobStats.numSingleDelMismatch())
        }
    }
}
