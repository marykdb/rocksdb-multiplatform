package maryk.rocksdb

import cnames.structs.rocksdb_backup_engine_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import maryk.asUInt32
import maryk.sizeTToInt
import maryk.toUByte
import maryk.toCheckedInt
import maryk.toCheckedLong
import maryk.wrapWithErrorThrower
import platform.posix.size_tVar
import rocksdb.rocksdb_backup_engine_close
import rocksdb.rocksdb_backup_engine_create_new_backup
import rocksdb.rocksdb_backup_engine_create_new_backup_flush
import rocksdb.rocksdb_backup_engine_get_backup_info
import rocksdb.rocksdb_backup_engine_info_backup_id
import rocksdb.rocksdb_backup_engine_info_count
import rocksdb.rocksdb_backup_engine_info_destroy
import rocksdb.rocksdb_backup_engine_info_number_files
import rocksdb.rocksdb_backup_engine_info_size
import rocksdb.rocksdb_backup_engine_info_timestamp
import rocksdb.rocksdb_backup_engine_open_opts
import rocksdb.rocksdb_backup_engine_purge_old_backups
import rocksdb.rocksdb_backup_engine_restore_db_from_backup
import rocksdb.rocksdb_backup_engine_restore_db_from_latest_backup

@OptIn(UnsafeNumber::class)
actual class BackupEngine
internal constructor(
    internal val native: CPointer<rocksdb_backup_engine_t>,
    private var envRef: Env? = null,
    private var backupEnvRef: Env? = null,
)
    : RocksObject(), AutoCloseable {
    actual fun createNewBackup(db: RocksDB) {
        checkOwningHandle()
        db.checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_create_new_backup(native, db.native, error)
        }
    }

    actual fun createNewBackup(db: RocksDB, flushBeforeBackup: Boolean) {
        checkOwningHandle()
        db.checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_create_new_backup_flush(native, db.native, flushBeforeBackup.toUByte(), error)
        }
    }

    actual fun createNewBackupWithMetadata(
        db: RocksDB,
        metadata: String,
        flushBeforeBackup: Boolean
    ) {
        checkOwningHandle()
        db.checkOwningHandle()
        wrapWithErrorThrower { error ->
            val options = requireNotNull(rocksdb.rocksdb_create_backup_options_create()) {
                "Unable to allocate RocksDB backup creation options"
            }
            try {
                rocksdb.rocksdb_create_backup_options_set_flush_before_backup(options, flushBeforeBackup)
                memScoped {
                    val backupId = alloc<UIntVar>()
                    rocksdb.rocksdb_backup_engine_create_new_backup_with_options_with_metadata(native, db.native, options, metadata, backupId.ptr, error)
                }
            } finally {
                rocksdb.rocksdb_create_backup_options_destroy(options)
            }
        }
    }

    actual fun getBackupInfo(): List<BackupInfo> {
        checkOwningHandle()
        val info = requireNotNull(rocksdb_backup_engine_get_backup_info(native)) {
            "RocksDB returned null backup info"
        }
        try {
            return buildList {
                val count = rocksdb_backup_engine_info_count(info)

                for (i in 0 until count) {
                    val appMetaData = rocksdb.rocksdb_backup_engine_info_app_metadata(info, i)
                    try {
                        this += BackupInfo(
                            backupId = rocksdb_backup_engine_info_backup_id(info, i).toCheckedInt("backup id"),
                            timestamp = rocksdb_backup_engine_info_timestamp(info, i),
                            size = rocksdb_backup_engine_info_size(info, i).toCheckedLong("backup size"),
                            numberFiles = rocksdb_backup_engine_info_number_files(info, i).toCheckedInt("backup file count"),
                            appMetadata = appMetaData?.toKString(),
                        )
                    } finally {
                        rocksdb.rocksdb_free(appMetaData)
                    }
                }
            }
        } finally {
            rocksdb_backup_engine_info_destroy(info)
        }
    }

    actual fun getCorruptedBackups(): IntArray {
        checkOwningHandle()
        return memScoped {
            wrapWithErrorThrower { _ ->
                val size = alloc<size_tVar>()
                val idsPtr = rocksdb.rocksdb_backup_engine_get_corrupted_backups(native, size.ptr)
                try {
                    val count = sizeTToInt(size.value, "corrupted backup count")
                    if (count == 0) return@wrapWithErrorThrower IntArray(0)
                    val ids = requireNotNull(idsPtr) {
                        "RocksDB returned null corrupted backup ids for $count backups"
                    }
                    IntArray(count) { index ->
                        ids[index]
                    }
                } finally {
                    rocksdb.rocksdb_free(idsPtr)
                }
            }
        }
    }

    actual fun garbageCollect() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_backup_engine_garbage_collect(native, error)
        }
    }

    actual fun purgeOldBackups(numBackupsToKeep: Int) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_purge_old_backups(native, numBackupsToKeep.asUInt32(), error)
        }
    }

    actual fun deleteBackup(backupId: Int) {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_backup_engine_delete_backup(native, backupId.asUInt32(), error)
        }
    }

    actual fun restoreDbFromBackup(
        backupId: Int,
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
        checkOwningHandle()
        restoreOptions.checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_restore_db_from_backup(
                native,
                dbDir,
                walDir,
                restoreOptions.native,
                backupId.asUInt32(),
                error,
            )
        }
    }

    actual fun restoreDbFromLatestBackup(
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
        checkOwningHandle()
        restoreOptions.checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_restore_db_from_latest_backup(
                native,
                dbDir,
                walDir,
                restoreOptions.native,
                error,
            )
        }
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_backup_engine_close(native)
            envRef = null
            backupEnvRef = null
            super.close()
        }
    }
}

actual fun openBackupEngine(
    env: Env,
    options: BackupEngineOptions
) = Unit.wrapWithErrorThrower { error ->
    env.checkOwningHandle()
    options.checkOwningHandle()
    BackupEngine(
        requireNotNull(rocksdb_backup_engine_open_opts(options.native, env.native, error)) {
            "RocksDB returned null backup engine without an error"
        },
        env,
        options.backupEnv(),
    )
}
