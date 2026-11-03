package maryk.rocksdb

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import maryk.rocksdb.util.createTestDBFolder

@OptIn(ExperimentalAtomicApi::class)
class WriteFlushIngestionControlsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun optionDefaultsAndRoundTrips() {
        WriteOptions().use { options ->
            assertFalse(options.memtableInsertHintPerBatch())
            assertSame(options, options.setMemtableInsertHintPerBatch(true))
            assertTrue(options.memtableInsertHintPerBatch())
            options.setMemtableInsertHintPerBatch(false)
            assertFalse(options.memtableInsertHintPerBatch())
        }
        FlushOptions().use { options ->
            assertTrue(options.waitForFlush())
            assertFalse(options.allowWriteStall())
            assertFalse(options.listenerWait())
            assertSame(options, options.setListenerWait(true))
            assertTrue(options.listenerWait())
            options.setListenerWait(false)
            assertFalse(options.listenerWait())
        }
        IngestExternalFileOptions().use { options ->
            // RocksDB 11.8.1 defaults this deprecated compatibility option to false.
            assertFalse(options.writeGlobalSeqno())
            assertSame(options, options.setWriteGlobalSeqno(false))
            assertFalse(options.writeGlobalSeqno())
            options.setWriteGlobalSeqno(true)
            assertTrue(options.writeGlobalSeqno())
        }
    }

    @Test
    fun hintedBatchAndListenerWaitPreserveDataAndWaitForCallbacks() {
        val completed = AtomicInt(0)
        val listener = object : EventListener() {
            override fun onFlushCompletedEvent(db: RocksDB, flushJobInfo: FlushJobInfo) {
                completed.fetchAndAdd(1)
            }
        }
        listener.use {
            Options().setCreateIfMissing(true).use { options ->
                options.addEventListener(listener)
                openRocksDB(options, createTestDBFolder("write-flush-controls")).use { db ->
                    WriteOptions().setMemtableInsertHintPerBatch(true).use { writeOptions ->
                        WriteBatch().use { batch ->
                            repeat(16) { index ->
                                batch.put(byteArrayOf(index.toByte()), byteArrayOf((index + 1).toByte()))
                            }
                            db.write(writeOptions, batch)
                        }
                    }
                    FlushOptions().setWaitForFlush(true).setListenerWait(true).use { db.flush(it) }
                    assertTrue(completed.load() > 0, "flush must wait until completion listeners finish")
                    repeat(16) { index ->
                        assertContentEquals(byteArrayOf((index + 1).toByte()), db[byteArrayOf(index.toByte())])
                    }
                }
            }
        }
    }

    @Test
    fun ingestWithoutRewritingGlobalSequenceNumberPreservesBinaryData() {
        val path = createTestDBFolder("ingest-without-global-seqno-rewrite")
        val key = byteArrayOf(0, 1, 0, 2)
        val value = byteArrayOf(0, 3, 0, 4)
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).use { db ->
                val file = "$path/external.sst"
                EnvOptions().use { env ->
                    SstFileWriter(env, options).use { writer ->
                        writer.open(file)
                        writer.put(key, value)
                        writer.finish()
                    }
                }
                IngestExternalFileOptions().setWriteGlobalSeqno(false).use { ingest ->
                    db.ingestExternalFile(listOf(file), ingest)
                }
                assertContentEquals(value, db[key])
            }
        }
    }
    @Test
    fun environmentIoOptionsRoundTrip() {
        EnvOptions().use { options ->
            assertSame(options, options.setUseMmapReads(true))
            assertTrue(options.useMmapReads())
            options.setUseMmapReads(false)
            assertFalse(options.useMmapReads())
            options.setUseMmapWrites(true)
            assertTrue(options.useMmapWrites())
            options.setUseDirectReads(true)
            assertTrue(options.useDirectReads())
            options.setUseDirectWrites(true)
            assertTrue(options.useDirectWrites())
            options.setAllowFallocate(false)
            assertFalse(options.allowFallocate())
            options.setSetFdCloexec(false)
            assertFalse(options.setFdCloexec())
            options.setFallocateWithKeepSize(false)
            assertFalse(options.fallocateWithKeepSize())
            options.setBytesPerSync(65536)
            assertEquals(65536L, options.bytesPerSync())
            options.setCompactionReadaheadSize(32768)
            assertEquals(32768L, options.compactionReadaheadSize())
            options.setWritableFileMaxBufferSize(16384)
            assertEquals(16384L, options.writableFileMaxBufferSize())
        }
    }
    @Test
    fun blockTableControlsRoundTripAndReopenData() {
        val config = BlockBasedTableConfig()
        assertEquals(4096L, config.blockSize())
        assertEquals(16, config.blockRestartInterval())
        assertEquals(ChecksumType.kXXH3, config.checksumType())
        config.setIndexBlockRestartInterval(2)
            .setOptimizeFiltersForMemory(false)
            .setUseDeltaEncoding(false)
            .setVerifyCompression(true)
            .setReadAmpBytesPerBit(8)
            .setSeparateKeyValueInDataBlock(true)
            .setUniformCvThreshold(0.25)
            .setEnableIndexCompression(false)
            .setBlockAlign(false)
            .setSuperBlockAlignmentSize(0)
            .setSuperBlockAlignmentSpaceOverheadRatio(256)
            .setIndexShortening(IndexShorteningMode.kNoShortening)
        assertEquals(2, config.indexBlockRestartInterval())
        assertFalse(config.optimizeFiltersForMemory())
        assertFalse(config.useDeltaEncoding())
        assertTrue(config.verifyCompression())
        assertEquals(8, config.readAmpBytesPerBit())
        assertTrue(config.separateKeyValueInDataBlock())
        assertEquals(0.25, config.uniformCvThreshold())
        assertFalse(config.enableIndexCompression())
        assertFalse(config.blockAlign())
        assertEquals(0L, config.superBlockAlignmentSize())
        assertEquals(256L, config.superBlockAlignmentSpaceOverheadRatio())
        assertEquals(IndexShorteningMode.kNoShortening, config.indexShortening())
        val path = createTestDBFolder("block-table-controls")
        Options().setCreateIfMissing(true).setTableFormatConfig(config).use { options ->
            openRocksDB(options, path).use { db ->
                repeat(64) { db.put(byteArrayOf(it.toByte()), byteArrayOf((it + 1).toByte())) }
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
            }
            openRocksDB(options, path).use { db ->
                repeat(64) { assertContentEquals(byteArrayOf((it + 1).toByte()), db[byteArrayOf(it.toByte())]) }
            }
        }
    }
    @Test
    fun compactionPolicyControlsCanBeAppliedAndClosedBeforeDatabaseUse() {
        val path = createTestDBFolder("fifo-policy-controls")
        Options().setCreateIfMissing(true).setCompactionStyle(CompactionStyle.FIFO).setNumLevels(1).use { options ->
            CompactionOptionsFIFO().setMaxDataFilesSize(64L * 1024 * 1024)
                .setUseKvRatioCompaction(true).use { fifo ->
                    assertEquals(64L * 1024 * 1024, fifo.maxDataFilesSize())
                    assertTrue(fifo.useKvRatioCompaction())
                    assertSame(options, options.setCompactionOptionsFIFO(fifo))
                    assertSame(fifo, options.compactionOptionsFIFO())
                    ColumnFamilyOptions().use { columnOptions ->
                        assertSame(columnOptions, columnOptions.setCompactionOptionsFIFO(fifo))
                        assertSame(fifo, columnOptions.compactionOptionsFIFO())
                    }
                }
            openRocksDB(options, path).use { db ->
                db.put(byteArrayOf(0, 1), byteArrayOf(0, 2))
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                assertContentEquals(byteArrayOf(0, 2), db[byteArrayOf(0, 1)])
            }
        }
        Options().setCreateIfMissing(true).setCompactionStyle(CompactionStyle.UNIVERSAL).use { options ->
            CompactionOptionsUniversal().setAllowTrivialMove(true).use { universal ->
                assertTrue(universal.allowTrivialMove())
                assertSame(options, options.setCompactionOptionsUniversal(universal))
                assertSame(universal, options.compactionOptionsUniversal())
                ColumnFamilyOptions().use { columnOptions ->
                    assertSame(columnOptions, columnOptions.setCompactionOptionsUniversal(universal))
                    assertSame(universal, columnOptions.compactionOptionsUniversal())
                }
            }
            openRocksDB(options, createTestDBFolder("universal-policy-controls")).use { db ->
                db.put(byteArrayOf(1), byteArrayOf(2))
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                assertContentEquals(byteArrayOf(2), db[byteArrayOf(1)])
            }
        }
    }
}
