package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.ChecksumType.kNoChecksum
import maryk.rocksdb.ChecksumType.kxxHash
import maryk.rocksdb.DataBlockIndexType.kDataBlockBinaryAndHash
import maryk.rocksdb.DataBlockIndexType.kDataBlockBinarySearch
import maryk.rocksdb.IndexType.kBinarySearch
import maryk.rocksdb.IndexType.kHashSearch
import maryk.rocksdb.TickerType.BLOCK_CACHE_ADD
import maryk.rocksdb.TickerType.BLOCK_CACHE_COMPRESSED_ADD
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BlockBasedTableConfigTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("BlockBasedTableConfigTest")

    @Test
    fun cacheIndexAndFilterBlocks() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setCacheIndexAndFilterBlocks(true)
        assertTrue(blockBasedTableConfig.cacheIndexAndFilterBlocks())

    }

    @Test
    fun cacheIndexAndFilterBlocksWithHighPriority() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setCacheIndexAndFilterBlocksWithHighPriority(true)
        assertTrue(blockBasedTableConfig.cacheIndexAndFilterBlocksWithHighPriority())
    }

    @Test
    fun pinL0FilterAndIndexBlocksInCache() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setPinL0FilterAndIndexBlocksInCache(true)
        assertTrue(blockBasedTableConfig.pinL0FilterAndIndexBlocksInCache())
    }

    @Test
    fun pinTopLevelIndexAndFilter() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setPinTopLevelIndexAndFilter(false)
        assertFalse(blockBasedTableConfig.pinTopLevelIndexAndFilter())
    }

    @Test
    fun indexType() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        assertEquals(3, IndexType.values().size)
        blockBasedTableConfig.setIndexType(kHashSearch)
        assertEquals(blockBasedTableConfig.indexType(), kHashSearch)
        assertNotNull(IndexType.valueOf("kBinarySearch"))
        blockBasedTableConfig.setIndexType(IndexType.valueOf("kBinarySearch"))
        assertEquals(blockBasedTableConfig.indexType(), kBinarySearch)
    }

    @Test
    fun dataBlockIndexType() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setDataBlockIndexType(kDataBlockBinaryAndHash)
        assertEquals(blockBasedTableConfig.dataBlockIndexType(), kDataBlockBinaryAndHash)
        blockBasedTableConfig.setDataBlockIndexType(kDataBlockBinarySearch)
        assertEquals(blockBasedTableConfig.dataBlockIndexType(), kDataBlockBinarySearch)
    }

    @Test
    fun checksumType() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        assertEquals(3, ChecksumType.values().size)
        assertEquals(kxxHash, ChecksumType.valueOf("kxxHash"))
        blockBasedTableConfig.setChecksumType(kNoChecksum)
        blockBasedTableConfig.setChecksumType(kxxHash)
        assertEquals(blockBasedTableConfig.checksumType(), kxxHash)
    }

    @Test
    fun noBlockCache() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setNoBlockCache(true)
        assertTrue(blockBasedTableConfig.noBlockCache())
    }

    @Test
    fun blockCache() {
        LRUCache((17 * 1024 * 1024).toLong()).use { cache ->
            Options().setTableFormatConfig(
                BlockBasedTableConfig().setBlockCache(cache)
            ).use { options -> assertEquals("BlockBasedTable", options.tableFactoryName()) }
        }
    }

    @Test
    fun blockCacheIntegration() {
        LRUCache((8 * 1024 * 1024).toLong()).use { cache ->
            Statistics().use { statistics ->
                for (shard in 0..7) {
                    Options().apply {
                        setCreateIfMissing(true)
                        setStatistics(statistics)
                        setTableFormatConfig(
                            BlockBasedTableConfig().setBlockCache(cache)
                        )
                    }.use { options ->
                        openRocksDB(options, createTestFolder() + shard).use { db ->
                            val key = "some-key".encodeToByteArray()
                            val value = "some-value".encodeToByteArray()

                            db.put(key, value)
                            db.flush(FlushOptions())
                            db[key]

                            assertEquals((shard + 1).toLong(), statistics.getTickerCount(BLOCK_CACHE_ADD))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun persistentCache() {
        DBOptions().setInfoLogLevel(InfoLogLevel.INFO_LEVEL).setCreateIfMissing(true).use { dbOptions ->
            object : Logger(dbOptions) {
                override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {
                    println(infoLogLevel.name + ": " + logMsg)
                }
            }.use { logger ->
                PersistentCache(
                    getDefaultEnv(),
                    createTestFolder(),
                    (1024 * 1024 * 100).toLong(),
                    logger,
                    false
                ).use { persistentCache ->
                    Options().setTableFormatConfig(
                        BlockBasedTableConfig().setPersistentCache(persistentCache)
                    ).use { options -> assertEquals("BlockBasedTable", options.tableFactoryName()) }
                }
            }
        }
    }

    @Test
    fun blockCacheCompressed() {
        LRUCache((17 * 1024 * 1024).toLong()).use { cache ->
            Options().setTableFormatConfig(
                BlockBasedTableConfig().setBlockCacheCompressed(cache)
            ).use { options -> assertEquals("BlockBasedTable", options.tableFactoryName()) }
        }
    }

    @Ignore // See issue: https://github.com/facebook/rocksdb/issues/4822
    @Test
    fun blockCacheCompressedIntegration() {
        val key1 = "some-key1".encodeToByteArray()
        val key2 = "some-key1".encodeToByteArray()
        val key3 = "some-key1".encodeToByteArray()
        val key4 = "some-key1".encodeToByteArray()
        val value = "some-value".encodeToByteArray()

        LRUCache((8 * 1024 * 1024).toLong()).use { compressedCache ->
            Statistics().use { statistics ->

                val blockBasedTableConfig = BlockBasedTableConfig()
                    .setNoBlockCache(true)
                    .setBlockCache(null)
                    .setBlockCacheCompressed(compressedCache)
                    .setFormatVersion(4)

                Options().apply {
                    setCreateIfMissing(true)
                    setStatistics(statistics)
                    setTableFormatConfig(blockBasedTableConfig)
                }.use { options ->
                    for (shard in 0..7) {
                        FlushOptions().use { flushOptions ->
                            WriteOptions().use { writeOptions ->
                                ReadOptions().use { readOptions ->
                                    openRocksDB(options, createTestFolder() + shard)
                                        .use { db ->
                                            db.put(writeOptions, key1, value)
                                            db.put(writeOptions, key2, value)
                                            db.put(writeOptions, key3, value)
                                            db.put(writeOptions, key4, value)
                                            db.flush(flushOptions)

                                            db.get(readOptions, key1)
                                            db.get(readOptions, key2)
                                            db.get(readOptions, key3)
                                            db.get(readOptions, key4)

                                            assertEquals((shard + 1).toLong(), statistics.getTickerCount(BLOCK_CACHE_COMPRESSED_ADD))
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun blockSize() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setBlockSize(10)
        assertEquals(10, blockBasedTableConfig.blockSize())
    }

    @Test
    fun blockSizeDeviation() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setBlockSizeDeviation(12)
        assertEquals(12, blockBasedTableConfig.blockSizeDeviation())
    }

    @Test
    fun blockRestartInterval() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setBlockRestartInterval(15)
        assertEquals(15, blockBasedTableConfig.blockRestartInterval())
    }

    @Test
    fun indexBlockRestartInterval() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setIndexBlockRestartInterval(15)
        assertEquals(15, blockBasedTableConfig.indexBlockRestartInterval())
    }

    @Test
    fun metadataBlockSize() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setMetadataBlockSize(1024)
        assertEquals(1024, blockBasedTableConfig.metadataBlockSize())
    }

    @Test
    fun partitionFilters() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setPartitionFilters(true)
        assertTrue(blockBasedTableConfig.partitionFilters())
    }

    @Test
    fun useDeltaEncoding() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setUseDeltaEncoding(false)
        assertFalse(blockBasedTableConfig.useDeltaEncoding())
    }

    @Test
    fun blockBasedTableWithFilterPolicy() {
        Options()
            .setTableFormatConfig(
                BlockBasedTableConfig().setFilterPolicy(BloomFilter(10))
            ).use { options -> assertEquals("BlockBasedTable", options.tableFactoryName()) }
    }

    @Test
    fun blockBasedTableWithoutFilterPolicy() {
        Options().setTableFormatConfig(
            BlockBasedTableConfig().setFilterPolicy(null)
        ).use { options -> assertEquals("BlockBasedTable", options.tableFactoryName()) }
    }

    @Test
    fun wholeKeyFiltering() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setWholeKeyFiltering(false)
        assertFalse(blockBasedTableConfig.wholeKeyFiltering())
    }

    @Test
    fun verifyCompression() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setVerifyCompression(true)
        assertTrue(blockBasedTableConfig.verifyCompression())
    }

    @Test
    fun readAmpBytesPerBit() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setReadAmpBytesPerBit(2)
        assertEquals(2, blockBasedTableConfig.readAmpBytesPerBit())
    }

    @Test
    fun formatVersion() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        for (version in 0..4) {
            blockBasedTableConfig.setFormatVersion(version)
            assertEquals(version, blockBasedTableConfig.formatVersion())
        }
    }

    @Test
    fun formatVersionFailNegative() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        assertFailsWith<AssertionError> {
            blockBasedTableConfig.setFormatVersion(-1)
        }
    }

    @Test
    fun formatVersionFailIllegalVersion() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        assertFailsWith<AssertionError> {
            blockBasedTableConfig.setFormatVersion(99)
        }
    }

    @Test
    fun enableIndexCompression() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setEnableIndexCompression(false)
        assertFalse(blockBasedTableConfig.enableIndexCompression())
    }

    @Test
    fun blockAlign() {
        val blockBasedTableConfig = BlockBasedTableConfig()
        blockBasedTableConfig.setBlockAlign(true)
        assertTrue(blockBasedTableConfig.blockAlign())
    }
}
