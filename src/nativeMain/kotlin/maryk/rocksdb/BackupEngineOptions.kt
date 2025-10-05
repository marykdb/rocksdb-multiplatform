package maryk.rocksdb

import cnames.structs.rocksdb_backup_engine_options_t
import kotlinx.cinterop.CPointer
import maryk.toUByte
import platform.posix.access
import platform.posix.W_OK
import rocksdb.rocksdb_backup_engine_options_create
import rocksdb.rocksdb_backup_engine_options_destroy
import rocksdb.rocksdb_backup_engine_options_get_backup_log_files
import rocksdb.rocksdb_backup_engine_options_get_backup_rate_limit
import rocksdb.rocksdb_backup_engine_options_get_callback_trigger_interval_size
import rocksdb.rocksdb_backup_engine_options_get_destroy_old_data
import rocksdb.rocksdb_backup_engine_options_get_max_background_operations
import rocksdb.rocksdb_backup_engine_options_get_restore_rate_limit
import rocksdb.rocksdb_backup_engine_options_get_share_files_with_checksum_naming
import rocksdb.rocksdb_backup_engine_options_get_share_table_files
import rocksdb.rocksdb_backup_engine_options_get_sync
import rocksdb.rocksdb_backup_engine_options_set_backup_log_files
import rocksdb.rocksdb_backup_engine_options_set_backup_rate_limit
import rocksdb.rocksdb_backup_engine_options_set_backup_rate_limiter
import rocksdb.rocksdb_backup_engine_options_set_callback_trigger_interval_size
import rocksdb.rocksdb_backup_engine_options_set_destroy_old_data
import rocksdb.rocksdb_backup_engine_options_set_env
import rocksdb.rocksdb_backup_engine_options_set_max_background_operations
import rocksdb.rocksdb_backup_engine_options_set_restore_rate_limit
import rocksdb.rocksdb_backup_engine_options_set_restore_rate_limiter
import rocksdb.rocksdb_backup_engine_options_set_share_files_with_checksum_naming
import rocksdb.rocksdb_backup_engine_options_set_share_table_files
import rocksdb.rocksdb_backup_engine_options_set_sync

actual class BackupEngineOptions
actual constructor(path: String)
    : RocksObject() {
    private val backupDirectory: String
    internal val native: CPointer<rocksdb_backup_engine_options_t>
    private var backupEnvRef: Env? = null
    private var backupRateLimiterRef: RateLimiter? = null
    private var restoreRateLimiterRef: RateLimiter? = null

    init {
        val normalizedPath = if (path.endsWith('/')) path.dropLast(1) else path
        backupDirectory = normalizedPath
        val pathToCheck = "$normalizedPath/"
        // Use POSIX access function to check write permission
        require(access(pathToCheck, W_OK) == 0) { "Path $normalizedPath is not writable" }
        native = requireNotNull(rocksdb_backup_engine_options_create(normalizedPath)) {
            "Unable to allocate backup engine options"
        }
    }

    actual fun backupDir(): String {
        return backupDirectory
    }

    actual fun setBackupEnv(env: Env): BackupEngineOptions {
        rocksdb_backup_engine_options_set_env(native, env.native)
        backupEnvRef = env
        return this
    }

    actual fun backupEnv(): Env? = backupEnvRef

    actual fun setShareTableFiles(shareTableFiles: Boolean): BackupEngineOptions {
        rocksdb_backup_engine_options_set_share_table_files(native, shareTableFiles.toUByte())
        return this
    }

    actual fun shareTableFiles(): Boolean =
        rocksdb_backup_engine_options_get_share_table_files(native) != 0.toUByte()

    actual fun setSync(sync: Boolean): BackupEngineOptions {
        rocksdb_backup_engine_options_set_sync(native, sync.toUByte())
        return this
    }

    actual fun sync(): Boolean =
        rocksdb_backup_engine_options_get_sync(native) != 0.toUByte()

    actual fun setDestroyOldData(destroyOldData: Boolean): BackupEngineOptions {
        rocksdb_backup_engine_options_set_destroy_old_data(native, destroyOldData.toUByte())
        return this
    }

    actual fun destroyOldData(): Boolean =
        rocksdb_backup_engine_options_get_destroy_old_data(native) != 0.toUByte()

    actual fun setBackupLogFiles(backupLogFiles: Boolean): BackupEngineOptions {
        rocksdb_backup_engine_options_set_backup_log_files(native, backupLogFiles.toUByte())
        return this
    }

    actual fun backupLogFiles(): Boolean =
        rocksdb_backup_engine_options_get_backup_log_files(native) != 0.toUByte()

    actual fun setBackupRateLimit(backupRateLimit: Long): BackupEngineOptions {
        val sanitized = if (backupRateLimit <= 0) 0 else backupRateLimit
        rocksdb_backup_engine_options_set_backup_rate_limit(native, sanitized.toULong())
        return this
    }

    actual fun backupRateLimit(): Long =
        rocksdb_backup_engine_options_get_backup_rate_limit(native).toLong()

    actual fun setBackupRateLimiter(rateLimiter: RateLimiter): BackupEngineOptions {
        rocksdb_backup_engine_options_set_backup_rate_limiter(native, rateLimiter.native)
        backupRateLimiterRef = rateLimiter
        return this
    }

    actual fun backupRateLimiter(): RateLimiter? = backupRateLimiterRef

    actual fun setRestoreRateLimit(restoreRateLimit: Long): BackupEngineOptions {
        val sanitized = if (restoreRateLimit <= 0) 0 else restoreRateLimit
        rocksdb_backup_engine_options_set_restore_rate_limit(native, sanitized.toULong())
        return this
    }

    actual fun restoreRateLimit(): Long =
        rocksdb_backup_engine_options_get_restore_rate_limit(native).toLong()

    actual fun setRestoreRateLimiter(rateLimiter: RateLimiter): BackupEngineOptions {
        rocksdb_backup_engine_options_set_restore_rate_limiter(native, rateLimiter.native)
        restoreRateLimiterRef = rateLimiter
        return this
    }

    actual fun restoreRateLimiter(): RateLimiter? = restoreRateLimiterRef

    actual fun setShareFilesWithChecksum(
        shareFilesWithChecksum: Boolean,
    ): BackupEngineOptions {
        rocksdb_backup_engine_options_set_share_files_with_checksum_naming(
            native,
            if (shareFilesWithChecksum) 1 else 0,
        )
        return this
    }

    actual fun shareFilesWithChecksum(): Boolean =
        rocksdb_backup_engine_options_get_share_files_with_checksum_naming(native) != 0

    actual fun setMaxBackgroundOperations(
        maxBackgroundOperations: Int,
    ): BackupEngineOptions {
        rocksdb_backup_engine_options_set_max_background_operations(native, maxBackgroundOperations)
        return this
    }

    actual fun maxBackgroundOperations(): Int =
        rocksdb_backup_engine_options_get_max_background_operations(native)

    actual fun setCallbackTriggerIntervalSize(
        callbackTriggerIntervalSize: Long,
    ): BackupEngineOptions {
        rocksdb_backup_engine_options_set_callback_trigger_interval_size(
            native,
            callbackTriggerIntervalSize.toULong(),
        )
        return this
    }

    actual fun callbackTriggerIntervalSize(): Long =
        rocksdb_backup_engine_options_get_callback_trigger_interval_size(native).toLong()

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_backup_engine_options_destroy(native)
            super.close()
        }
    }
}
