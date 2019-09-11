package maryk.rocksdb

actual enum class CompactionPriority(
    private val value: Byte
) {
    ByCompensatedSize(0x0),
    OldestLargestSeqFirst(0x1),
    OldestSmallestSeqFirst(0x2),
    MinOverlappingRatio(0x3);

    actual fun getValue() = value
}

actual fun getCompactionPriority(value: Byte) =
    CompactionPriority.values().firstOrNull { it.getValue() == value }
        ?: throw IllegalArgumentException("Illegal value provided for CompactionPriority.")
