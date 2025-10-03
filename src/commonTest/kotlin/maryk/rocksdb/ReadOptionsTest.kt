package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun verifyChecksum() {
        ReadOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setVerifyChecksums(boolValue)
            assertEquals(boolValue, opt.verifyChecksums())
        }
    }

    @Test
    fun fillCache() {
        ReadOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setFillCache(boolValue)
            assertEquals(boolValue, opt.fillCache())
        }
    }

    @Test
    fun prefixSameAsStart() {
        ReadOptions().use { opt ->
            opt.setPrefixSameAsStart(true)
            assertTrue(opt.prefixSameAsStart())
        }
    }

    @Test
    fun snapshotRoundTrip() {
        val dbPath = createTestDBFolder("ReadOptionsTest_snapshot")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                val snapshot = db.getSnapshot()
                assertNotNull(snapshot)
                try {
                    ReadOptions().use { opt ->
                        assertNull(opt.snapshot())
                        opt.setSnapshot(snapshot)
                        val boundSnapshot = opt.snapshot()
                        assertNotNull(boundSnapshot)
                        assertEquals(snapshot.getSequenceNumber(), boundSnapshot.getSequenceNumber())
                        opt.setSnapshot(null)
                        assertNull(opt.snapshot())
                    }
                } finally {
                    db.releaseSnapshot(snapshot)
                    snapshot.close()
                }
            }
        }
    }

    @Test
    fun iterateBoundsCopyData() {
        ReadOptions().use { opt ->
            assertNull(opt.iterateLowerBound())
            assertNull(opt.iterateUpperBound())

            val lower = Slice("a")
            val upper = Slice("z")

            opt.setIterateLowerBound(lower)
            opt.setIterateUpperBound(upper)

            val storedLower = opt.iterateLowerBound()
            val storedUpper = opt.iterateUpperBound()

            assertNotNull(storedLower)
            assertNotNull(storedUpper)
            assertContentEquals(lower.data(), storedLower.data())
            assertContentEquals(upper.data(), storedUpper.data())
        }
    }

    @Test
    fun readTierSelection() {
        ReadOptions().use { opt ->
            assertEquals(ReadTier.READ_ALL_TIER, opt.readTier())
            opt.setReadTier(ReadTier.BLOCK_CACHE_TIER)
            assertEquals(ReadTier.BLOCK_CACHE_TIER, opt.readTier())
            opt.setReadTier(ReadTier.PERSISTED_TIER)
            assertEquals(ReadTier.PERSISTED_TIER, opt.readTier())
        }
    }

    @Test
    fun tailingToggle() {
        ReadOptions().use { opt ->
            assertFalse(opt.tailing())
            opt.setTailing(true)
            assertTrue(opt.tailing())
            opt.setTailing(false)
            assertFalse(opt.tailing())
        }
    }

    @Test
    fun failSetVerifyChecksumUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.setVerifyChecksums(true)
            }
        }
    }

    @Test
    fun failVerifyChecksumUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.verifyChecksums()
            }
        }
    }

    @Test
    fun failSetFillCacheUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.setFillCache(true)
            }
        }
    }

    @Test
    fun failFillCacheUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.fillCache()
            }
        }
    }

    private fun setupUninitializedReadOptions() =
        ReadOptions().apply {
            close()
        }
}
