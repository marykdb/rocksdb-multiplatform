package maryk.rocksdb

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WriteBufferManagerTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun allowStallFlagReflectsConstruction() {
        LRUCache(4L * 1024 * 1024).use { cache ->
            WriteBufferManager(2L * 1024 * 1024, cache).use { manager ->
                assertFalse(manager.allowStall())
            }

            WriteBufferManager(2L * 1024 * 1024, cache, allowStall = true).use { manager ->
                assertTrue(manager.allowStall())
            }
        }
    }
}
