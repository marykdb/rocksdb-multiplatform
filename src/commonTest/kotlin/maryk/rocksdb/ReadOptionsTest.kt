package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
