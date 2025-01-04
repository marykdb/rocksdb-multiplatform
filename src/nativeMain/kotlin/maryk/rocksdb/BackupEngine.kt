package maryk.rocksdb

import kotlinx.cinterop.CPointer
import maryk.toUByte
import maryk.wrapWithErrorThrower
import cnames.structs.rocksdb_backup_engine_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
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

actual class BackupEngine
internal constructor(
    internal val native: CPointer<rocksdb_backup_engine_t>
)
    : RocksObject(), AutoCloseable {
    actual fun createNewBackup(db: RocksDB) {
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_create_new_backup(native, db.native, error)
        }
    }

    actual fun createNewBackup(db: RocksDB, flushBeforeBackup: Boolean) {
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_create_new_backup_flush(native, db.native, flushBeforeBackup.toUByte(), error)
        }
    }

    actual fun createNewBackupWithMetadata(
        db: RocksDB,
        metadata: String,
        flushBeforeBackup: Boolean
    ) {
        wrapWithErrorThrower { error ->
            val options = rocksdb.rocksdb_create_backup_options_create()
            rocksdb.rocksdb_create_backup_options_set_flush_before_backup(options, flushBeforeBackup)
            memScoped {
                var backupId = alloc<UIntVar>()
                rocksdb.rocksdb_backup_engine_create_new_backup_with_options_with_metadata(native, db.native, options, metadata, backupId.ptr, error)
            }
            rocksdb.rocksdb_create_backup_options_destroy(options)
        }
    }

    actual fun getBackupInfo(): List<BackupInfo> {
        return buildList {
            val info = rocksdb_backup_engine_get_backup_info(native)
            val count = rocksdb_backup_engine_info_count(info)

            for (i in 0 until count) {
                val appMetaData = rocksdb.rocksdb_backup_engine_info_app_metadata(info, i)
                this += BackupInfo(
                    backupId = rocksdb_backup_engine_info_backup_id(info, i).toInt(),
                    timestamp = rocksdb_backup_engine_info_timestamp(info, i),
                    size = rocksdb_backup_engine_info_size(info, i).toLong(),
                    numberFiles = rocksdb_backup_engine_info_number_files(info, i).toInt(),
                    appMetadata = appMetaData?.toKString(),
                )
                rocksdb.rocksdb_free(appMetaData)
            }

            rocksdb_backup_engine_info_destroy(info)
        }
    }

    actual fun getCorruptedBackups(): IntArray {
        return memScoped {
            wrapWithErrorThrower { error ->
                val size = alloc<size_tVar>()
                val idsPtr = rocksdb.rocksdb_backup_engine_get_corrupted_backups(native, size.ptr)

                IntArray(size.value.toInt()) { index ->
                    idsPtr?.get(index)?.toInt()!!
                }.also {
                    rocksdb.rocksdb_free(idsPtr)
                }
            }
        }
    }

    actual fun garbageCollect() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_backup_engine_garbage_collect(native, error)
        }
    }

    actual fun purgeOldBackups(numBackupsToKeep: Int) {
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_purge_old_backups(native, numBackupsToKeep.toUInt(), error)
        }
    }

    actual fun deleteBackup(backupId: Int) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_backup_engine_delete_backup(native, backupId.toUInt(), error)
        }
    }

    actual fun restoreDbFromBackup(
        backupId: Int,
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
        wrapWithErrorThrower { error ->
            rocksdb_backup_engine_restore_db_from_backup(
                native,
                dbDir,
                walDir,
                restoreOptions.native,
                backupId.toUInt(),
                error,
            )
        }
    }

    actual fun restoreDbFromLatestBackup(
        dbDir: String,
        walDir: String,
        restoreOptions: RestoreOptions
    ) {
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
        if (isOwningHandle()) {
            rocksdb_backup_engine_close(native)
            super.close()
        }
    }
}

actual fun openBackupEngine(
    env: Env,
    options: BackupEngineOptions
) = Unit.wrapWithErrorThrower { error ->
    BackupEngine(
        rocksdb_backup_engine_open_opts(options.native, env.native, error)!!
    )
}
