package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertEquals

class MixedOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun mixedOptionsTest() {
        // Set a table factory and check the names
        BloomFilter().use { bloomFilter ->
            ColumnFamilyOptions().apply {
                setTableFormatConfig(
                    BlockBasedTableConfig().setFilterPolicy(bloomFilter)
                )
            }.use { cfOptions ->
                assertEquals("BlockBasedTable", cfOptions.tableFactoryName())
                cfOptions.setTableFormatConfig(PlainTableConfig())
                assertEquals("PlainTable", cfOptions.tableFactoryName())
                // Initialize a dbOptions object from cf options and
                // db options
                DBOptions().use { dbOptions ->
                    Options(dbOptions, cfOptions).use { options ->
                        assertEquals("PlainTable", options.tableFactoryName())
                        // Free instances
                    }
                }
            }
        }

        // Test Optimize for statements
        ColumnFamilyOptions().apply {
            optimizeUniversalStyleCompaction()
            optimizeLevelStyleCompaction()
            optimizeForPointLookup(1024)
        }.use {
            Options().apply {
                optimizeLevelStyleCompaction()
                optimizeLevelStyleCompaction(400)
                optimizeUniversalStyleCompaction()
                optimizeUniversalStyleCompaction(400)
                optimizeForPointLookup(1024)
                prepareForBulkLoad()
            }.use {}
        }
    }
}
