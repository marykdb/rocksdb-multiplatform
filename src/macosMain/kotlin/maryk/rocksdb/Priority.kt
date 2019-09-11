package maryk.rocksdb

actual enum class Priority(
    private val value: Byte
) {
    BOTTOM(0x0),
    LOW(0x1),
    HIGH(0x2),
    TOTAL(0x3);
}
