package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ReadOptionsTuningTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun tuningOptionsRoundTripAndCanBeCleared() {
        ReadOptions().use { options ->
            assertEquals(0L, options.readaheadSize())
            assertFalse(options.autoPrefixMode())
            assertFalse(options.totalOrderSeek())
            assertFalse(options.asyncIo())
            assertEquals(0L, options.deadline())
            assertEquals(0L, options.ioTimeout())

            assertSame(options, options.setReadaheadSize(65_536L))
            assertSame(options, options.setAutoPrefixMode(true))
            assertSame(options, options.setTotalOrderSeek(true))
            assertSame(options, options.setAsyncIo(true))
            assertSame(options, options.setDeadline(1_900_000_000_123_456L))
            assertSame(options, options.setIoTimeout(5_000_000_000L))

            assertEquals(65_536L, options.readaheadSize())
            assertTrue(options.autoPrefixMode())
            assertTrue(options.totalOrderSeek())
            assertTrue(options.asyncIo())
            assertEquals(1_900_000_000_123_456L, options.deadline())
            assertEquals(5_000_000_000L, options.ioTimeout())

            options.setReadaheadSize(0).setAutoPrefixMode(false).setTotalOrderSeek(false)
                .setAsyncIo(false).setDeadline(0).setIoTimeout(0)
            assertEquals(0L, options.readaheadSize())
            assertFalse(options.autoPrefixMode())
            assertFalse(options.totalOrderSeek())
            assertFalse(options.asyncIo())
            assertEquals(0L, options.deadline())
            assertEquals(0L, options.ioTimeout())
        }
    }

    @Test
    fun additionalIteratorAndMultigetControlsRoundTrip() {
        ReadOptions().use { options ->
            assertFalse(options.backgroundPurgeOnIteratorCleanup())
            assertFalse(options.ignoreRangeDeletions())
            assertFalse(options.pinData())
            assertEquals(0L, options.maxSkippableInternalKeys())
            assertEquals(-1L, options.valueSizeSoftLimit())
            assertSame(options, options.setBackgroundPurgeOnIteratorCleanup(true))
            assertSame(options, options.setIgnoreRangeDeletions(true))
            assertSame(options, options.setPinData(true))
            assertSame(options, options.setMaxSkippableInternalKeys(5_000_000_000L))
            assertSame(options, options.setValueSizeSoftLimit(5_000_000_000L))
            assertTrue(options.backgroundPurgeOnIteratorCleanup())
            assertTrue(options.ignoreRangeDeletions())
            assertTrue(options.pinData())
            assertEquals(5_000_000_000L, options.maxSkippableInternalKeys())
            assertEquals(5_000_000_000L, options.valueSizeSoftLimit())
            options.setBackgroundPurgeOnIteratorCleanup(false).setIgnoreRangeDeletions(false)
                .setPinData(false).setMaxSkippableInternalKeys(0).setValueSizeSoftLimit(-1)
            assertFalse(options.backgroundPurgeOnIteratorCleanup())
            assertFalse(options.ignoreRangeDeletions())
            assertFalse(options.pinData())
            assertEquals(0L, options.maxSkippableInternalKeys())
            assertEquals(-1L, options.valueSizeSoftLimit())
        }
    }

    @Test
    fun ignoringRangeDeletionsExposesKeysCoveredByTombstones() {
        Options().setCreateIfMissing(true).setDisableAutoCompactions(true).use { options ->
            openRocksDB(options, createTestDBFolder("ReadOptionsTuningTest_rangeTombstone")).use { db ->
                val key = "a".encodeToByteArray()
                val value = "retained-until-compaction".encodeToByteArray()
                db.put(key, value)
                db.deleteRange(key, "b".encodeToByteArray())
                assertNull(db.get(key))
                ReadOptions().setIgnoreRangeDeletions(true).use { read ->
                    assertContentEquals(value, db.get(read, key))
                    read.setIgnoreRangeDeletions(false)
                    assertNull(db.get(read, key))
                }
            }
        }
    }

    @Test
    fun automaticPrefixScanPreservesBoundsAndSnapshot() {
        val path = createTestDBFolder("ReadOptionsTuningTest_boundedSnapshot")
        Options().setCreateIfMissing(true).useFixedLengthPrefixExtractor(1).use { options ->
            openRocksDB(options, path).use { db ->
                for (key in listOf("a1", "a2", "a3", "b1")) {
                    db.put(key.encodeToByteArray(), "old-$key".encodeToByteArray())
                }
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                val snapshot = assertNotNull(db.getSnapshot())
                try {
                    db.put("a2".encodeToByteArray(), "new-a2".encodeToByteArray())
                    db.put("a15".encodeToByteArray(), "new-a15".encodeToByteArray())
                    db.delete("a1".encodeToByteArray())

                    Slice("a1").use { lower ->
                        Slice("a3").use { upper ->
                            ReadOptions().setSnapshot(snapshot)
                                .setIterateLowerBound(lower).setIterateUpperBound(upper)
                                .setAutoPrefixMode(true).setPinData(true).setReadaheadSize(65_536L).use { readOptions ->
                                    db.newIterator(readOptions).use { iterator ->
                                        iterator.seek("a1".encodeToByteArray())
                                        for (key in listOf("a1", "a2")) {
                                            assertTrue(iterator.isValid())
                                            assertContentEquals(key.encodeToByteArray(), iterator.key())
                                            assertContentEquals("old-$key".encodeToByteArray(), iterator.value())
                                            iterator.next()
                                        }
                                        assertFalse(iterator.isValid())
                                        iterator.status()
                                    }
                                }
                        }
                    }
                    assertContentEquals("new-a2".encodeToByteArray(), db.get("a2".encodeToByteArray()))
                } finally {
                    db.releaseSnapshot(snapshot)
                    snapshot.close()
                }
            }
        }
    }
}
