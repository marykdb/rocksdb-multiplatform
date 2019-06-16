package maryk.rocksdb

actual typealias BackupEngine = org.rocksdb.BackupEngine

actual fun openBackupEngine(
    env: Env,
    options: BackupableDBOptions
): BackupEngine = BackupEngine.open(
    env, options
)
