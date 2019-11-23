package maryk.rocksdb

actual enum class CompactionPriority(
    internal val value: Byte
) {
    ByCompensatedSize(0x0),
    OldestLargestSeqFirst(0x1),
    OldestSmallestSeqFirst(0x2),
    MinOverlappingRatio(0x3);
}

fun getCompactionPriority(value: Byte) =
    CompactionPriority.values().firstOrNull { it.value == value }
        ?: throw IllegalArgumentException("Illegal value provided for CompactionPriority.")
