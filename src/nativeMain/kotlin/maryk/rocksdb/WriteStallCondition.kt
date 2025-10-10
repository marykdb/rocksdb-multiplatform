package maryk.rocksdb

actual enum class WriteStallCondition(internal val value: UByte) {
    DELAYED(0u),
    STOPPED(1u),
    NORMAL(2u);
}

internal fun writeStallConditionFromValue(value: UByte): WriteStallCondition =
    WriteStallCondition.entries.firstOrNull { it.value == value } ?: WriteStallCondition.NORMAL
