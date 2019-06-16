package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val MEMTABLE_SIZE = "rocksdb.size-all-mem-tables"
private const val UNFLUSHED_MEMTABLE_SIZE = "rocksdb.cur-size-all-mem-tables"
private const val TABLE_READERS = "rocksdb.estimate-table-readers-mem"

class MemoryUtilTest {
    init {
        loadRocksDBLibrary()
    }

    private val key = "some-key".encodeToByteArray()
    private val value = "some-value".encodeToByteArray()

    private fun createTestFolder() = createTestDBFolder("MemoryUtilTest")

    /**
     * Test MemoryUtil.getApproximateMemoryUsageByType before and after a put + get
     */
    @Test
    fun getApproximateMemoryUsageByType() {
        LRUCache((8 * 1024 * 1024).toLong()).use { cache ->
            Options().apply {
                setCreateIfMissing(true)
                setTableFormatConfig(BlockBasedTableConfig().setBlockCache(cache))
            }.use { options ->
                FlushOptions().apply {
                    setWaitForFlush(true)
                }.use { flushOptions ->
                    openRocksDB(options, createTestFolder()).use { db ->
                        val dbs = listOf(db)
                        val caches = setOf(cache)
                        var usage = MemoryUtil.getApproximateMemoryUsageByType(dbs, caches)

                        assertEquals(
                            db.getAggregatedLongProperty(MEMTABLE_SIZE),
                            usage[MemoryUsageType.kMemTableTotal]
                        )
                        assertEquals(
                            db.getAggregatedLongProperty(UNFLUSHED_MEMTABLE_SIZE),
                            usage[MemoryUsageType.kMemTableUnFlushed]
                        )
                        assertEquals(
                            db.getAggregatedLongProperty(TABLE_READERS),
                            usage[MemoryUsageType.kTableReadersTotal]
                        )
                        assertEquals(0, usage[MemoryUsageType.kCacheTotal])

                        db.put(key, value)
                        db.flush(flushOptions)
                        db[key]

                        usage = MemoryUtil.getApproximateMemoryUsageByType(dbs, caches)
                        assertTrue(0 < usage[MemoryUsageType.kMemTableTotal] ?: error("Should have a value"))
                        assertEquals(
                            db.getAggregatedLongProperty(MEMTABLE_SIZE),
                            usage[MemoryUsageType.kMemTableTotal]
                        )
                        assertTrue(0 < usage[MemoryUsageType.kMemTableUnFlushed] ?: error("Should have a value"))
                        assertEquals(
                            db.getAggregatedLongProperty(UNFLUSHED_MEMTABLE_SIZE),
                            usage[MemoryUsageType.kMemTableUnFlushed]
                        )
                        assertTrue(0 < usage[MemoryUsageType.kTableReadersTotal] ?: error("Should have a value"))
                        assertEquals(
                            db.getAggregatedLongProperty(TABLE_READERS),
                            usage[MemoryUsageType.kTableReadersTotal]
                        )
                        assertTrue(0 < usage[MemoryUsageType.kCacheTotal] ?: error("Should have a value"))
                    }
                }
            }
        }
    }

    /**
     * Test MemoryUtil.getApproximateMemoryUsageByType with null inputs
     */
    @Test
    fun getApproximateMemoryUsageByTypeNulls() {
        val usage = MemoryUtil.getApproximateMemoryUsageByType(null, null)

        assertEquals(null, usage[MemoryUsageType.kMemTableTotal])
        assertEquals(null, usage[MemoryUsageType.kMemTableUnFlushed])
        assertEquals(null, usage[MemoryUsageType.kTableReadersTotal])
        assertEquals(null, usage[MemoryUsageType.kCacheTotal])
    }

    /**
     * Test MemoryUtil.getApproximateMemoryUsageByType with two DBs and two caches
     */
    @Test
    fun getApproximateMemoryUsageByTypeMultiple() {
        LRUCache((1 * 1024 * 1024).toLong()).use { cache1 ->
            Options()
                .setCreateIfMissing(true)
                .setTableFormatConfig(BlockBasedTableConfig().setBlockCache(cache1)).use { options1 ->
                    openRocksDB(options1, createTestFolder()).use { db1 ->
                        LRUCache((1 * 1024 * 1024).toLong()).use { cache2 ->
                            Options().apply {
                                setCreateIfMissing(true)
                                setTableFormatConfig(BlockBasedTableConfig().setBlockCache(cache2))
                            }.use { options2 ->
                                openRocksDB(
                                    options2,
                                    createTestFolder()
                                ).use { db2 ->
                                    FlushOptions().apply {
                                        setWaitForFlush(true)
                                    }.use { flushOptions ->
                                        val dbs = listOf(db1, db2)
                                        val caches = setOf(cache1, cache2)

                                        for (db in dbs) {
                                            db.put(key, value)
                                            db.flush(flushOptions)
                                            db[key]
                                        }

                                        val usage = MemoryUtil.getApproximateMemoryUsageByType(dbs, caches)
                                        assertEquals(
                                            db1.getAggregatedLongProperty(MEMTABLE_SIZE) + db2.getAggregatedLongProperty(
                                                MEMTABLE_SIZE
                                            ), usage[MemoryUsageType.kMemTableTotal]
                                        )
                                        assertEquals(
                                            db1.getAggregatedLongProperty(UNFLUSHED_MEMTABLE_SIZE) + db2.getAggregatedLongProperty(
                                                UNFLUSHED_MEMTABLE_SIZE
                                            ), usage[MemoryUsageType.kMemTableUnFlushed]
                                        )
                                        assertEquals(
                                            db1.getAggregatedLongProperty(TABLE_READERS) + db2.getAggregatedLongProperty(
                                                TABLE_READERS
                                            ), usage[MemoryUsageType.kTableReadersTotal]
                                        )
                                        assertTrue(0 < usage[MemoryUsageType.kCacheTotal] ?: error("Should have a value"))
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}
