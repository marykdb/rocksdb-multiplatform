package maryk.rocksdb

import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SharedOptionsConfigurationTest {
    init { loadRocksDBLibrary() }

    @Test
    fun defaultColumnFamilySupportsNamedAndCustomMergeOperators() {
        Options().setCreateIfMissing(true).use { options ->
            assertSame(options, options.setMergeOperatorName("stringappend"))
            exerciseMerge(options, "named", "first,second")
        }
        StringAppendOperator("|").use { operator ->
            Options().setCreateIfMissing(true).use { options ->
                assertSame(options, options.setMergeOperator(operator))
                exerciseMerge(options, "custom", "first|second")
            }
        }
    }

    private fun exerciseMerge(options: Options, suffix: String, expected: String) {
        val path = createTestDBFolder("SharedOptionsConfigurationTest_merge_$suffix")
        val key = "key".encodeToByteArray()
        openRocksDB(options, path).use { db ->
            db.put(key, "first".encodeToByteArray())
            db.merge(key, "second".encodeToByteArray())
            assertContentEquals(expected.encodeToByteArray(), db[key])
            FlushOptions().setWaitForFlush(true).use { db.flush(it) }
        }
        openRocksDB(options, path).use { db ->
            assertContentEquals(expected.encodeToByteArray(), db[key])
        }
    }

    @Test
    fun compressionPerLevelOverridesGlobalCompressionAndCanBeReset() {
        for (reset in listOf(false, true)) {
            Options().setCreateIfMissing(true).setDisableAutoCompactions(true)
                .setCompressionType(CompressionType.ZSTD_COMPRESSION).use { options ->
                    val levels = MutableList(7) { CompressionType.NO_COMPRESSION }
                    assertSame(options, options.setCompressionPerLevel(levels))
                    levels.fill(CompressionType.ZSTD_COMPRESSION)
                    if (reset) options.setCompressionPerLevel(emptyList())
                    val path = createTestDBFolder("SharedOptionsConfigurationTest_compression_$reset")
                    openRocksDB(options, path).use { db ->
                        assertSstCompression(db, null, reset)
                        ColumnFamilyOptions().setCompressionType(CompressionType.ZSTD_COMPRESSION).use { cfOptions ->
                            assertSame(cfOptions, cfOptions.setCompressionPerLevel(List(7) { CompressionType.NO_COMPRESSION }))
                            if (reset) cfOptions.setCompressionPerLevel(emptyList())
                            db.createColumnFamily(ColumnFamilyDescriptor("configured".encodeToByteArray(), cfOptions)).use { cf ->
                                assertSstCompression(db, cf, reset)
                            }
                        }
                    }
                }
        }
    }

    private fun assertSstCompression(db: RocksDB, cf: ColumnFamilyHandle?, compressed: Boolean) {
        val key = byteArrayOf(1)
        val value = ByteArray(32768) { (it % 17).toByte() }
        if (cf == null) db.put(key, value) else db.put(cf, key, value)
        FlushOptions().setWaitForFlush(true).use { if (cf == null) db.flush(it) else db.flush(it, cf) }
        val dataSize = db.tableProperties(cf).values.single().dataSize()
        if (compressed) {
            assertTrue(dataSize < value.size / 4, "The repetitive value should be compressed: $dataSize bytes")
        } else {
            assertTrue(dataSize >= value.size, "Per-level NO_COMPRESSION must override ZSTD: $dataSize bytes")
        }
        assertContentEquals(value, if (cf == null) db[key] else db.get(cf, key))
    }

    @Test
    fun rowCacheCanBeSharedByOptionsAndDbOptions() {
        LRUCache(4L * 1024 * 1024).use { cache ->
            Statistics().use { statistics ->
                Options().setCreateIfMissing(true).setStatistics(statistics).use { options ->
                    assertSame(options, options.setRowCache(cache))
                    openRocksDB(options, createTestDBFolder("SharedOptionsConfigurationTest_rowCache_options")).use { db ->
                        exerciseRowCache(db, statistics)
                    }
                }
            }
            Statistics().use { statistics ->
                DBOptions().setCreateIfMissing(true).setStatistics(statistics).use { options ->
                    assertSame(options, options.setRowCache(cache))
                    ColumnFamilyOptions().use { cfOptions ->
                        withColumnFamilyDatabase(options, cfOptions, "rowCache_dbOptions") { db ->
                            exerciseRowCache(db, statistics)
                        }
                    }
                }
            }
        }
    }

    private fun exerciseRowCache(db: RocksDB, statistics: Statistics) {
        val key = byteArrayOf(1)
        val value = byteArrayOf(2)
        db.put(key, value)
        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
        repeat(5) { assertContentEquals(value, db[key]) }
        assertTrue(statistics.getTickerCount(TickerType.ROW_CACHE_HIT) > 0)
    }

    @Test
    fun loggerReceivesMessagesAndCanBeSharedByOptionsAndDbOptions() {
        val count = ThreadSafeCounter()
        val belowThreshold = ThreadSafeCounter()
        val logger = createLogger(InfoLogLevel.INFO_LEVEL) { level, message ->
            if (level == InfoLogLevel.DEBUG_LEVEL) belowThreshold.increment()
            if (message.isNotEmpty()) count.increment()
        }
        logger.use {
            Options().setCreateIfMissing(true).setInfoLogLevel(InfoLogLevel.DEBUG_LEVEL).use { options ->
                assertSame(options, options.setLogger(logger))
                openRocksDB(options, createTestDBFolder("SharedOptionsConfigurationTest_logger_options")).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                }
            }
            val firstCount = count.value()
            assertTrue(firstCount > 0)
            DBOptions().setCreateIfMissing(true).use { options ->
                assertSame(options, options.setLogger(logger))
                ColumnFamilyOptions().use { cfOptions ->
                    withColumnFamilyDatabase(options, cfOptions, "logger_dbOptions") { db ->
                        db.put(byteArrayOf(1), byteArrayOf(2))
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    }
                }
            }
            assertTrue(count.value() > firstCount, "The shared logger must survive closing the first options")
            assertEquals(0, belowThreshold.value())
        }
    }

    private fun withColumnFamilyDatabase(
        options: DBOptions,
        cfOptions: ColumnFamilyOptions,
        suffix: String,
        block: (RocksDB) -> Unit,
    ) {
        val handles = mutableListOf<ColumnFamilyHandle>()
        try {
            openRocksDB(options, createTestDBFolder("SharedOptionsConfigurationTest_$suffix"),
                listOf(ColumnFamilyDescriptor(defaultColumnFamily, cfOptions)), handles).use { db ->
                try { block(db) } finally {
                    handles.forEach { it.close() }
                    handles.clear()
                }
            }
        } finally { handles.forEach { it.close() } }
    }

    @Test
    fun debugLoggerReceivesDebugMessagesFromFlushes() {
        val debugCount = ThreadSafeCounter()
        val logger = createLogger(InfoLogLevel.DEBUG_LEVEL) { level, message ->
            if (level == InfoLogLevel.DEBUG_LEVEL && message.isNotEmpty()) debugCount.increment()
        }
        logger.use {
            Options().setCreateIfMissing(true).setInfoLogLevel(InfoLogLevel.DEBUG_LEVEL).use { options ->
                options.setLogger(logger)
                openRocksDB(options, createTestDBFolder("SharedOptionsConfigurationTest_debugLogger")).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                }
            }
            assertTrue(debugCount.value() > 0, "The Native logger must honor DEBUG as well as INFO thresholds")
        }
    }

    @Test
    fun loggerCallbackExceptionsDoNotEscapeIntoRocksDB() {
        val messages = ThreadSafeCounter()
        createLogger(InfoLogLevel.INFO_LEVEL) { _, _ ->
            messages.increment()
            error("Deliberate logging callback failure")
        }.use { logger ->
            Options().setCreateIfMissing(true).use { options ->
                options.setLogger(logger)
                openRocksDB(options, createTestDBFolder("SharedOptionsConfigurationTest_throwingLogger")).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    assertContentEquals(byteArrayOf(2), db[byteArrayOf(1)])
                }
            }
            assertTrue(messages.value() > 0)
        }
    }

    @Test
    fun allIndexSearchTypesSupportPersistedReadsAndSeeks() {
        assertEquals(IndexSearchType.kBinary, BlockBasedTableConfig().indexSearchType())
        for (type in IndexSearchType.entries) {
            val config = BlockBasedTableConfig()
            assertSame(config, config.setIndexSearchType(type))
            assertEquals(type, config.indexSearchType())
            val path = createTestDBFolder("SharedOptionsConfigurationTest_index_${type.name}")
            Options().setCreateIfMissing(true).setTableFormatConfig(config).use { options ->
                openRocksDB(options, path).use { db ->
                    repeat(128) { db.put(indexKey(it), ByteArray(128) { it.toByte() }) }
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                }
                openRocksDB(options, path).use { db ->
                    db.newIterator().use { iterator ->
                        repeat(128) { index ->
                            val expected = ByteArray(128) { it.toByte() }
                            assertContentEquals(expected, db[indexKey(index)])
                            iterator.seek(indexKey(index))
                            assertTrue(iterator.isValid())
                            assertContentEquals(indexKey(index), iterator.key())
                            assertContentEquals(expected, iterator.value())
                        }
                    }
                }
            }
        }
    }

    private fun indexKey(index: Int) = "key-${index.toString().padStart(4, '0')}".encodeToByteArray()
}
