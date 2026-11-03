package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabasePathsTest {
    init { loadRocksDBLibrary() }

    @Test
    fun databaseAndColumnFamilyPathsPlaceFlushedSstFilesInConfiguredDirectories() {
        val database = createTestDBFolder("DatabasePathsTest_database")
        val defaultFiles = createTestDBFolder("DatabasePathsTest_default_ssts")
        val otherFiles = createTestDBFolder("DatabasePathsTest_other_ssts")
        Options().setCreateIfMissing(true)
            .configureDbPaths(listOf(DatabasePath(defaultFiles, 1024 * 1024L))).use { options ->
                openRocksDB(options, database).use { db ->
                    db.put(byteArrayOf(1), byteArrayOf(2))
                    FlushOptions().setWaitForFlush(true).use { db.flush(it) }
                    assertEquals(defaultFiles.trimEnd('/'), db.getLiveFilesMetaData().single().path().trimEnd('/'))
                    ColumnFamilyOptions().configureCfPaths(listOf(DatabasePath(otherFiles, 1024 * 1024L))).use { cf ->
                        db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cf)).use { handle ->
                            db.put(handle, byteArrayOf(3), byteArrayOf(4))
                            FlushOptions().setWaitForFlush(true).use { db.flush(it, handle) }
                            val metadata = db.getLiveFilesMetaData().single { it.columnFamilyName().decodeToString() == "other" }
                            assertEquals(otherFiles.trimEnd('/'), metadata.path().trimEnd('/'))
                            assertContentEquals(byteArrayOf(4), db.get(handle, byteArrayOf(3)))
                        }
                    }
                }
            }
    }

    @Test
    fun optionsAndDbOptionsAcceptCopiedOrClearedPaths() {
        val paths = listOf(DatabasePath(createTestDBFolder("DatabasePathsTest_configuration"), 1024 * 1024L))
        DBOptions().use { it.configureDbPaths(paths).configureDbPaths(emptyList()) }
        Options().use { it.configureCfPaths(paths).configureCfPaths(emptyList()) }
        assertFailsWith<IllegalArgumentException> { DatabasePath("", 1) }
        assertFailsWith<IllegalArgumentException> { DatabasePath("bad\u0000path", 1) }
        assertFailsWith<IllegalArgumentException> { DatabasePath("data", -1) }
    }
}
