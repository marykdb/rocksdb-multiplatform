package maryk.rocksdb

import kotlin.test.Test

class LRUCacheNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun strictCapacityLimitUsesSupportedNativeFactory() {
        LRUCache(
            capacity = 1000,
            numShardBits = -1,
            strictCapacityLimit = true
        ).use {
            // no op
        }
    }

    @Test
    fun nativeTuningConstructorsUseOptionsFactory() {
        LRUCache(
            capacity = 1000,
            numShardBits = 4,
            strictCapacityLimit = true
        ).use {
            // no op
        }

        LRUCache(
            capacity = 1000,
            numShardBits = -1,
            strictCapacityLimit = false,
            highPriPoolRatio = 0.25
        ).use {
            // no op
        }

        LRUCache(
            capacity = 1000,
            numShardBits = -1,
            strictCapacityLimit = false,
            highPriPoolRatio = 0.25,
            lowPriPoolRatio = 0.10
        ).use {
            // no op
        }
    }
}
