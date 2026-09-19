package maryk.rocksdb

import kotlin.test.Test

class LRUCacheTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun newLRUCache() {
        val capacity: Long = 1000
        val numShardBits = 16
        LRUCache(
            capacity,
            numShardBits
        ).use {
            //no op
        }
    }
}
