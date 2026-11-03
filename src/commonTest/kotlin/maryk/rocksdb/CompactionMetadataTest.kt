package maryk.rocksdb

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import maryk.rocksdb.util.createTestDBFolder

@OptIn(ExperimentalAtomicApi::class)
class CompactionMetadataTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun completedCompactionMetadataSurvivesCallbackAndDatabaseClose() {
        val completed = AtomicReference<CompactionJobInfo?>(null)
        val listener = object : EventListener() {
            override fun onCompactionCompletedEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {
                completed.store(compactionJobInfo)
            }
        }
        listener.use {
            Options().setCreateIfMissing(true).setDisableAutoCompactions(true)
                .setCompressionType(CompressionType.NO_COMPRESSION).use { options ->
                    options.addEventListener(listener)
                    openRocksDB(options, createTestDBFolder("CompactionMetadataTest_snapshot")).use { db ->
                        FlushOptions().setWaitForFlush(true).use { flush ->
                            repeat(3) { version ->
                                repeat(32) { index ->
                                    val key = "key-${index.toString().padStart(3, '0')}".encodeToByteArray()
                                    db.put(key, ByteArray(1024) { version.toByte() })
                                }
                                db.flush(flush)
                            }
                        }
                        db.compactRange()
                    }
                }
        }
        val info = assertNotNull(completed.load())
        assertContentEquals("default".encodeToByteArray(), info.columnFamilyName())
        assertTrue(info.threadId() > 0)
        assertTrue(info.jobId() >= 0)
        assertEquals(CompressionType.NO_COMPRESSION, info.compression())
        assertTrue(info.inputFiles().isNotEmpty())
        assertTrue(info.outputFiles().isNotEmpty())
        val stats = info.compactionStats()
        assertEquals(CompactionReason.kManualCompaction, info.compactionReason())
        assertTrue(stats.numInputRecords() >= 32)
        assertTrue(stats.numOutputRecords() >= 32)
        assertTrue(stats.numRecordsReplaced() > 0)
        assertTrue(stats.totalInputRawKeyBytes() > 0)
        assertTrue(stats.totalInputRawValueBytes() > 0)
        assertTrue(stats.numInputDeletionRecords() >= 0)
        assertTrue(stats.numExpiredDeletionRecords() >= 0)
        assertTrue(stats.fileWriteNanos() >= 0)
        assertTrue(stats.fileRangeSyncNanos() >= 0)
        assertTrue(stats.fileFsyncNanos() >= 0)
        assertTrue(stats.filePrepareWriteNanos() >= 0)
        assertEquals(0L, stats.numSingleDelFallthru())
        assertEquals(0L, stats.numSingleDelMismatch())
        val name = info.columnFamilyName()
        name[0] = 0
        assertContentEquals("default".encodeToByteArray(), info.columnFamilyName(), "Metadata snapshots must not expose mutable internal arrays")
    }
}
