package maryk.rocksdb

actual enum class CompactionStyle(
    private val value: Byte
) {
    LEVEL(0),
    UNIVERSAL(1),
    FIFO(2),
    NONE(3);
}
