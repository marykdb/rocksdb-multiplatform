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
}
