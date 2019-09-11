package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class BackupableDBOptions
    actual constructor(path: String)
: RocksObject(newBackupableDBOptions(ensureWritableFile(path))) {
    actual fun backupDir(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBackupEnv(env: Env): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun backupEnv(): Env? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setShareTableFiles(shareTableFiles: Boolean): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun shareTableFiles(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInfoLog(logger: Logger): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun infoLog(): Logger? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSync(sync: Boolean): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun sync(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDestroyOldData(destroyOldData: Boolean): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun destroyOldData(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBackupLogFiles(backupLogFiles: Boolean): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun backupLogFiles(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBackupRateLimit(backupRateLimit: Long): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun backupRateLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBackupRateLimiter(backupRateLimiter: RateLimiter): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun backupRateLimiter(): RateLimiter? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRestoreRateLimit(restoreRateLimit: Long): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun restoreRateLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setRestoreRateLimiter(restoreRateLimiter: RateLimiter): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun restoreRateLimiter(): RateLimiter? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setShareFilesWithChecksum(shareFilesWithChecksum: Boolean): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun shareFilesWithChecksum(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBackgroundOperations(maxBackgroundOperations: Int): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBackgroundOperations(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCallbackTriggerIntervalSize(callbackTriggerIntervalSize: Long): BackupableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun callbackTriggerIntervalSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun ensureWritableFile(path: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun newBackupableDBOptions(ensureWritableFile: String): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
