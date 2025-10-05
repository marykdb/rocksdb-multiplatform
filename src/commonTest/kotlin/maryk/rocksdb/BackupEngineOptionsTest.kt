package maryk.rocksdb

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import maryk.createFolder
import maryk.rocksdb.util.createTestDBFolder

class BackupEngineOptionsTest {
    init {
        loadRocksDBLibrary()
    }

    private fun newBackupDir(suffix: String): String {
        val path = createTestDBFolder("BackupEngineOptionsTest-$suffix")
        createFolder(path)
        return path
    }

    @Test
    fun backupDir() {
        val path = newBackupDir("backupDir")
        BackupEngineOptions(path).use { options ->
            assertEquals(path, options.backupDir())
        }
    }

    @Test
    fun backupEnvRoundTrip() {
        val path = newBackupDir("env")
        val env = getDefaultEnv()
        BackupEngineOptions(path).use { options ->
            options.setBackupEnv(env)
            assertSame(env, options.backupEnv())
        }
    }

    @Test
    fun shareTableFilesRoundTrip() {
        val path = newBackupDir("shareTableFiles")
        BackupEngineOptions(path).use { options ->
            options.setShareTableFiles(true)
            assertTrue(options.shareTableFiles())

            options.setShareTableFiles(false)
            assertFalse(options.shareTableFiles())
        }
    }

    @Test
    fun syncRoundTrip() {
        val path = newBackupDir("sync")
        BackupEngineOptions(path).use { options ->
            options.setSync(true)
            assertTrue(options.sync())

            options.setSync(false)
            assertFalse(options.sync())
        }
    }

    @Test
    fun destroyOldDataRoundTrip() {
        val path = newBackupDir("destroyOldData")
        BackupEngineOptions(path).use { options ->
            options.setDestroyOldData(true)
            assertTrue(options.destroyOldData())

            options.setDestroyOldData(false)
            assertFalse(options.destroyOldData())
        }
    }

    @Test
    fun backupLogFilesRoundTrip() {
        val path = newBackupDir("backupLogFiles")
        BackupEngineOptions(path).use { options ->
            options.setBackupLogFiles(true)
            assertTrue(options.backupLogFiles())

            options.setBackupLogFiles(false)
            assertFalse(options.backupLogFiles())
        }
    }

    @Test
    fun backupRateLimit() {
        val path = newBackupDir("backupRateLimit")
        val rate = kotlin.math.abs(Random.nextLong())
        BackupEngineOptions(path).use { options ->
            options.setBackupRateLimit(rate)
            assertEquals(rate, options.backupRateLimit())

            options.setBackupRateLimit(-1)
            assertEquals(0, options.backupRateLimit())
        }
    }

    @Test
    fun backupRateLimiterReference() {
        val path = newBackupDir("backupRateLimiter")
        BackupEngineOptions(path).use { options ->
            RateLimiter(1_000).use { limiter ->
                options.setBackupRateLimiter(limiter)
                assertSame(limiter, options.backupRateLimiter())
            }
        }
    }

    @Test
    fun restoreRateLimit() {
        val path = newBackupDir("restoreRateLimit")
        val rate = kotlin.math.abs(Random.nextLong())
        BackupEngineOptions(path).use { options ->
            options.setRestoreRateLimit(rate)
            assertEquals(rate, options.restoreRateLimit())

            options.setRestoreRateLimit(-42)
            assertEquals(0, options.restoreRateLimit())
        }
    }

    @Test
    fun restoreRateLimiterReference() {
        val path = newBackupDir("restoreRateLimiter")
        BackupEngineOptions(path).use { options ->
            RateLimiter(2_000).use { limiter ->
                options.setRestoreRateLimiter(limiter)
                assertSame(limiter, options.restoreRateLimiter())
            }
        }
    }

    @Test
    fun shareFilesWithChecksumRoundTrip() {
        val path = newBackupDir("shareFilesWithChecksum")
        BackupEngineOptions(path).use { options ->
            options.setShareFilesWithChecksum(true)
            assertTrue(options.shareFilesWithChecksum())

            options.setShareFilesWithChecksum(false)
            assertFalse(options.shareFilesWithChecksum())
        }
    }

    @Test
    fun maxBackgroundOperationsRoundTrip() {
        val path = newBackupDir("maxBackground")
        val value = Random.nextInt()
        BackupEngineOptions(path).use { options ->
            options.setMaxBackgroundOperations(value)
            assertEquals(value, options.maxBackgroundOperations())
        }
    }

    @Test
    fun callbackTriggerIntervalSizeRoundTrip() {
        val path = newBackupDir("callbackInterval")
        val value = Random.nextLong()
        BackupEngineOptions(path).use { options ->
            options.setCallbackTriggerIntervalSize(value)
            assertEquals(value, options.callbackTriggerIntervalSize())
        }
    }
}
