package maryk.rocksdb

import maryk.createFolder
import kotlin.test.Test
import kotlin.test.assertEquals

class BackupableDBOptionsTest {
    private val arbitraryPath =
        "build/test-database/BackupableDBOptionsTest"

    init {
        createFolder(arbitraryPath)
        loadRocksDBLibrary()
    }

    @Test
    fun backupDir() {
        BackupEngineOptions(arbitraryPath).use { backupableDBOptions ->
            assertEquals(arbitraryPath, backupableDBOptions.backupDir())
        }
    }
}
