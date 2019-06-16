package maryk.rocksdb

actual typealias WALRecoveryMode = org.rocksdb.WALRecoveryMode

actual fun getWALRecoveryMode(identifier: Byte) =
    WALRecoveryMode.getWALRecoveryMode(identifier)
