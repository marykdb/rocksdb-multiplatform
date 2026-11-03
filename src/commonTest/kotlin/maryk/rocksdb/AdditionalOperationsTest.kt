package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdditionalOperationsTest {
    init { loadRocksDBLibrary() }

    @Test
    fun exactExistenceHonorsEmptyValuesSnapshotsOffsetsAndColumnFamilies() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("AdditionalOperationsTest_exists")).use { db ->
                val key = byteArrayOf(0, 1)
                val padded = byteArrayOf(9, 0, 1, 9)
                assertFalse(db.keyExists(key))
                db.put(key, byteArrayOf())
                assertTrue(db.keyExists(key))
                assertTrue(db.keyExists(padded, 1, 2))
                db.withSnapshot { snapshot ->
                    db.delete(key)
                    assertFalse(db.keyExists(key))
                    assertTrue(db.keyExists(snapshot, key))
                    assertTrue(db.keyExists(snapshot, padded, 1, 2))
                }
                ColumnFamilyOptions().use { cfOptions ->
                    db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { cf ->
                        ReadOptions().use { readOptions ->
                            assertFalse(db.keyExists(cf, key))
                            db.put(cf, key, byteArrayOf())
                            assertTrue(db.keyExists(cf, key))
                            assertTrue(db.keyExists(cf, padded, 1, 2))
                            assertTrue(db.keyExists(cf, readOptions, key))
                            assertTrue(db.keyExists(cf, readOptions, padded, 1, 2))
                            assertFalse(db.keyExists(key))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun directSingleDeleteSupportsWriteOptionsAndColumnFamilies() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("AdditionalOperationsTest_singleDelete")).use { db ->
                WriteOptions().use { writeOptions ->
                    val first = byteArrayOf(1)
                    val second = byteArrayOf(2)
                    db.put(first, byteArrayOf(10))
                    db.put(second, byteArrayOf(20))
                    db.singleDelete(first)
                    db.singleDelete(writeOptions, second)
                    assertFalse(db.keyExists(first))
                    assertFalse(db.keyExists(second))
                    ColumnFamilyOptions().use { cfOptions ->
                        db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { cf ->
                            db.put(cf, first, byteArrayOf(10))
                            db.put(cf, second, byteArrayOf(20))
                            db.singleDelete(cf, first)
                            db.singleDelete(cf, writeOptions, second)
                            assertFalse(db.keyExists(cf, first))
                            assertFalse(db.keyExists(cf, second))
                        }
                    }
                }
                FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                db.compactRange()
                assertFalse(db.keyExists(byteArrayOf(1)))
                db.abortAllCompactions()
                db.abortAllCompactions()
                db.resumeAllCompactions()
                db.resumeAllCompactions()
                db.put(byteArrayOf(3), byteArrayOf(30))
                db.compactRange()
                assertContentEquals(byteArrayOf(30), db.get(byteArrayOf(3)))
            }
        }
    }

    @Test
    fun effectiveOptionsAreIndependentSafeSnapshotsAndRespectColumnFamilyUpdates() {
        Options().setCreateIfMissing(true).setMaxOpenFiles(123).setWriteBufferSize(4 * 1024 * 1024L).use { options ->
            openRocksDB(options, createTestDBFolder("AdditionalOperationsTest_effectiveOptions")).use { db ->
                val originalDB = db.effectiveDBOptions()
                val originalCF = db.effectiveColumnFamilyOptions()
                assertEquals("123", originalDB["max_open_files"])
                assertEquals("4194304", originalCF["write_buffer_size"])
                assertFalse("compression" in originalCF)
                assertFalse("blob_compression_type" in originalCF)
                mutableDBOptionsBuilder().use { builder ->
                    builder.setMaxOpenFiles(87).build().use { db.setDBOptions(it) }
                }
                assertEquals("87", db.effectiveDBOptions()["max_open_files"])
                assertEquals("123", originalDB["max_open_files"])
                ColumnFamilyOptions().use { cfOptions ->
                    db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { cf ->
                        mutableColumnFamilyOptionsBuilder().use { builder ->
                            builder.setWriteBufferSize(2097152).build().use { db.setOptions(cf, it) }
                        }
                        assertEquals("2097152", db.effectiveColumnFamilyOptions(cf)["write_buffer_size"])
                        assertEquals("4194304", db.effectiveColumnFamilyOptions()["write_buffer_size"])
                        assertEquals("4194304", originalCF["write_buffer_size"])
                    }
                }
            }
        }
    }

    @Test
    fun metadataAndTrackedFilesAreCopiedBeforeNativeObjectsClose() {
        val captured = Options().setCreateIfMissing(true).use { options ->
            getDefaultEnv().use { env ->
                SstFileManager(env).use { manager ->
                    options.setSstFileManager(manager)
                    openRocksDB(options, createTestDBFolder("AdditionalOperationsTest_metadata")).use { db ->
                        db.put(byteArrayOf(1), byteArrayOf(10))
                        db.put(byteArrayOf(2), byteArrayOf(20))
                        FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                        val files = db.getLiveFilesMetaData()
                        val sst = db.getColumnFamilyMetaData().levels().flatMap { it.files() }
                        assertTrue(files.isNotEmpty())
                        assertTrue(sst.isNotEmpty())
                        assertTrue(files.sumOf { it.numEntries() } >= 2)
                        val tracked = manager.getTrackedFiles()
                        assertTrue(tracked.isNotEmpty())
                        assertEquals(manager.getTotalSize(), tracked.values.sum())
                        Triple(files, sst, tracked)
                    }
                }
            }
        }
        val (liveFiles, sstFiles, tracked) = captured
        (liveFiles + sstFiles).forEach { file ->
            assertTrue(file.smallestSeqno() <= file.largestSeqno())
            assertTrue(file.numReadsSampled() >= 0)
            assertTrue(file.numEntries() >= file.numDeletions())
            assertFalse(file.beingCompacted())
            val checksum = file.fileChecksum()
            assertContentEquals(checksum, file.fileChecksum())
        }
        assertTrue(tracked.values.all { it > 0 })
    }

    @Test
    fun effectiveSnapshotsIgnoreNestedAndEscapedUnexposedSettings() {
        val actual = effectiveOptionSnapshot(
            "table_factory={nested={key=value};text=escaped\\;value};max_open_files=123;compression=kSnappyCompression;bytes_per_sync=8192",
            effectiveDBOptionKeys,
        )
        assertEquals(mapOf("max_open_files" to "123", "bytes_per_sync" to "8192"), actual)
        val sentinel = "18446744073709551614"
        assertEquals(
            mapOf("ttl" to "-2", "periodic_compaction_seconds" to "-2", "write_buffer_size" to sentinel),
            effectiveOptionSnapshot(
                "ttl=$sentinel;periodic_compaction_seconds=$sentinel;write_buffer_size=$sentinel",
                effectiveColumnFamilyOptionKeys,
            ),
        )
    }
}
