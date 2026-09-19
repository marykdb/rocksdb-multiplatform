package maryk.rocksdb

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventListenerTest {
    private lateinit var dbDir: File

    @BeforeTest
    fun setUp() {
        loadRocksDBLibrary()
        dbDir = createTempDirectory(prefix = "rocksdb-events").toFile()
    }

    @AfterTest
    fun tearDown() {
        if (::dbDir.isInitialized) {
            dbDir.deleteRecursively()
        }
    }

    @Test
    fun flushAndCompactionCallbacksFire() {
        val flushEvents = mutableListOf<FlushJobInfo>()
        val compactionEvents = mutableListOf<CompactionJobInfo>()

        val listener = object : EventListener() {
            override fun onFlushCompletedEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {
                flushEvents += flushJobInfo
            }

            override fun onCompactionCompletedEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {
                compactionEvents += compactionJobInfo
            }
        }

        Options().setCreateIfMissing(true).use { options ->
            options.addEventListener(listener)

            RocksDB.open(options, dbDir.absolutePath).use { db ->
                val keyPrefix = "listener-key".encodeToByteArray()
                val value = ByteArray(1024) { 42 }
                repeat(4) { index ->
                    val key = keyPrefix + index.toByte()
                    db.put(key, value)
                }

                FlushOptions().setWaitForFlush(true).use { flushOptions ->
                    db.flush(flushOptions)
                }

                db.compactRange()
            }
        }

        // Listener is owned by options once registered, closing it becomes a no-op but keeps API symmetry.
        listener.close()

        assertTrue(flushEvents.isNotEmpty(), "Expected at least one flush event")
        assertTrue(flushEvents.all { it.filePath().isNotEmpty() })

        val flushInfo = flushEvents.first()
        assertEquals(0L, flushInfo.columnFamilyId(), "default CF id should be zero")
        assertEquals("default", flushInfo.columnFamilyName())
        assertTrue(flushInfo.threadId() > 0)
        assertTrue(flushInfo.jobId() >= 0)
        assertEquals(FlushReason.MANUAL_FLUSH, flushInfo.flushReason())

        val tableProps = flushInfo.tableProperties()
        assertNotNull(tableProps.compressionName())
        assertTrue(tableProps.numEntries() > 0)
        assertEquals("default", tableProps.columnFamilyName())

        assertTrue(compactionEvents.isNotEmpty(), "Expected at least one compaction event")
        assertTrue(compactionEvents.any { it.totalOutputBytes() >= 0 })

        val stats = compactionEvents.first().compactionStats()
        assertTrue(stats.numOutputFiles() >= 0, "Expected non-negative output file count in compaction stats")
    }

    @Test
    fun externalFileIngestionCallbackProvidesMetadata() {
        val ingestions = mutableListOf<ExternalFileIngestionInfo>()

        val listener = object : EventListener() {
            override fun onExternalFileIngested(db: RocksDB, ingestionInfo: ExternalFileIngestionInfo) {
                ingestions += ingestionInfo
            }
        }

        Options().setCreateIfMissing(true).use { options ->
            options.addEventListener(listener)

            RocksDB.open(options, dbDir.absolutePath).use { db ->
                val sstFile = File(dbDir, "ingest.sst")

                EnvOptions().use { envOptions ->
                    SstFileWriter(envOptions, options).use { writer ->
                        writer.open(sstFile.absolutePath)
                        writer.put("ingest-key".encodeToByteArray(), "ingest-value".encodeToByteArray())
                        writer.finish()
                    }
                }

                IngestExternalFileOptions().use { ingestOptions ->
                    ingestOptions.setMoveFiles(true)
                    db.ingestExternalFile(listOf(sstFile.absolutePath), ingestOptions)
                }
            }
        }

        listener.close()

        val info = ingestions.single()
        assertEquals("default", info.columnFamilyName())
        assertTrue(info.externalFilePath().isNotEmpty())
        assertTrue(info.internalFilePath().isNotEmpty())
        assertTrue(info.globalSequenceNumber() >= 0)
        assertNotNull(info.tableProperties())
    }
}
