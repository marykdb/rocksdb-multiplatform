package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ResourceConfigurationTest {
    init { loadRocksDBLibrary() }

    @Test
    fun realFlushesUseTheSharedRateLimiterAndExposeCounters() {
        RateLimiter(100_000_000, 100_000, 10, RateLimiterMode.ALL_IO, false).use { limiter ->
            assertEquals(0L, limiter.getTotalBytesThrough())
            assertEquals(0L, limiter.getTotalRequests())
            assertTrue(limiter.getSingleBurstBytes() > 0)
            Options().setCreateIfMissing(true).setCompressionType(CompressionType.NO_COMPRESSION).use { options ->
                options.setRateLimiter(limiter)
                openRocksDB(options, createTestDBFolder("ResourceConfigurationTest_rate")).use { db ->
                    repeat(300) { index -> db.put(index.toString().encodeToByteArray(), ByteArray(1024)) }
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    assertTrue(limiter.getTotalBytesThrough() > 0)
                    assertTrue(limiter.getTotalRequests() > 0)
                    limiter.setBytesPerSecond(200_000_000)
                    assertEquals(200_000_000L, limiter.getBytesPerSecond())
                }
            }
        }
        RateLimiter(100_000_000, 100_000, 10, RateLimiterMode.READS_ONLY).use { limiter ->
            DBOptions().setCreateIfMissing(true).use { options ->
                options.setRateLimiter(limiter)
                ColumnFamilyOptions().use { cfOptions ->
                    val handles = mutableListOf<ColumnFamilyHandle>()
                    try {
                        openRocksDB(options, createTestDBFolder("ResourceConfigurationTest_readRate"),
                            listOf(ColumnFamilyDescriptor(defaultColumnFamily, cfOptions)), handles).use { db ->
                            db.put(byteArrayOf(1), byteArrayOf(2))
                            assertContentEquals(byteArrayOf(2), db.get(byteArrayOf(1)))
                        }
                    } finally { handles.forEach { it.close() } }
                }
            }
        }
        RateLimiter(100_000_000, 100_000, 10, RateLimiterMode.WRITES_ONLY, true).use { limiter ->
            assertTrue(limiter.getBytesPerSecond() in 5_000_000L..100_000_000L)
        }
        RateLimiter(100_000_000).use { limiter ->
            assertEquals(10_000_000L, limiter.getSingleBurstBytes())
        }
    }

    @Test
    fun hyperClockCacheAndPartitionerWorkWithDefaultAndAdditionalFamilies() {
        HyperClockCache(8 * 1024 * 1024L, 0, -1, false).use { cache ->
            SstPartitionerFixedPrefixFactory(2).use { partitioner ->
                Options().setCreateIfMissing(true).setCompressionType(CompressionType.NO_COMPRESSION).use { options ->
                    options.setSstPartitionerFactory(partitioner)
                    options.setTableFormatConfig(BlockBasedTableConfig().setBlockCache(cache))
                    openRocksDB(options, createTestDBFolder("ResourceConfigurationTest_partition")).use { db ->
                        repeat(100) { index -> db.put(byteArrayOf((index / 10).toByte(), index.toByte()), ByteArray(1024) { index.toByte() }) }
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                        db.compactRange()
                        repeat(100) { index ->
                            assertContentEquals(ByteArray(1024) { index.toByte() }, db.get(byteArrayOf((index / 10).toByte(), index.toByte())))
                        }
                        assertTrue(cache.getUsage() > 0)
                        ColumnFamilyOptions().use { cfOptions ->
                            cfOptions.setSstPartitionerFactory(partitioner)
                            cfOptions.setTableFormatConfig(BlockBasedTableConfig().setBlockCache(cache))
                            db.createColumnFamily(ColumnFamilyDescriptor("partitioned".encodeToByteArray(), cfOptions)).use { cf ->
                                db.put(cf, byteArrayOf(1, 2), byteArrayOf(3))
                                FlushOptions().setWaitForFlush(true).use { db.flush(it, cf) }
                                assertContentEquals(byteArrayOf(3), db.get(cf, byteArrayOf(1, 2)))
                            }
                        }
                    }
                }
            }
        }
        HyperClockCache(1024 * 1024L, 1024, 0, true).use { assertTrue(it.getUsage() >= 0) }
    }

    @Test
    fun sharedCompactionLimiterCanPauseAndResumeAutomaticCompactions() {
        ConcurrentTaskLimiterImpl("shared-compactions", 0).use { limiter ->
            assertEquals("shared-compactions", limiter.name())
            assertEquals(0, limiter.outstandingTask())
            Options().setCreateIfMissing(true).setLevel0FileNumCompactionTrigger(2).use { options ->
                options.setCompactionThreadLimiter(limiter)
                openRocksDB(options, createTestDBFolder("ResourceConfigurationTest_compactions")).use { db ->
                    repeat(3) { index ->
                        db.put(byteArrayOf(index.toByte()), byteArrayOf(10))
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    }
                    assertEquals(3, db.getColumnFamilyMetaData().levels().first().files().size)
                    assertSame(limiter, limiter.setMaxOutstandingTask(1))
                    assertSame(limiter, limiter.resetMaxOutstandingTask())
                    db.compactRange()
                    repeat(3) { assertContentEquals(byteArrayOf(10), db.get(byteArrayOf(it.toByte()))) }
                    ColumnFamilyOptions().use { cfOptions ->
                        cfOptions.setCompactionThreadLimiter(limiter)
                        db.createColumnFamily(ColumnFamilyDescriptor("limited".encodeToByteArray(), cfOptions)).use { cf ->
                            db.put(cf, byteArrayOf(1), byteArrayOf(2))
                            assertContentEquals(byteArrayOf(2), db.get(cf, byteArrayOf(1)))
                        }
                    }
                }
            }
        }
    }
}
