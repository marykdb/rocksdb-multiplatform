package maryk.rocksdb

import maryk.createFolder
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BackupableDBOptionsTest {
    private val arbitraryPath =
        "build/test-database/BackupableDBOptionsTest"

    init {
        createFolder(arbitraryPath)
        loadRocksDBLibrary()
    }

    @Test
    fun backupDir() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            assertEquals(arbitraryPath, backupableDBOptions.backupDir())
        }
    }

    @Test
    fun env() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            assertNull(backupableDBOptions.backupEnv())

            MemEnv(getDefaultEnv()).use { env ->
                backupableDBOptions.setBackupEnv(env)
                assertEquals(env, backupableDBOptions.backupEnv())
            }
        }
    }

    @Test
    fun shareTableFiles() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextBoolean()
            backupableDBOptions.setShareTableFiles(value)
            assertEquals(value, backupableDBOptions.shareTableFiles())
        }
    }

    @Test
    fun infoLog() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            assertNull(backupableDBOptions.infoLog())

            Options().use { options ->
                object : Logger(options) {
                    override fun log(infoLogLevel: InfoLogLevel, logMsg: String) {

                    }
                }.use { logger ->
                    backupableDBOptions.setInfoLog(logger)
                    assertEquals(logger, backupableDBOptions.infoLog())
                }
            }
        }
    }

    @Test
    fun sync() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextBoolean()
            backupableDBOptions.setSync(value)
            assertEquals(value, backupableDBOptions.sync())
        }
    }

    @Test
    fun destroyOldData() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextBoolean()
            backupableDBOptions.setDestroyOldData(value)
            assertEquals(value, backupableDBOptions.destroyOldData())
        }
    }

    @Test
    fun backupLogFiles() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextBoolean()
            backupableDBOptions.setBackupLogFiles(value)
            assertEquals(value, backupableDBOptions.backupLogFiles())
        }
    }

    @Test
    fun backupRateLimit() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = abs(Random.nextLong())
            backupableDBOptions.setBackupRateLimit(value)
            assertEquals(value, backupableDBOptions.backupRateLimit())
            // negative will be mapped to 0
            backupableDBOptions.setBackupRateLimit(-1)
            assertEquals(0, backupableDBOptions.backupRateLimit())
        }
    }

    @Test
    fun backupRateLimiter() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            assertNull(backupableDBOptions.backupEnv())

            RateLimiter(999).use { backupRateLimiter ->
                backupableDBOptions.setBackupRateLimiter(backupRateLimiter)
                assertEquals(backupRateLimiter, backupableDBOptions.backupRateLimiter())
            }
        }
    }

    @Test
    fun restoreRateLimit() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = abs(Random.nextLong())
            backupableDBOptions.setRestoreRateLimit(value)
            assertEquals(value, backupableDBOptions.restoreRateLimit())
            // negative will be mapped to 0
            backupableDBOptions.setRestoreRateLimit(-1)
            assertEquals(0, backupableDBOptions.restoreRateLimit())
        }
    }

    @Test
    fun restoreRateLimiter() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            assertNull(backupableDBOptions.backupEnv())

            RateLimiter(911).use { restoreRateLimiter ->
                backupableDBOptions.setRestoreRateLimiter(restoreRateLimiter)
                assertEquals(restoreRateLimiter, backupableDBOptions.restoreRateLimiter())
            }
        }
    }

    @Test
    fun shareFilesWithChecksum() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextBoolean()
            backupableDBOptions.setShareFilesWithChecksum(value)
            assertEquals(value, backupableDBOptions.shareFilesWithChecksum())
        }
    }

    @Test
    fun maxBackgroundOperations() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextInt()
            backupableDBOptions.setMaxBackgroundOperations(value)
            assertEquals(value, backupableDBOptions.maxBackgroundOperations())
        }
    }

    @Test
    fun callbackTriggerIntervalSize() {
        BackupableDBOptions(arbitraryPath).use { backupableDBOptions ->
            val value = Random.nextLong()
            backupableDBOptions.setCallbackTriggerIntervalSize(value)
            assertEquals(value, backupableDBOptions.callbackTriggerIntervalSize())
        }
    }

    @Test
    fun failBackupDirIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.backupDir()
            }
        }
    }

    @Test
    fun failSetShareTableFilesIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setShareTableFiles(true)
            }
        }
    }

    @Test
    fun failShareTableFilesIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.shareTableFiles()
            }
        }
    }

    @Test
    fun failSetSyncIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setSync(true)
            }
        }
    }

    @Test
    fun failSyncIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.sync()
            }
        }
    }

    @Test
    fun failSetDestroyOldDataIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setDestroyOldData(true)
            }
        }
    }

    @Test
    fun failDestroyOldDataIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.destroyOldData()
            }
        }
    }

    @Test
    fun failSetBackupLogFilesIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setBackupLogFiles(true)
            }
        }
    }

    @Test
    fun failBackupLogFilesIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.backupLogFiles()
            }
        }
    }

    @Test
    fun failSetBackupRateLimitIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setBackupRateLimit(1)
            }
        }
    }

    @Test
    fun failBackupRateLimitIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.backupRateLimit()
            }
        }
    }

    @Test
    fun failSetRestoreRateLimitIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setRestoreRateLimit(1)
            }
        }
    }

    @Test
    fun failRestoreRateLimitIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.restoreRateLimit()
            }
        }
    }

    @Test
    fun failSetShareFilesWithChecksumIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.setShareFilesWithChecksum(true)
            }
        }
    }

    @Test
    fun failShareFilesWithChecksumIfDisposed() {
        setupUninitializedBackupableDBOptions().use { options ->
            assertFailsWith<AssertionError> {
                options.shareFilesWithChecksum()
            }
        }
    }

    private fun setupUninitializedBackupableDBOptions() =
        BackupableDBOptions(arbitraryPath).apply {
            close()
        }
}
