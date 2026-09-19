package maryk.rocksdb

actual enum class WALRecoveryMode(private val value: Byte) {
    TolerateCorruptedTailRecords(0),
    AbsoluteConsistency(1),
    PointInTimeRecovery(2),
    SkipAnyCorruptedRecords(3);

    actual fun getValue(): Byte = value
}

actual fun walRecoveryModeFromValue(value: Byte): WALRecoveryMode = when (value.toInt()) {
    0 -> WALRecoveryMode.TolerateCorruptedTailRecords
    1 -> WALRecoveryMode.AbsoluteConsistency
    2 -> WALRecoveryMode.PointInTimeRecovery
    3 -> WALRecoveryMode.SkipAnyCorruptedRecords
    else -> throw IllegalArgumentException("Unknown WALRecoveryMode value $value")
}
