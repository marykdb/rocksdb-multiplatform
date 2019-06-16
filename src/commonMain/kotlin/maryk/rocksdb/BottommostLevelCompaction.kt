package maryk.rocksdb

/**
 * For level based compaction, we can configure if we want to skip/force bottommost level compaction.
 * The order of this enum MUST follow the C++ layer. See BottommostLevelCompaction in db/options.h
 */
expect enum class BottommostLevelCompaction {
    /** Skip bottommost level compaction */
    kSkip,
    /** Only compact bottommost level if there is a compaction filter. This is the default option */
    kIfHaveCompactionFilter,
    /** Always compact bottommost level */
    kForce
}
