package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BloomFilterTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun filterPolicyCanBeReplacedAndCleared() {
        val config = BlockBasedTableConfig()
        assertNull(config.filterPolicy())
        BloomFilter().use { first ->
            BloomFilter(8.0).use { second ->
                assertSame(config, config.setFilterPolicy(first))
                assertSame(first, config.filterPolicy())
                config.setFilterPolicy(second)
                assertSame(second, config.filterPolicy())
                config.setFilterPolicy(null)
                assertNull(config.filterPolicy())

                Options().setCreateIfMissing(true).setTableFormatConfig(config).use { options ->
                    openRocksDB(options, createTestDBFolder("BloomFilterTest_cleared")).use { db ->
                        db.put(byteArrayOf(1), byteArrayOf(2))
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                        assertEquals(0L, db.tableProperties().values.single().filterSize())
                    }
                }
            }
        }
    }

    @Test
    fun bloomFiltersPersistInSstsAndCanBeReusedAcrossOptions() {
        val constructors = listOf<() -> BloomFilter>(
            { BloomFilter() },
            { BloomFilter(9.9) },
            { BloomFilter(9.9, false) },
            { BloomFilter(9.9, true) },
        )
        constructors.forEachIndexed { variant, createFilter ->
            createFilter().use { filter ->
                val config = BlockBasedTableConfig().setFilterPolicy(filter)
                repeat(2) { instance ->
                    val path = createTestDBFolder("BloomFilterTest_${variant}_$instance")
                    Options().setCreateIfMissing(true).setTableFormatConfig(config).use { options ->
                        openRocksDB(options, path).use { db ->
                            repeat(64) { index ->
                                db.put("key-$index".encodeToByteArray(), byteArrayOf(index.toByte()))
                            }
                            FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                            val properties = db.tableProperties().values.single()
                            assertTrue(properties.filterSize() > 0, "The flushed SST must contain a filter")
                            val policyName = assertNotNull(properties.filterPolicyName())
                            assertTrue(policyName.contains("bloom", ignoreCase = true), policyName)
                        }
                        // Reopening ensures reads use the persisted SST rather than a memtable.
                        openRocksDB(options, path).use { db ->
                            repeat(64) { index ->
                                assertContentEquals(
                                    byteArrayOf(index.toByte()), db["key-$index".encodeToByteArray()],
                                )
                            }
                            assertNull(db["missing-key".encodeToByteArray()])
                        }
                    }
                }
            }
        }
    }
}
