package maryk.rocksdb

actual enum class ThreadType(
    private val value: Byte
) {
    HIGH_PRIORITY(0x0),
    LOW_PRIORITY(0x1),
    USER(0x2),
    BOTTOM_PRIORITY(0x3);
}
