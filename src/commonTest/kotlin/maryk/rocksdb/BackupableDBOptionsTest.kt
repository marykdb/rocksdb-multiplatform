package maryk.rocksdb

import maryk.WindowsIgnore
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

    @WindowsIgnore("RocksDB backup engine options crash under Wine")
    @Test
    fun backupDir() {
        BackupEngineOptions(arbitraryPath).use { backupableDBOptions ->
            assertEquals(arbitraryPath, backupableDBOptions.backupDir())
        }
    }
}
