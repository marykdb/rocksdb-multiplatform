package maryk.rocksdb

import maryk.createFolder
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackupEngineNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun backupInfoCopiesAndFreesMetadata() {
        val dbPath = createTestDBFolder("BackupEngineNativeTest_metadata_db")
        val backupPath = createTestDBFolder("BackupEngineNativeTest_metadata_backup")
        createFolder(backupPath)
        val metadata = "native-backup-metadata"

        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())

                BackupEngineOptions(backupPath).use { backupOptions ->
                    openBackupEngine(options.getEnv(), backupOptions).use { backupEngine ->
                        backupEngine.createNewBackupWithMetadata(db, metadata, flushBeforeBackup = true)

                        assertTrue(backupEngine.getCorruptedBackups().isEmpty())
                        val backupInfo = backupEngine.getBackupInfo()
                        assertEquals(1, backupInfo.size)
                        assertEquals(metadata, backupInfo.single().appMetadata())
                    }
                }
            }
        }
    }
}
