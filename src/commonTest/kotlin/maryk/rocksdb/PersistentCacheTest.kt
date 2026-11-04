package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PersistentCacheTest {
    init { loadRocksDBLibrary() }

    @Test
    fun persistentCacheCanBeSharedReplacedAndDetachedFromFactorySnapshots() {
        val logger = createLogger(InfoLogLevel.INFO_LEVEL) { _, _ -> }
        logger.use {
            val snapshots = mutableListOf<Pair<Options, Boolean>>()
            try {
                val env = getDefaultEnv()
                PersistentCache(env, createTestDBFolder("PersistentCacheTest_first"), CACHE_SIZE, logger, false).use { first ->
                    PersistentCache(env, createTestDBFolder("PersistentCacheTest_second"), CACHE_SIZE, logger, false).use { second ->
                        val config = BlockBasedTableConfig().setNoBlockCache(true)
                        // Null is valid on a fresh config as well as after attachment.
                        assertSame(config, config.setPersistentCache(null))
                        assertSame(config, config.setPersistentCache(first))
                        snapshots += snapshot(config) to true
                        config.setPersistentCache(null)
                        snapshots += snapshot(config) to false
                        config.setPersistentCache(first)
                        snapshots += snapshot(config) to true
                        config.setPersistentCache(second)
                        snapshots += snapshot(config) to true
                    }
                }
                // The caller's cache handles have closed; factories retain shared ownership.
                snapshots.forEachIndexed { index, (options, cached) ->
                    Statistics().use { statistics ->
                        options.use {
                            options.setStatistics(statistics)
                            val path = createTestDBFolder("PersistentCacheTest_database_$index")
                            val key = "key".encodeToByteArray()
                            val value = ByteArray(8192) { 42 }
                            openRocksDB(options, path).use { db ->
                                db.put(key, value)
                                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                            }
                            openRocksDB(options, path).use { db ->
                                assertContentEquals(value, db[key])
                                val misses = statistics.getTickerCount(TickerType.PERSISTENT_CACHE_MISS)
                                if (cached) {
                                    assertTrue(misses > 0, "The factory snapshot must consult its persistent cache")
                                } else {
                                    assertEquals(0L, misses, "Detaching the cache must take effect in the next factory")
                                }
                            }
                        }
                    }
                }
            } finally {
                snapshots.forEach { it.first.close() }
            }
        }
    }

    private fun snapshot(config: BlockBasedTableConfig): Options = Options()
        .setCreateIfMissing(true)
        .setDisableAutoCompactions(true)
        .setTableFormatConfig(config)

    private companion object {
        const val CACHE_SIZE = 128L * 1024 * 1024
    }
}
