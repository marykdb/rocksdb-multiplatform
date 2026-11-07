package maryk.rocksdb

import maryk.createFolder
import maryk.deleteFolder
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NativePathValidationTest {
    @Test
    fun malformedPathsCannotOpenOrDestroyAnExistingDatabase() {
        val parent = createTestDBFolder("path-validation")
        assertTrue(createFolder(parent))
        val source = "$parent/source"
        val key = byteArrayOf(1)
        val value = byteArrayOf(2)
        try {
            Options().setCreateIfMissing(true).use { options ->
                openRocksDB(options, source).use { it.put(key, value) }
                options.setCreateIfMissing(false)
                for (malformed in listOf("$source\u0000ignored", "$parent/\uD800", "$parent/\uDC00")) {
                    assertFailsWith<IllegalArgumentException> { openRocksDB(options, malformed) }
                    assertFailsWith<IllegalArgumentException> { destroyRocksDB(malformed, options) }
                    assertFailsWith<IllegalArgumentException> { BackupEngineOptions(malformed) }
                }
                openRocksDB(options, source).use { assertContentEquals(value, it[key]) }
                destroyRocksDB(source, options)
            }
        } finally {
            deleteFolder(parent)
        }
    }

    @Test
    fun missingBackupDirectoriesAreReportedAsMissing() {
        val missing = createTestDBFolder("missing-backup-directory")
        val error = assertFailsWith<IllegalArgumentException> { BackupEngineOptions(missing) }
        assertTrue(error.message.orEmpty().contains("does not exist"))
    }
}
