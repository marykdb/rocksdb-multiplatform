package maryk.rocksdb

import kotlin.test.Test

class ClockCacheTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun newClockCache() {
        val capacity: Long = 1000
        val numShardBits = 16
        val strictCapacityLimit = true
        ClockCache(
            capacity,
            numShardBits, strictCapacityLimit
        ).use {
            //no op
        }
    }
}
