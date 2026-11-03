package maryk.rocksdb

import kotlin.test.*
import maryk.rocksdb.util.createTestDBFolder

class DatabaseMaintenanceTest {
    init { loadRocksDBLibrary() }

    @Test
    fun rangeStatsPropertiesAndWalSurviveClose() {
        lateinit var properties: Map<String, TableProperties>
        lateinit var wal: List<LogFile>
        Options().setCreateIfMissing(true).setDisableAutoCompactions(true).use { options ->
            openRocksDB(options, createTestDBFolder("maintenance-metadata")).use { db ->
                Slice(byteArrayOf(0)).use { start ->
                    Slice(byteArrayOf(2)).use { end ->
                        val range = Range(start, end)
                        repeat(64) { db.put(byteArrayOf(1, 0, it.toByte()), ByteArray(1024) { it.toByte() }) }
                        val stats = db.approximateMemTableStats(range)
                        assertTrue(stats.count > 0, "approximate memtable record count: ${stats.count}")
                        assertTrue(stats.size > 0)
                        assertFailsWith<IllegalArgumentException> { db.approximateSizes(listOf(range)) }
                        assertFailsWith<IllegalArgumentException> { db.approximateSizes(listOf(range), SizeApproximationFlag.NONE) }
                        val inMemory = db.approximateSizes(listOf(range), SizeApproximationFlag.INCLUDE_MEMTABLES)
                        assertEquals(1, inMemory.size)
                        assertTrue(inMemory[0] > 0)
                        db.flushWal(true)
                        wal = db.getSortedWalFiles()
                        assertTrue(wal.isNotEmpty())
                        assertTrue(wal.zipWithNext().all { (a, b) -> a.logNumber() <= b.logNumber() })
                        assertTrue(wal.all { it.sizeFileBytes() > 0 })
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                        properties = db.tableProperties()
                        assertTrue(properties.isNotEmpty())
                        assertEquals(64L, properties.values.sumOf { it.numEntries() })
                        assertTrue(db.approximateSizes(listOf(range), SizeApproximationFlag.INCLUDE_FILES)[0] > 0)
                        assertEquals(0, db.approximateSizes(emptyList(), SizeApproximationFlag.INCLUDE_FILES).size)
                    }
                }
            }
        }
        assertEquals(64L, properties.values.sumOf { it.numEntries() })
        assertTrue(wal.first().pathName().isNotEmpty())
    }

    @Test
    fun compactExplicitFilesAndPropagateErrors() {
        Options().setCreateIfMissing(true).setDisableAutoCompactions(true).use { options ->
            openRocksDB(options, createTestDBFolder("maintenance-compaction")).use { db ->
                FlushOptions().setWaitForFlush(true).use { flush ->
                    repeat(2) { batch ->
                        repeat(32) { db.put(byteArrayOf(batch.toByte(), it.toByte()), ByteArray(128) { batch.toByte() }) }
                        db.flush(flush)
                    }
                }
                val files = db.getLiveFilesMetaData().map { it.fileName() }
                assertTrue(files.size >= 2)
                CompactionOptions().use { config ->
                    val outputs = db.compactFiles(config, files, 1)
                    assertTrue(outputs.isNotEmpty())
                    assertContentEquals(ByteArray(128), db.get(byteArrayOf(0, 0)))
                    assertFailsWith<RocksDBException> { db.compactFiles(config, listOf("99999999.sst"), 1) }
                }
            }
        }
    }

    @Test
    fun binaryColumnFamilyRangesAndClippingAreIsolated() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("maintenance-clipping")).use { db ->
                ColumnFamilyOptions().use { cfOptions ->
                    db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { cf ->
                        val keys = listOf(byteArrayOf(0, 0), byteArrayOf(0, 1), byteArrayOf(0, 2), byteArrayOf(0, 3))
                        keys.forEach { db.put(cf, it, it); db.put(it, it) }
                        Slice(keys[1]).use { start ->
                            Slice(keys[3]).use { end ->
                                val range = Range(start, end)
                                assertTrue(db.approximateMemTableStats(range, cf).count > 0)
                                assertTrue(db.approximateSizes(cf, listOf(range), SizeApproximationFlag.INCLUDE_MEMTABLES)[0] > 0)
                                FlushOptions().setWaitForFlush(true).use { db.flush(it, cf) }
                                assertEquals(4L, db.tableProperties(cf).values.sumOf { it.numEntries() })
                            }
                        }
                        db.clipColumnFamily(cf, keys[1], keys[3])
                        assertNull(db.get(cf, keys[0]))
                        assertContentEquals(keys[1], db.get(cf, keys[1]))
                        assertContentEquals(keys[2], db.get(cf, keys[2]))
                        assertNull(db.get(cf, keys[3]))
                        keys.forEach { assertContentEquals(it, db.get(it)) }
                    }
                }
            }
        }
    }
}
