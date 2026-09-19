package maryk.rocksdb

actual enum class CompactionStyle(
    internal val value: Byte
) {
    LEVEL(0),
    UNIVERSAL(1),
    FIFO(2),
    NONE(3);
}

fun getCompactionStyle(value: Byte): CompactionStyle {
    for (compactionStyle in CompactionStyle.entries) {
        if (compactionStyle.value == value) {
            return compactionStyle
        }
    }
    throw IllegalArgumentException("Illegal value provided for CompactionStyle.")
}
