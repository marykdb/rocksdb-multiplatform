package maryk.rocksdb

import kotlinx.cinterop.toKString
import platform.posix.getenv
import maryk.createFolder
import maryk.deleteFolder
import maryk.doesFolderExist
import maryk.rocksdb.util.createTestDBFolder
import platform.windows.CP_UTF8
import platform.windows.GetACP
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WindowsUnicodePathsTest {
    @Test
    fun backupOptionsPreserveDriveRoots() {
        val root = requireNotNull(getenv("SystemDrive")).toKString() + "/"
        BackupEngineOptions(root).use { assertEquals(root, it.backupDir()) }
    }

    @Test
    fun unicodePathNeverFallsBackToALegacyEncodedLookalike() {
        val parent = createTestDBFolder("legacy-path-selection")
        assertTrue(createFolder(parent))
        val modern = "$parent/café-İ"
        // The spelling produced by interpreting the modern name's UTF-8 bytes as Windows-1252.
        val legacy = "$parent/cafÃ©-Ä°"
        val key = byteArrayOf(1)
        val legacyValue = "legacy".encodeToByteArray()
        val modernValue = "modern".encodeToByteArray()
        try {
            Options().setCreateIfMissing(true).use { options ->
                openRocksDB(options, legacy).use { it.put(key, legacyValue) }
                options.setCreateIfMissing(false)
                assertFailsWith<RocksDBException> { openRocksDB(options, modern) }
                openRocksDB(options, legacy).use { assertContentEquals(legacyValue, it[key]) }
                options.setCreateIfMissing(true)
                openRocksDB(options, modern).use { it.put(key, modernValue) }
                options.setCreateIfMissing(false)
                openRocksDB(options, modern).use { assertContentEquals(modernValue, it[key]) }
                openRocksDB(options, legacy).use { assertContentEquals(legacyValue, it[key]) }
                assertTrue(doesFolderExist(modern))
                assertTrue(doesFolderExist(legacy))
            }
        } finally {
            assertTrue(deleteFolder(parent))
        }
    }

    @Test
    fun unicodeDatabaseCheckpointAndBackupRestoreUseTheExactPaths() {
        assertTrue(GetACP() != CP_UTF8.toUInt(), "Exercise a legacy Windows code page")
        val name = "café-İ-漢字-🧪"
        val parent = createTestDBFolder("parent-$name")
        assertTrue(createFolder(parent))
        val source = "$parent/source-$name"
        val repository = "$parent/backups-$name"
        val restored = "$parent/restored-$name"
        val checkpoint = "$parent/checkpoint-$name"
        val key = "key".encodeToByteArray()
        val value = "Unicode recovery".encodeToByteArray()
        try {
            assertTrue(createFolder(repository))
            getDefaultEnv().use { env ->
                BackupEngineOptions(repository).use { backupOptions ->
                    Options().setCreateIfMissing(true).use { options ->
                        openRocksDB(options, source).use { db ->
                            val tracePath = "$parent/trace-$name"
                            db.startTrace(TraceOptions(), tracePath).use { db.put(key, value) }
                            assertTrue(traceFileSize(tracePath) > 0)
                            createCheckpoint(db).use { it.createCheckpoint(checkpoint) }
                            openBackupEngine(env, backupOptions).use { engine ->
                                engine.createNewBackup(db, true)
                                assertEquals(1, engine.getBackupInfo().size)
                            }
                        }
                        options.setCreateIfMissing(false)
                        openRocksDB(options, source).use { assertContentEquals(value, it[key]) }
                        openRocksDB(options, checkpoint).use { assertContentEquals(value, it[key]) }
                        openBackupEngine(env, backupOptions).use { engine ->
                            RestoreOptions(false).use { restoreOptions ->
                                engine.restoreDbFromLatestBackup(restored, restored, restoreOptions)
                            }
                        }
                        openRocksDB(options, restored).use { assertContentEquals(value, it[key]) }
                    }
                }
            }
            // These helpers use wide Windows APIs independently of RocksDB.
            for (path in listOf(source, repository, restored, checkpoint)) {
                assertTrue(doesFolderExist(path), "Exact Unicode directory must exist: $path")
            }
            assertTrue(deleteFolder(parent))
            assertFalse(doesFolderExist(parent))
            assertTrue(createFolder(parent), "Released handles must allow immediate path reuse")
        } finally {
            assertTrue(deleteFolder(parent))
        }
    }
}
