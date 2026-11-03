package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LiveOptionsTest {
    init { loadRocksDBLibrary() }

    @Test
    fun dbUpdatesOnlyApplyExplicitValuesAndSnapshotsDoNotFollowBuilderChanges() {
        val path = createTestDBFolder("LiveOptionsTest_database")
        Options().setCreateIfMissing(true).setMaxOpenFiles(123).setBytesPerSync(8192).use { options ->
            openRocksDB(options, path).use { db ->
                mutableDBOptionsBuilder().use { builder ->
                    builder.setMaxOpenFiles(87)
                    builder.build().use { first ->
                        builder.setMaxOpenFiles(99)
                        db.setDBOptions(first)
                    }
                }
                readPersistedOptions(path) { loaded, _ ->
                    assertEquals(87, liveOptionsMaxOpenFiles(loaded))
                    assertEquals(8192L, liveOptionsBytesPerSync(loaded))
                }
                parseMutableDBOptions("bytes_per_sync=16384;unknown_live_option=7", true).use { builder ->
                    builder.build().use { db.setDBOptions(it) }
                }
                readPersistedOptions(path) { loaded, _ ->
                    assertEquals(87, liveOptionsMaxOpenFiles(loaded))
                    assertEquals(16384L, liveOptionsBytesPerSync(loaded))
                }
                db.put(byteArrayOf(0, 1), byteArrayOf(0, 2))
                assertContentEquals(byteArrayOf(0, 2), db.get(byteArrayOf(0, 1)))
            }
        }
    }

    @Test
    fun columnFamilyUpdatesArePartialAndIsolated() {
        val path = createTestDBFolder("LiveOptionsTest_families")
        Options().setCreateIfMissing(true).setWriteBufferSize(4 * 1024 * 1024L).use { options ->
            openRocksDB(options, path).use { db ->
                ColumnFamilyOptions().setWriteBufferSize(8 * 1024 * 1024L).use { cfOptions ->
                    db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { other ->
                        mutableColumnFamilyOptionsBuilder().use { builder ->
                            builder.setDisableAutoCompactions(true)
                            builder.build().use { update ->
                                builder.setDisableAutoCompactions(false)
                                db.setOptions(other, update)
                            }
                        }
                        readPersistedOptions(path) { _, families ->
                            val default = families.getValue("default")
                            val changed = families.getValue("other")
                            assertFalse(default.disableAutoCompactions())
                            assertTrue(changed.disableAutoCompactions())
                            assertEquals(4 * 1024 * 1024L, default.writeBufferSize())
                            assertEquals(8 * 1024 * 1024L, changed.writeBufferSize())
                        }
                        parseMutableColumnFamilyOptions("write_buffer_size=2097152").use { builder ->
                            builder.build().use { db.setOptions(it) }
                        }
                        readPersistedOptions(path) { _, families ->
                            assertEquals(2097152L, families.getValue("default").writeBufferSize())
                            assertFalse(families.getValue("default").disableAutoCompactions())
                            assertEquals(8 * 1024 * 1024L, families.getValue("other").writeBufferSize())
                            assertTrue(families.getValue("other").disableAutoCompactions())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun emptyUpdatesPropagateEngineErrorsWithoutClosingTheDatabase() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("LiveOptionsTest_invalid")).use { db ->
                mutableDBOptionsBuilder().use { builder ->
                    builder.build().use { update ->
                        assertFailsWith<RocksDBException> { db.setDBOptions(update) }
                    }
                }
                mutableColumnFamilyOptionsBuilder().use { builder ->
                    builder.build().use { update ->
                        assertFailsWith<RocksDBException> { db.setOptions(update) }
                    }
                }
                db.put(byteArrayOf(1), byteArrayOf(2))
                assertContentEquals(byteArrayOf(2), db.get(byteArrayOf(1)))
            }
        }
    }

    @Test
    fun optionsUsePreservesTheBlockException() {
        val expected = IllegalStateException("option scope failed")
        val actual = assertFailsWith<IllegalStateException> {
            mutableDBOptionsBuilder().use { throw expected }
        }
        assertTrue(actual === expected)
    }

    private fun readPersistedOptions(path: String, check: (DBOptions, Map<String, ColumnFamilyOptions>) -> Unit) {
        ConfigOptions().use { config ->
            DBOptions().use { loaded ->
                val descriptors = mutableListOf<ColumnFamilyDescriptor>()
                try {
                    OptionsUtil.loadLatestOptions(config, path, loaded, descriptors)
                    check(loaded, descriptors.associate { it.getName().decodeToString() to it.getOptions() })
                } finally {
                    descriptors.forEach { it.getOptions().close() }
                }
            }
        }
    }
}

internal expect fun liveOptionsMaxOpenFiles(options: DBOptions): Int
internal expect fun liveOptionsBytesPerSync(options: DBOptions): Long
