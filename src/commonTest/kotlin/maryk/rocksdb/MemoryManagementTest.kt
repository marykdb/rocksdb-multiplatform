package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MemoryManagementTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun emptySnapshotHasFourZeroCategories() {
        val memory = approximateMemoryUsage()
        assertEquals(4, memory.size)
        assertFalse(MemoryUsageType.kNumUsageTypes in memory)
        assertTrue(memory.values.all { it == 0L })
    }

    @Test
    fun sharedManagerChargesCacheAndRepeatedInputsAreCountedOnce() {
        lateinit var snapshot: Map<MemoryUsageType, Long>
        LRUCache(128L * 1024 * 1024).use { cache ->
            WriteBufferManager(64L * 1024 * 1024, cache).use { manager ->
                Options().setCreateIfMissing(true).setDisableAutoCompactions(true).use { firstOptions ->
                    Options().setCreateIfMissing(true).setDisableAutoCompactions(true).use { secondOptions ->
                        assertSame(firstOptions, firstOptions.setWriteBufferManager(manager))
                        assertSame(secondOptions, secondOptions.setWriteBufferManager(manager))
                        openRocksDB(firstOptions, createTestDBFolder("MemoryManagementTest_first")).use { first ->
                            openRocksDB(secondOptions, createTestDBFolder("MemoryManagementTest_second")).use { second ->
                                repeat(32) { index ->
                                    val key = byteArrayOf(0, index.toByte())
                                    first.put(key, ByteArray(2048) { index.toByte() })
                                    second.put(key, ByteArray(2048) { (index + 1).toByte() })
                                }
                                snapshot = approximateMemoryUsage(listOf(first, second), listOf(cache))
                                assertTrue(snapshot.getValue(MemoryUsageType.kMemTableTotal) > 0)
                                assertTrue(snapshot.getValue(MemoryUsageType.kMemTableUnFlushed) > 0)
                                assertTrue(snapshot.getValue(MemoryUsageType.kCacheTotal) > 0)
                                assertTrue(snapshot.getValue(MemoryUsageType.kMemTableTotal) >=
                                    snapshot.getValue(MemoryUsageType.kMemTableUnFlushed))
                                assertEquals(snapshot, approximateMemoryUsage(
                                    listOf(first, first, second, second), listOf(cache, cache),
                                ))
                                assertEquals(0L, approximateMemoryUsage(listOf(first, second))
                                    .getValue(MemoryUsageType.kCacheTotal))
                                assertContentEquals(ByteArray(2048), first.get(byteArrayOf(0, 0)))
                                assertContentEquals(ByteArray(2048) { 1 }, second.get(byteArrayOf(0, 0)))
                            }
                        }
                    }
                }
            }
        }
        assertTrue(snapshot.getValue(MemoryUsageType.kMemTableTotal) > 0)
        assertTrue(snapshot.getValue(MemoryUsageType.kCacheTotal) > 0)
    }

    @Test
    fun dbOptionsAttachManagerToColumnFamilyDatabase() {
        LRUCache(128L * 1024 * 1024).use { cache ->
            WriteBufferManager(64L * 1024 * 1024, cache).use { manager ->
                DBOptions().setCreateIfMissing(true).use { options ->
                    assertSame(options, options.setWriteBufferManager(manager))
                    ColumnFamilyOptions().use { columnOptions ->
                        val handles = mutableListOf<ColumnFamilyHandle>()
                        val descriptors = listOf(ColumnFamilyDescriptor(defaultColumnFamily, columnOptions))
                        openRocksDB(options, createTestDBFolder("MemoryManagementTest_dbOptions"), descriptors, handles).use { db ->
                            try {
                                db.put(handles.single(), byteArrayOf(0, 1), ByteArray(1024) { 7 })
                                val memory = approximateMemoryUsage(listOf(db), listOf(cache))
                                assertTrue(memory.getValue(MemoryUsageType.kMemTableTotal) > 0)
                                assertTrue(memory.getValue(MemoryUsageType.kCacheTotal) > 0)
                            } finally {
                                handles.forEach { it.close() }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun managerRetainsUnderlyingCacheAfterCacheWrapperCloses() {
        val cache = LRUCache(16L * 1024 * 1024)
        val manager = WriteBufferManager(8L * 1024 * 1024, cache)
        cache.close()
        manager.use {
            Options().setCreateIfMissing(true).use { options ->
                options.setWriteBufferManager(manager)
                openRocksDB(options, createTestDBFolder("MemoryManagementTest_cacheLifetime")).use { db ->
                    val key = byteArrayOf(0, 1)
                    val value = ByteArray(1024) { 9 }
                    db.put(key, value)
                    assertContentEquals(value, db.get(key))
                    assertTrue(approximateMemoryUsage(listOf(db)).getValue(MemoryUsageType.kMemTableTotal) > 0)
                }
            }
        }
        assertFalse(cache.isOwningHandle())
    }

    @Test
    fun borrowedBaseViewsAreRejectedWithoutAffectingTheirOwner() {
        Options().setCreateIfMissing(true).use { options ->
            openOptimisticTransactionDB(options, createTestDBFolder("MemoryManagementTest_borrowed")).use { db ->
                db.getBaseDB().use { borrowed ->
                    assertFailsWith<IllegalStateException> { approximateMemoryUsage(listOf(borrowed)) }
                }
                db.put(byteArrayOf(0), byteArrayOf(1))
                assertTrue(approximateMemoryUsage(listOf(db)).getValue(MemoryUsageType.kMemTableTotal) > 0)
            }
        }
    }

    @Test
    fun closedInputsAreRejectedBeforeEnteringNativeCode() {
        val db = openRocksDB(createTestDBFolder("MemoryManagementTest_closed"))
        db.close()
        assertFailsWith<IllegalStateException> { approximateMemoryUsage(listOf(db)) }

        val cache = LRUCache(1024 * 1024L)
        cache.close()
        assertFailsWith<IllegalStateException> { approximateMemoryUsage(caches = listOf(cache)) }
    }
}
