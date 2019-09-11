package maryk.rocksdb

actual enum class WALRecoveryMode(
    private val value: Byte
) {
    TolerateCorruptedTailRecords(0x0),
    AbsoluteConsistency(0x1),
    PointInTimeRecovery(0x2),
    SkipAnyCorruptedRecords(0x3);

    actual fun getValue() = value
}

actual fun getWALRecoveryMode(identifier: Byte): WALRecoveryMode {
    for (walRecoveryMode in WALRecoveryMode.values()) {
        if (walRecoveryMode.getValue() == identifier) {
            return walRecoveryMode
        }
    }

    throw IllegalArgumentException("Illegal value provided for WALRecoveryMode.")
}
