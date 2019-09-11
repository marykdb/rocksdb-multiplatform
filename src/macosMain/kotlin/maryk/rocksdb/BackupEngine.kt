package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class BackupEngine
    private constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle), AutoCloseable {
    actual fun createNewBackup(db: RocksDB) {
    }

    actual fun createNewBackup(db: RocksDB, flushBeforeBackup: Boolean) {
    }

    actual fun createNewBackupWithMetadata(
        db: RocksDB,
        metadata: String,
        flushBeforeBackup: Boolean
    ) {
    }

    actual fun getBackupInfo(): List<BackupInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getCorruptedBackups(): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun garbageCollect() {
    }

    actual fun purgeOldBackups(numBackupsToKeep: Int) {
    }

    actual fun deleteBackup(backupId: Int) {
    }

    actual fun restoreDbFromBackup(
        backupId: Int,
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
    }

    actual fun restoreDbFromLatestBackup(
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
    }
}

actual fun openBackupEngine(
    env: Env,
    options: BackupableDBOptions
): BackupEngine {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
