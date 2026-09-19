package maryk.rocksdb

/**
 * For level based compaction, we can configure if we want to skip/force bottommost level compaction.
 */
expect enum class BottommostLevelCompaction {
    /** Skip bottommost level compaction */
    kSkip,
    /** Only compact bottommost level if there is a compaction filter. This is the default option */
    kIfHaveCompactionFilter,
    /** Always compact bottommost level */
    kForce,
    /**
     * Always compact bottommost level but in bottommost level avoid
     * double-compacting files created in the same compaction.
     */
    kForceOptimized,
}
