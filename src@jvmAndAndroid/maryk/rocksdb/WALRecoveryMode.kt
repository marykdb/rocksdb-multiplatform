package maryk.rocksdb

actual typealias WALRecoveryMode = org.rocksdb.WALRecoveryMode

actual fun walRecoveryModeFromValue(value: Byte): WALRecoveryMode =
    WALRecoveryMode.getWALRecoveryMode(value)
