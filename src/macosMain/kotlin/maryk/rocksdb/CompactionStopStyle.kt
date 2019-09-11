package maryk.rocksdb

actual enum class CompactionStopStyle(
    private val value: Byte
) {
    CompactionStopStyleSimilarSize(0),
    CompactionStopStyleTotalSize(1);

    actual fun getValue() = value
}

actual fun getCompactionStopStyle(value: Byte): CompactionStopStyle {
    for (compactionStopStyle in CompactionStopStyle.values()) {
        if (compactionStopStyle.getValue() == value) {
            return compactionStopStyle
        }
    }
    throw IllegalArgumentException("Illegal value provided for CompactionStopStyle.")
}
