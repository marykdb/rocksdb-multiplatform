package maryk.rocksdb

import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import maryk.rocksdb.util.sleepMillis

class EventListenerNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun backgroundErrorCallbackDecodesBorrowedStatusMessage() = memScoped {
        val status = decodeBackgroundErrorStatus("[5|4] background write failed".cstr.ptr)

        assertNotNull(status)
        assertEquals(StatusCode.IOError, status.getCode())
        assertEquals(StatusSubCode.NoSpace, status.getSubCode())
        assertEquals("background write failed", status.getState())
    }

    @Test
    fun externalFileIngestionCallbackExceptionDoesNotEscapeOrLeakState() {
        val dbPath = createTestDBFolder("EventListenerNativeTest_ingest_callback")
        val callbackCount = ThreadSafeCounter()

        val listener = object : EventListener() {
            override fun onExternalFileIngested(db: RocksDB, ingestionInfo: ExternalFileIngestionInfo) {
                assertTrue(ingestionInfo.externalFilePath().isNotEmpty())
                assertTrue(ingestionInfo.internalFilePath().isNotEmpty())
                assertNotNull(ingestionInfo.tableProperties())
                callbackCount.increment()
                throw IllegalStateException("callback failures must not escape RocksDB")
            }
        }

        Options().setCreateIfMissing(true).use { options ->
            options.addEventListener(listener)
            openRocksDB(options, dbPath).use { db ->
                repeat(5) { index ->
                    val sstPath = "$dbPath/external-$index.sst"
                    EnvOptions().use { envOptions ->
                        SstFileWriter(envOptions, options).use { writer ->
                            writer.open(sstPath)
                            writer.put("ingest-key-$index".encodeToByteArray(), "value-$index".encodeToByteArray())
                            writer.finish()
                        }
                    }

                    IngestExternalFileOptions().use { ingestOptions ->
                        ingestOptions.setMoveFiles(true)
                        db.ingestExternalFile(listOf(sstPath), ingestOptions)
                    }

                    val value = db["ingest-key-$index".encodeToByteArray()]
                    assertNotNull(value)
                    assertContentEquals("value-$index".encodeToByteArray(), value)
                }

                val timeout = 5.seconds
                val timer = TimeSource.Monotonic.markNow()
                while (callbackCount.value() < 5 && timer.elapsedNow() < timeout) {
                    sleepMillis(50)
                }
            }
        }

        assertTrue(callbackCount.value() >= 5, "external-file ingestion callbacks should be invoked")
    }

    @Test
    fun flushCallbacksCanDecodeMetadataAndContainExceptions() {
        val dbPath = createTestDBFolder("EventListenerNativeTest_flush_callback")
        val beginCount = ThreadSafeCounter()
        val completedCount = ThreadSafeCounter()

        val listener = object : EventListener() {
            override fun onFlushBeginEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {
                assertEquals("default", flushJobInfo.columnFamilyName())
                assertTrue(flushJobInfo.threadId() > 0)
                flushJobInfo.flushReason()
                beginCount.increment()
                throw IllegalStateException("flush-begin callback failures must not escape RocksDB")
            }

            override fun onFlushCompletedEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {
                assertEquals("default", flushJobInfo.columnFamilyName())
                assertTrue(flushJobInfo.filePath().isNotEmpty())
                assertTrue(flushJobInfo.largestSeqno() >= flushJobInfo.smallestSeqno())
                assertTrue(flushJobInfo.tableProperties().numEntries() > 0)
                completedCount.increment()
                throw IllegalStateException("flush-completed callback failures must not escape RocksDB")
            }
        }

        Options().setCreateIfMissing(true).use { options ->
            options.addEventListener(listener)
            openRocksDB(options, dbPath).use { db ->
                repeat(16) { index ->
                    db.put("flush-key-$index".encodeToByteArray(), "value-$index".encodeToByteArray())
                }

                FlushOptions().setWaitForFlush(true).use { flushOptions ->
                    db.flush(flushOptions)
                }
            }
        }

        assertTrue(beginCount.value() > 0, "flush-begin callback should be invoked")
        assertTrue(completedCount.value() > 0, "flush-completed callback should be invoked")
    }

    @Test
    fun compactionCallbacksCanDecodeMetadataAndContainExceptions() {
        val dbPath = createTestDBFolder("EventListenerNativeTest_compaction_callback")
        val beginCount = ThreadSafeCounter()
        val completedCount = ThreadSafeCounter()

        val listener = object : EventListener() {
            override fun onCompactionBeginEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {
                assertTrue(compactionJobInfo.columnFamilyName().isNotEmpty())
                compactionJobInfo.compactionReason()
                compactionJobInfo.inputFiles()
                beginCount.increment()
                throw IllegalStateException("compaction-begin callback failures must not escape RocksDB")
            }

            override fun onCompactionCompletedEvent(db: RocksDB, compactionJobInfo: CompactionJobInfo) {
                assertTrue(compactionJobInfo.columnFamilyName().isNotEmpty())
                compactionJobInfo.compactionStats()
                compactionJobInfo.outputFiles()
                completedCount.increment()
                throw IllegalStateException("compaction-completed callback failures must not escape RocksDB")
            }
        }

        Options()
            .setCreateIfMissing(true)
            .setDisableAutoCompactions(true)
            .setCompactionStyle(CompactionStyle.LEVEL)
            .setNumLevels(4)
            .setWriteBufferSize((100 shl 10).toLong())
            .setLevel0FileNumCompactionTrigger(3)
            .setTargetFileSizeBase((200 shl 10).toLong())
            .setTargetFileSizeMultiplier(1)
            .setMaxBytesForLevelBase((500 shl 10).toLong())
            .setMaxBytesForLevelMultiplier(1.0)
            .use { options ->
                options.addEventListener(listener)
                openRocksDB(options, dbPath).use { db ->
                    val value = ByteArray(10_000) { index -> index.toByte() }
                    repeat(200) { index ->
                        db.put("compaction-key-$index".encodeToByteArray(), value)
                    }
                    db.compactRange()
                }
            }

        assertTrue(beginCount.value() > 0, "compaction-begin callback should be invoked")
        assertTrue(completedCount.value() > 0, "compaction-completed callback should be invoked")
    }

    @Test
    fun memTableSealedCallbackCanDecodeMetadataAndContainExceptions() {
        val dbPath = createTestDBFolder("EventListenerNativeTest_memtable_callback")
        val callbackCount = ThreadSafeCounter()

        val listener = object : EventListener() {
            override fun onMemTableSealed(info: MemTableInfo) {
                assertTrue(info.columnFamilyName().isNotEmpty())
                assertTrue(info.numEntries() > 0)
                info.firstSeqno()
                info.earliestSeqno()
                info.numDeletes()
                callbackCount.increment()
                throw IllegalStateException("memtable callback failures must not escape RocksDB")
            }
        }

        Options()
            .setCreateIfMissing(true)
            .setWriteBufferSize((32 shl 10).toLong())
            .setMaxWriteBufferNumber(4)
            .use { options ->
                options.addEventListener(listener)
                openRocksDB(options, dbPath).use { db ->
                    val value = ByteArray(10_000) { index -> index.toByte() }
                    repeat(80) { index ->
                        db.put("memtable-key-$index".encodeToByteArray(), value)
                    }
                    db.compactRange()
                }
            }

        assertTrue(callbackCount.value() > 0, "memtable-sealed callback should be invoked")
    }
}
