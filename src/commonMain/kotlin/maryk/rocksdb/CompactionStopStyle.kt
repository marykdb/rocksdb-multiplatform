package maryk.rocksdb

/**
 * Algorithm used to make a compaction request stop picking new files
 * into a single compaction run
 */
expect enum class CompactionStopStyle {
    /** Pick files of similar size */
    CompactionStopStyleSimilarSize,

    /** Total size of picked files &gt; next file */
    CompactionStopStyleTotalSize;

    /** Returns the byte value of the enumerations value */
    fun getValue(): Byte
}

/**
 * Get CompactionStopStyle by byte value.
 *
 * @param value byte representation of CompactionStopStyle.
 *
 * @return [maryk.rocksdb.CompactionStopStyle] instance or null.
 * @throws IllegalArgumentException if an invalid
 * value is provided.
 */
expect fun getCompactionStopStyle(value: Byte): CompactionStopStyle
