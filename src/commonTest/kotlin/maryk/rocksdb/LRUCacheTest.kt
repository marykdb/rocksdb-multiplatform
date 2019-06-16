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
        val strictCapacityLimit = true
        val highPriPoolRatio = 5.0
        LRUCache(
            capacity,
            numShardBits, strictCapacityLimit, highPriPoolRatio
        ).use {
            //no op
        }
    }

}
