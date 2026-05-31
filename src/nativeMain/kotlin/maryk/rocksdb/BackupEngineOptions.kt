package maryk.rocksdb

import cnames.structs.rocksdb_backup_engine_options_t
import kotlinx.cinterop.CPointer
import maryk.asUInt64
import maryk.toCheckedLong
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
        checkOwningHandle()
        return backupDirectory
    }

    actual fun setBackupEnv(env: Env): BackupEngineOptions {
        checkOwningHandle()
        env.checkOwningHandle()
        rocksdb_backup_engine_options_set_env(native, env.native)
        backupEnvRef = env
        return this
    }

    actual fun backupEnv(): Env? {
        checkOwningHandle()
        return backupEnvRef
    }

    actual fun setShareTableFiles(shareTableFiles: Boolean): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_share_table_files(native, shareTableFiles.toUByte())
        return this
    }

    actual fun shareTableFiles(): Boolean {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_share_table_files(native) != 0.toUByte()
    }

    actual fun setSync(sync: Boolean): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_sync(native, sync.toUByte())
        return this
    }

    actual fun sync(): Boolean {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_sync(native) != 0.toUByte()
    }

    actual fun setDestroyOldData(destroyOldData: Boolean): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_destroy_old_data(native, destroyOldData.toUByte())
        return this
    }

    actual fun destroyOldData(): Boolean {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_destroy_old_data(native) != 0.toUByte()
    }

    actual fun setBackupLogFiles(backupLogFiles: Boolean): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_backup_log_files(native, backupLogFiles.toUByte())
        return this
    }

    actual fun backupLogFiles(): Boolean {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_backup_log_files(native) != 0.toUByte()
    }

    actual fun setBackupRateLimit(backupRateLimit: Long): BackupEngineOptions {
        checkOwningHandle()
        val sanitized = if (backupRateLimit <= 0) 0 else backupRateLimit
        rocksdb_backup_engine_options_set_backup_rate_limit(native, sanitized.asUInt64())
        return this
    }

    actual fun backupRateLimit(): Long {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_backup_rate_limit(native).toCheckedLong("backup rate limit")
    }

    actual fun setBackupRateLimiter(rateLimiter: RateLimiter): BackupEngineOptions {
        checkOwningHandle()
        rateLimiter.checkOwningHandle()
        rocksdb_backup_engine_options_set_backup_rate_limiter(native, rateLimiter.native)
        backupRateLimiterRef = rateLimiter
        return this
    }

    actual fun backupRateLimiter(): RateLimiter? {
        checkOwningHandle()
        return backupRateLimiterRef
    }

    actual fun setRestoreRateLimit(restoreRateLimit: Long): BackupEngineOptions {
        checkOwningHandle()
        val sanitized = if (restoreRateLimit <= 0) 0 else restoreRateLimit
        rocksdb_backup_engine_options_set_restore_rate_limit(native, sanitized.asUInt64())
        return this
    }

    actual fun restoreRateLimit(): Long {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_restore_rate_limit(native).toCheckedLong("restore rate limit")
    }

    actual fun setRestoreRateLimiter(rateLimiter: RateLimiter): BackupEngineOptions {
        checkOwningHandle()
        rateLimiter.checkOwningHandle()
        rocksdb_backup_engine_options_set_restore_rate_limiter(native, rateLimiter.native)
        restoreRateLimiterRef = rateLimiter
        return this
    }

    actual fun restoreRateLimiter(): RateLimiter? {
        checkOwningHandle()
        return restoreRateLimiterRef
    }

    actual fun setShareFilesWithChecksum(
        shareFilesWithChecksum: Boolean,
    ): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_share_files_with_checksum_naming(
            native,
            if (shareFilesWithChecksum) 1 else 0,
        )
        return this
    }

    actual fun shareFilesWithChecksum(): Boolean {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_share_files_with_checksum_naming(native) != 0
    }

    actual fun setMaxBackgroundOperations(
        maxBackgroundOperations: Int,
    ): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_max_background_operations(native, maxBackgroundOperations)
        return this
    }

    actual fun maxBackgroundOperations(): Int {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_max_background_operations(native)
    }

    actual fun setCallbackTriggerIntervalSize(
        callbackTriggerIntervalSize: Long,
    ): BackupEngineOptions {
        checkOwningHandle()
        rocksdb_backup_engine_options_set_callback_trigger_interval_size(
            native,
            callbackTriggerIntervalSize.asUInt64(),
        )
        return this
    }

    actual fun callbackTriggerIntervalSize(): Long {
        checkOwningHandle()
        return rocksdb_backup_engine_options_get_callback_trigger_interval_size(native).toCheckedLong("callback trigger interval size")
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_backup_engine_options_destroy(native)
            backupEnvRef = null
            backupRateLimiterRef = null
            restoreRateLimiterRef = null
            super.close()
        }
    }
}
