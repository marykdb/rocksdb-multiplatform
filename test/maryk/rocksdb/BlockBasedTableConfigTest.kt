package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BlockBasedTableConfigTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun blockBasedConfigRoundTrip() {
        val cache = LRUCache((1 shl 20).toLong())
        try {
            val config = BlockBasedTableConfig()
            config.setNoBlockCache(true)
            assertTrue(config.noBlockCache())
            config.setNoBlockCache(false)
            assertFalse(config.noBlockCache())
            config
                .setBlockCache(cache)
                .setBlockSize(8L * 1024)
                .setBlockSizeDeviation(4)
                .setBlockRestartInterval(16)
                .setMetadataBlockSize(4L * 1024)
                .setPartitionFilters(true)
                .setCacheIndexAndFilterBlocks(true)
                .setCacheIndexAndFilterBlocksWithHighPriority(true)
                .setPinL0FilterAndIndexBlocksInCache(true)
                .setPinTopLevelIndexAndFilter(true)
                .setWholeKeyFiltering(true)
                .setFormatVersion(5)
                .setChecksumType(ChecksumType.kxxHash)
                .setIndexType(IndexType.kBinarySearch)
                .setDataBlockIndexType(DataBlockIndexType.kDataBlockBinaryAndHash)
                .setDataBlockHashTableUtilRatio(0.6)

            assertEquals(8 * 1024L, config.blockSize())
            assertEquals(4, config.blockSizeDeviation())
            assertEquals(16, config.blockRestartInterval())
            assertEquals(4 * 1024L, config.metadataBlockSize())
            assertTrue(config.partitionFilters())
            assertTrue(config.cacheIndexAndFilterBlocks())
            assertTrue(config.cacheIndexAndFilterBlocksWithHighPriority())
            assertTrue(config.pinL0FilterAndIndexBlocksInCache())
            assertTrue(config.pinTopLevelIndexAndFilter())
            assertTrue(config.wholeKeyFiltering())
            assertEquals(5, config.formatVersion())
            assertEquals(ChecksumType.kxxHash, config.checksumType())
            assertEquals(IndexType.kBinarySearch, config.indexType())
            assertEquals(DataBlockIndexType.kDataBlockBinaryAndHash, config.dataBlockIndexType())
            assertEquals(0.6, config.dataBlockHashTableUtilRatio())

            val dbPath = createTestDBFolder("BlockBasedTableConfigTest_roundTrip")
            Options().use { options ->
                options.setCreateIfMissing(true)
                options.setTableFormatConfig(config)

                openRocksDB(options, dbPath).use { db ->
                    val key = "key".encodeToByteArray()
                    val value = "value".encodeToByteArray()
                    db.put(key, value)
                    val loaded = db[key]
                    assertNotNull(loaded)
                    assertContentEquals(value, loaded)
                }
            }
        } finally {
            cache.close()
        }
    }
}
