package maryk.rocksdb

import maryk.assertContentEquals
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun altConstructor() {
        @Suppress("BooleanLiteralArgument")
        ReadOptions(true, true).use { opt ->
            assertTrue(opt.verifyChecksums())
            assertTrue(opt.fillCache())
        }
    }

    @Test
    fun copyConstructor() {
        ReadOptions().use { opt ->
            opt.setVerifyChecksums(false)
            opt.setFillCache(false)
            opt.setIterateUpperBound(buildRandomSlice())
            opt.setIterateLowerBound(buildRandomSlice())
            ReadOptions(opt).use { other ->
                assertEquals(opt.verifyChecksums(), other.verifyChecksums())
                assertEquals(opt.fillCache(), other.fillCache())
                assertContentEquals(opt.iterateUpperBound()!!.data(), other.iterateUpperBound()?.data())
                assertContentEquals(opt.iterateLowerBound()!!.data(), other.iterateLowerBound()?.data())
            }
        }
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
    fun tailing() {
        ReadOptions().use { opt ->
            val boolValue = Random.nextBoolean()
            opt.setTailing(boolValue)
            assertEquals(boolValue, opt.tailing())
        }
    }

    @Test
    fun snapshot() {
        ReadOptions().use { opt ->
            opt.setSnapshot(null)
            assertNull(opt.snapshot())
        }
    }

    @Test
    fun readTier() {
        ReadOptions().use { opt ->
            opt.setReadTier(ReadTier.BLOCK_CACHE_TIER)
            assertEquals(ReadTier.BLOCK_CACHE_TIER, opt.readTier())
        }
    }

    @Test
    fun totalOrderSeek() {
        ReadOptions().use { opt ->
            opt.setTotalOrderSeek(true)
            assertTrue(opt.totalOrderSeek())
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
    fun pinData() {
        ReadOptions().use { opt ->
            opt.setPinData(true)
            assertTrue(opt.pinData())
        }
    }

    @Test
    fun backgroundPurgeOnIteratorCleanup() {
        ReadOptions().use { opt ->
            opt.setBackgroundPurgeOnIteratorCleanup(true)
            assertTrue(opt.backgroundPurgeOnIteratorCleanup())
        }
    }

    @Test
    fun readaheadSize() {
        ReadOptions().use { opt ->
            val longValue = Random.nextLong()
            opt.setReadaheadSize(longValue)
            assertEquals(longValue, opt.readaheadSize())
        }
    }

    @Test
    fun ignoreRangeDeletions() {
        ReadOptions().use { opt ->
            opt.setIgnoreRangeDeletions(true)
            assertTrue(opt.ignoreRangeDeletions())
        }
    }

    @Test
    fun iterateUpperBound() {
        ReadOptions().use { opt ->
            val upperBound = buildRandomSlice()
            opt.setIterateUpperBound(upperBound)
            assertContentEquals(upperBound.data(), opt.iterateUpperBound()!!.data())
        }
    }

    @Test
    fun iterateUpperBoundNull() {
        ReadOptions().use { opt -> assertNull(opt.iterateUpperBound()) }
    }

    @Test
    fun iterateLowerBound() {
        ReadOptions().use { opt ->
            val lowerBound = buildRandomSlice()
            opt.setIterateLowerBound(lowerBound)
            assertContentEquals(lowerBound.data(), opt.iterateLowerBound()!!.data())
        }
    }

    @Test
    fun iterateLowerBoundNull() {
        ReadOptions().use { opt -> assertNull(opt.iterateLowerBound()) }
    }

    @Test
    fun tableFilter() {
        ReadOptions().use { opt -> AllTablesFilter().use { allTablesFilter -> opt.setTableFilter(allTablesFilter) } }
    }

    @Test
    fun iterStartSeqnum() {
        ReadOptions().use { opt ->
            assertEquals(0, opt.iterStartSeqnum())

            opt.setIterStartSeqnum(10)
            assertEquals(10, opt.iterStartSeqnum())
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

    @Test
    fun failSetTailingUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.setTailing(true)
            }
        }
    }

    @Test
    fun failTailingUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.tailing()
            }
        }
    }

    @Test
    fun failSetSnapshotUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.setSnapshot(null)
            }
        }
    }

    @Test
    fun failSnapshotUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.snapshot()
            }
        }
    }

    @Test
    fun failSetIterateUpperBoundUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.setIterateUpperBound(null)
            }
        }
    }

    @Test
    fun failIterateUpperBoundUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.iterateUpperBound()
            }
        }
    }

    @Test
    fun failSetIterateLowerBoundUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.setIterateLowerBound(null)
            }
        }
    }

    @Test
    fun failIterateLowerBoundUninitialized() {
        setupUninitializedReadOptions().use { readOptions ->
            assertFailsWith<AssertionError> {
                readOptions.iterateLowerBound()
            }
        }
    }

    private fun setupUninitializedReadOptions() =
        ReadOptions().apply {
            close()
        }

    private fun buildRandomSlice(): Slice {
        val sliceBytes = ByteArray(Random.nextInt(100) + 1)
        Random.nextBytes(sliceBytes)
        return Slice(sliceBytes)
    }

    private class AllTablesFilter : AbstractTableFilter() {
        override fun filter(tableProperties: TableProperties): Boolean {
            return true
        }
    }
}
