package maryk.rocksdb

import maryk.wrapWithErrorThrower
import platform.Foundation.NSNumber
import rocksdb.RocksDBBackupEngine
import rocksdb.RocksDBBackupInfo

actual class BackupEngine
    internal constructor(
        internal val native: RocksDBBackupEngine
    )
: RocksObject(), AutoCloseable {
    actual fun createNewBackup(db: RocksDB) {
        wrapWithErrorThrower { error ->
            native.createBackupForDatabase(db.native, error)
        }
    }

    actual fun createNewBackup(db: RocksDB, flushBeforeBackup: Boolean) {
        wrapWithErrorThrower { error ->
            native.createBackupForDatabase(db.native, "", flushBeforeBackup, error)
        }
    }

    actual fun createNewBackupWithMetadata(
        db: RocksDB,
        metadata: String,
        flushBeforeBackup: Boolean
    ) {
        wrapWithErrorThrower { error ->
            native.createBackupForDatabase(db.native, metadata, flushBeforeBackup, error)
        }
    }

    actual fun getBackupInfo(): List<BackupInfo> {
        return native.backupInfo().map { info ->
            val castedInfo = (info as RocksDBBackupInfo)

            BackupInfo(
                backupId = castedInfo.backupId.toInt(),
                timestamp = castedInfo.timestamp,
                size = castedInfo.size.toLong(),
                numberFiles = castedInfo.numberFiles.toInt(),
                appMetadata = castedInfo.appMetadata
            )
        }
    }

    actual fun getCorruptedBackups(): IntArray {
        @Suppress("UNCHECKED_CAST")
        val corruptedBackups = native.getCorruptedBackups() as List<NSNumber>
        return corruptedBackups.map { it.intValue }.toIntArray()
    }

    actual fun garbageCollect() {
        wrapWithErrorThrower { error ->
            native.garbageCollect(error)
        }
    }

    actual fun purgeOldBackups(numBackupsToKeep: Int) {
        wrapWithErrorThrower { error ->
            native.purgeOldBackupsKeepingLast(numBackupsToKeep.toUInt(), error)
        }
    }

    actual fun deleteBackup(backupId: Int) {
        wrapWithErrorThrower { error ->
            native.deleteBackupWithId(backupId.toUInt(), error)
        }
    }

    actual fun restoreDbFromBackup(
        backupId: Int,
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
        wrapWithErrorThrower { error ->
            native.restoreDbFromBackup(
                backupId,
                dbDir,
                walDir,
                restoreOptions.keepLogFiles,
                error
            )
        }
    }

    actual fun restoreDbFromLatestBackup(
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
        wrapWithErrorThrower { error ->
            native.restoreDbFromLatestBackup(dbDir, walDir, restoreOptions.keepLogFiles, error)
        }
    }
}

actual fun openBackupEngine(
    env: Env,
    options: BackupableDBOptions
) = Unit.wrapWithErrorThrower { error ->
    BackupEngine(
        RocksDBBackupEngine(options.backupDir(), env.native, error)
    )
}
