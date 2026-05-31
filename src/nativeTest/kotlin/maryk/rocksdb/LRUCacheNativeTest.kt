package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFailsWith

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
    fun unsupportedNativeTuningFailsFast() {
        assertFailsWith<UnsupportedOperationException> {
            LRUCache(
                capacity = 1000,
                numShardBits = 4,
                strictCapacityLimit = true
            ).close()
        }

        assertFailsWith<UnsupportedOperationException> {
            LRUCache(
                capacity = 1000,
                numShardBits = -1,
                strictCapacityLimit = false,
                highPriPoolRatio = 0.25
            ).close()
        }
    }
}
