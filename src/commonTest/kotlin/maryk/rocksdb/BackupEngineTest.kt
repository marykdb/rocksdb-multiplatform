package maryk.rocksdb

import maryk.createFolder
import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackupEngineTest {
    private fun createTestFolder() = createTestDBFolder("BackupEngineTest")

    private fun createBackupFolder(): String {
        val folder = createTestFolder()
        createFolder(folder)
        return folder
    }

    @Test
    fun backupDb() {
        // Open empty database.
        Options().setCreateIfMissing(true).use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // Fill database with some test values
                prepareDatabase(db)

                // Create two backups
                BackupableDBOptions(
                    createBackupFolder()
                ).use { bopt ->
                    openBackupEngine(opt.getEnv(), bopt).use { be ->
                        be.createNewBackup(db, false)
                        be.createNewBackup(db, true)
                        verifyNumberOfValidBackups(be, 2)
                    }
                }
            }
        }
    }

    @Test
    fun deleteBackup() {
        // Open empty database.
        Options().setCreateIfMissing(true).use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // Fill database with some test values
                prepareDatabase(db)
                // Create two backups
                BackupableDBOptions(
                    createBackupFolder()
                ).use { bopt ->
                    openBackupEngine(opt.getEnv(), bopt).use { be ->
                        be.createNewBackup(db, false)
                        be.createNewBackup(db, true)
                        val backupInfo = verifyNumberOfValidBackups(be, 2)
                        // Delete the first backup
                        be.deleteBackup(backupInfo[0].backupId())
                        val newBackupInfo = verifyNumberOfValidBackups(be, 1)

                        // The second backup must remain.
                        assertEquals(backupInfo[1].backupId(), newBackupInfo[0].backupId())
                    }
                }
            }
        }
    }

    @Test
    fun purgeOldBackups() {
        // Open empty database.
        Options().setCreateIfMissing(true).use { opt ->
            openRocksDB(
                opt,
                createTestFolder()
            ).use { db ->
                // Fill database with some test values
                prepareDatabase(db)
                // Create four backups
                BackupableDBOptions(
                    createBackupFolder()
                ).use { bopt ->
                    openBackupEngine(opt.getEnv(), bopt).use { be ->
                        be.createNewBackup(db, false)
                        be.createNewBackup(db, true)
                        be.createNewBackup(db, true)
                        be.createNewBackup(db, true)
                        val backupInfo = verifyNumberOfValidBackups(be, 4)
                        // Delete everything except the latest backup
                        be.purgeOldBackups(1)
                        val newBackupInfo = verifyNumberOfValidBackups(be, 1)
                        // The latest backup must remain.
                        assertEquals(backupInfo[3].backupId(), newBackupInfo[0].backupId())
                    }
                }
            }
        }
    }

    @Test
    fun restoreLatestBackup() {
        Options().setCreateIfMissing(true).use { opt ->
            // Open empty database.
            var db: RocksDB? = null
            try {
                val testFolder = createTestFolder()
                db = openRocksDB(
                    opt,
                    testFolder
                )
                // Fill database with some test values
                prepareDatabase(db)

                BackupableDBOptions(
                    createBackupFolder()
                ).use { bopt ->
                    openBackupEngine(opt.getEnv(), bopt).use { be ->
                        be.createNewBackup(db!!, true)
                        verifyNumberOfValidBackups(be, 1)
                        db!!.put("key1".encodeToByteArray(), "valueV2".encodeToByteArray())
                        db!!.put("key2".encodeToByteArray(), "valueV2".encodeToByteArray())
                        be.createNewBackup(db!!, true)
                        verifyNumberOfValidBackups(be, 2)
                        db!!.put("key1".encodeToByteArray(), "valueV3".encodeToByteArray())
                        db!!.put("key2".encodeToByteArray(), "valueV3".encodeToByteArray())
                        assertTrue(db!!["key1".encodeToByteArray()]!!.decodeToString().endsWith("V3"))
                        assertTrue(db!!["key2".encodeToByteArray()]!!.decodeToString().endsWith("V3"))

                        db!!.close()
                        db = null

                        verifyNumberOfValidBackups(be, 2)
                        // restore db from latest backup
                        RestoreOptions(false).use { ropts ->
                            be.restoreDbFromLatestBackup(
                                testFolder,
                                testFolder, ropts
                            )
                        }

                        // Open database again.
                        db = openRocksDB(opt, testFolder)

                        // Values must have suffix V2 because of restoring latest backup.
                        assertTrue(db!!["key1".encodeToByteArray()]!!.decodeToString().endsWith("V2"))
                        assertTrue(db!!["key2".encodeToByteArray()]!!.decodeToString().endsWith("V2"))
                    }
                }
            } finally {
                if (db != null) {
                    db!!.close()
                }
            }
        }
    }

    @Test
    fun restoreFromBackup() {
        Options().setCreateIfMissing(true).use { opt ->
            var db: RocksDB? = null
            val testFolder = createTestFolder()
            try {
                // Open empty database.
                db = openRocksDB(
                    opt,
                    testFolder
                )
                // Fill database with some test values
                prepareDatabase(db)
                BackupableDBOptions(
                    createBackupFolder()
                ).use { bopt ->
                    openBackupEngine(opt.getEnv(), bopt).use { be ->
                        be.createNewBackup(db!!, true)
                        verifyNumberOfValidBackups(be, 1)
                        db!!.put("key1".encodeToByteArray(), "valueV2".encodeToByteArray())
                        db!!.put("key2".encodeToByteArray(), "valueV2".encodeToByteArray())
                        be.createNewBackup(db!!, true)
                        verifyNumberOfValidBackups(be, 2)
                        db!!.put("key1".encodeToByteArray(), "valueV3".encodeToByteArray())
                        db!!.put("key2".encodeToByteArray(), "valueV3".encodeToByteArray())
                        assertTrue(db!!["key1".encodeToByteArray()]!!.decodeToString().endsWith("V3"))
                        assertTrue(db!!["key2".encodeToByteArray()]!!.decodeToString().endsWith("V3"))

                        //close the database
                        db!!.close()
                        db = null

                        //restore the backup
                        val backupInfo = verifyNumberOfValidBackups(be, 2)
                        // restore db from first backup
                        be.restoreDbFromBackup(
                            backupInfo[0].backupId(),
                            testFolder,
                            testFolder,
                            RestoreOptions(false)
                        )
                        // Open database again.
                        db = openRocksDB(
                            opt,
                            testFolder
                        )
                        // Values must have suffix V2 because of restoring latest backup.
                        assertTrue(db!!["key1".encodeToByteArray()]!!.decodeToString().endsWith("V1"))
                        assertTrue(db!!["key2".encodeToByteArray()]!!.decodeToString().endsWith("V1"))
                    }
                }
            } finally {
                if (db != null) {
                    db!!.close()
                }
            }
        }
    }

    @Test
    fun backupDbWithMetadata() {
        // Open empty database.
        Options().setCreateIfMissing(true).use { opt ->
            openRocksDB(opt, createTestFolder()).use { db ->
                // Fill database with some test values
                prepareDatabase(db)

                // Create two backups
                BackupableDBOptions(
                    createBackupFolder()
                ).use { bopt ->
                    openBackupEngine(opt.getEnv(), bopt).use { be ->
                        val metadata = Random.nextInt().toString()
                        be.createNewBackupWithMetadata(db, metadata, true)
                        val backupInfoList = verifyNumberOfValidBackups(be, 1)
                        assertEquals(metadata, backupInfoList[0].appMetadata())
                    }
                }
            }
        }
    }

    /**
     * Verify backups.
     *
     * @param be [BackupEngine] instance.
     * @param expectedNumberOfBackups numerical value
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    private fun verifyNumberOfValidBackups(
        be: BackupEngine,
        expectedNumberOfBackups: Int
    ): List<BackupInfo> {
        // Verify that backups exist
        assertEquals(0, be.getCorruptedBackups().size)
        be.garbageCollect()
        val backupInfo = be.getBackupInfo()
        assertEquals(expectedNumberOfBackups, backupInfo.size)
        return backupInfo
    }

    /**
     * Fill database with some test values.
     *
     * @param db [RocksDB] instance.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    private fun prepareDatabase(db: RocksDB) {
        db.put("key1".encodeToByteArray(), "valueV1".encodeToByteArray())
        db.put("key2".encodeToByteArray(), "valueV1".encodeToByteArray())
    }
}
