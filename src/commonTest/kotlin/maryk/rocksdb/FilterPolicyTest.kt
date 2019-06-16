package maryk.rocksdb

import kotlin.test.Test

class FilterTest {
    @Test
    fun filter() {
        // new Bloom filter
        val blockConfig = BlockBasedTableConfig()
        Options().use { options ->
            BloomFilter().use { bloomFilter ->
                blockConfig.setFilterPolicy(bloomFilter)
                options.setTableFormatConfig(blockConfig)
            }

            BloomFilter(10).use { bloomFilter ->
                blockConfig.setFilterPolicy(bloomFilter)
                options.setTableFormatConfig(blockConfig)
            }

            BloomFilter(10, false).use { bloomFilter ->
                blockConfig.setFilterPolicy(bloomFilter)
                options.setTableFormatConfig(blockConfig)
            }
        }
    }
}
