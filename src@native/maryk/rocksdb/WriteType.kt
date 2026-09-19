package maryk.rocksdb

actual enum class WriteType(
    internal val value: UInt
) {
    PUT(0u),
    MERGE(1u),
    DELETE(2u),
    SINGLE_DELETE(3u),
    DELETE_RANGE(4u),
    LOG(5u),
    XID(6u);
}

internal fun getWriteTypeByValue(value: UInt): WriteType =
    WriteType.entries.firstOrNull { it.value == value }
        ?: throw IllegalStateException("Unknown WriteType value: $value")
