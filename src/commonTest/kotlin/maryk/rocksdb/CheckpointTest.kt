package maryk.rocksdb

import maryk.createFolder
import maryk.deleteFolder
import maryk.assertContentEquals
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CheckPointTest {
    private val checkpointFolder = "build/test-database/CheckPointTest"

    private fun createTestFolder() = createTestDBFolder("CheckPointTest")

    @BeforeTest
    fun setup() {
        createFolder(checkpointFolder)
    }

    @AfterTest
    fun cleanup() {
        deleteFolder(checkpointFolder)
    }

    @Test
    fun checkPoint() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                createCheckpoint(db).use { checkpoint ->
                    checkpoint.createCheckpoint("$checkpointFolder/snapshot1")
                    db.put("key2".encodeToByteArray(), "value2".encodeToByteArray())
                    checkpoint.createCheckpoint("$checkpointFolder/snapshot2")
                }
            }

            openRocksDB(
                options,
                "$checkpointFolder/snapshot1"
            ).use { db ->
                assertEquals("value", db["key".encodeToByteArray()]!!.decodeToString())
                assertNull(db["key2".encodeToByteArray()])
            }

            openRocksDB(
                options,
                "$checkpointFolder/snapshot2"
            ).use { db ->
                assertEquals("value", db["key".encodeToByteArray()]!!.decodeToString())
                assertEquals("value2", db["key2".encodeToByteArray()]!!.decodeToString())
            }
        }
    }

    @Test
    fun exportColumnFamilyAndImport() {
        val sourcePath = createTestFolder()
        val targetPath = createTestDBFolder("CheckPointImportTarget")
        val listTargetPath = createTestDBFolder("CheckPointImportListTarget")
        val exportPath = "$checkpointFolder/exported-cf"
        val sourceCfName = "source-cf".encodeToByteArray()
        val importedCfName = "imported-cf".encodeToByteArray()
        val key = "import-key".encodeToByteArray()
        val value = "import-value".encodeToByteArray()

        Options().setCreateIfMissing(true).use { options ->
            val metadata = openRocksDB(options, sourcePath).use { sourceDb ->
                ColumnFamilyOptions().use { cfOptions ->
                    sourceDb.createColumnFamily(ColumnFamilyDescriptor(sourceCfName, cfOptions)).use { sourceCf ->
                        sourceDb.put(sourceCf, key, value)
                        FlushOptions().setWaitForFlush(true).use { flushOptions ->
                            sourceDb.flush(flushOptions, sourceCf)
                        }
                        createCheckpoint(sourceDb).use { checkpoint ->
                            checkpoint.exportColumnFamily(sourceCf, exportPath)
                        }
                    }
                }
            }

            metadata.use { exportedMetadata ->
                openRocksDB(options, targetPath).use { targetDb ->
                    ColumnFamilyOptions().use { cfOptions ->
                        ImportColumnFamilyOptions().use { importOptions ->
                            targetDb.createColumnFamilyWithImport(
                                ColumnFamilyDescriptor(importedCfName, cfOptions),
                                importOptions,
                                exportedMetadata
                            ).use { importedCf ->
                                assertContentEquals(value, targetDb.get(importedCf, key))
                            }
                        }
                    }
                }

                openRocksDB(options, listTargetPath).use { targetDb ->
                    ColumnFamilyOptions().use { cfOptions ->
                        ImportColumnFamilyOptions().use { importOptions ->
                            targetDb.createColumnFamilyWithImport(
                                ColumnFamilyDescriptor(importedCfName, cfOptions),
                                importOptions,
                                listOf(exportedMetadata)
                            ).use { importedCf ->
                                assertContentEquals(value, targetDb.get(importedCf, key))
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun failIfDbNotInitialized() {
        openRocksDB(
            createTestFolder()
        ).use { db ->
            db.close()
            assertFailsWith<IllegalStateException> {
                createCheckpoint(db)
            }
        }
    }

    @Test
    fun failWithIllegalPath() {
        openRocksDB(createTestFolder()).use { db ->
            createCheckpoint(db).use { checkpoint ->
                assertFailsWith<RocksDBException> {
                    checkpoint.createCheckpoint("/Z:///:\\C:\\TZ/-")
                }
            }
        }
    }
}
