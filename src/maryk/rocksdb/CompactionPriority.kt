package maryk.rocksdb

/** Compaction Priorities */
expect enum class CompactionPriority {
    /** Slightly Prioritize larger files by size compensated by #deletes */
    ByCompensatedSize,

    /**
     * First compact files whose data's latest update time is oldest.
     * Try this if you only update some hot keys in small ranges.
     */
    OldestLargestSeqFirst,

    /**
     * First compact files whose range hasn't been compacted to the next level
     * for the longest. If your updates are random across the key space,
     * write amplification is slightly better with this option.
     */
    OldestSmallestSeqFirst,

    /**
     * First compact files whose ratio between overlapping size in next level
     * and its size is the smallest. It in many cases can optimize write
     * amplification.
     */
    MinOverlappingRatio;
}
