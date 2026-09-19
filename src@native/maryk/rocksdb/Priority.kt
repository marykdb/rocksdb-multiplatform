package maryk.rocksdb

actual enum class Priority(
    internal val value: UByte
) {
    BOTTOM(0u),
    LOW(1u),
    HIGH(2u),
    USER(3u),
    TOTAL(4u);
}
