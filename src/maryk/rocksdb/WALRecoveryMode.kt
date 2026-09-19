package maryk.rocksdb

/**
 * Recovery modes used when replaying the write-ahead log during open.
 */
expect enum class WALRecoveryMode {
    TolerateCorruptedTailRecords,
    AbsoluteConsistency,
    PointInTimeRecovery,
    SkipAnyCorruptedRecords;

    fun getValue(): Byte
}

expect fun walRecoveryModeFromValue(value: Byte): WALRecoveryMode
