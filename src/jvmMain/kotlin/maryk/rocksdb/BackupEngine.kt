package maryk.rocksdb

import org.rocksdb.BackupEngineOptions

actual typealias BackupEngine = org.rocksdb.BackupEngine

actual fun openBackupEngine(
    env: Env,
    options: BackupEngineOptions
): BackupEngine = BackupEngine.open(
    env, options
)
