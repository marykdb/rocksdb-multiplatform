package maryk.rocksdb

actual enum class CompactionStopStyle(
    internal val value: Byte
) {
    CompactionStopStyleSimilarSize(0),
    CompactionStopStyleTotalSize(1);
}

fun getCompactionStopStyle(value: Byte): CompactionStopStyle {
    for (compactionStopStyle in CompactionStopStyle.values()) {
        if (compactionStopStyle.value == value) {
            return compactionStopStyle
        }
    }
    throw IllegalArgumentException("Illegal value provided for CompactionStopStyle.")
}
