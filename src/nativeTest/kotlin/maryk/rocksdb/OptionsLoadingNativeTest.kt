package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OptionsLoadingNativeTest {
    @Test
    fun loadingReplacesCachedRuntimeReferences() {
        val path = createTestDBFolder("OptionsLoadingNativeTest_runtime_references")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).close()
        }
        MemEnv(getDefaultEnv()).use { oldEnv ->
            Statistics().use { oldStatistics ->
                DBOptions().setEnv(oldEnv).setStatistics(oldStatistics).use { loaded ->
                    ConfigOptions().use { config ->
                        val descriptors = mutableListOf<ColumnFamilyDescriptor>()
                        try {
                            OptionsUtil.loadLatestOptions(config, path, loaded, descriptors)
                            assertSame(getDefaultEnv(), loaded.getEnv())
                            assertNull(loaded.statistics())
                            assertFalse(oldStatistics.isAttachedToNative())
                        } finally {
                            descriptors.forEach { it.getOptions().close() }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun failedLoadingPreservesRuntimeReferences() {
        MemEnv(getDefaultEnv()).use { env ->
            Statistics().use { statistics ->
                DBOptions().setEnv(env).setStatistics(statistics).use { loaded ->
                    ConfigOptions().use { config ->
                        assertFailsWith<RocksDBException> {
                            OptionsUtil.loadLatestOptions(
                                config, createTestDBFolder("OptionsLoadingNativeTest_missing"),
                                loaded, mutableListOf(),
                            )
                        }
                        assertSame(env, loaded.getEnv())
                        assertSame(statistics, loaded.statistics())
                        assertTrue(statistics.isAttachedToNative())
                    }
                }
            }
        }
    }
}
