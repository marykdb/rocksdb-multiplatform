package maryk.rocksdb

/** Settings for the built-in SkipList memtable representation. */
expect class SkipListMemTableConfig() : MemTableConfig {
    fun setLookahead(value: Long): SkipListMemTableConfig
    fun lookahead(): Long
}

/** Settings for the built-in HashSkipList memtable representation. */
expect class HashSkipListMemTableConfig() : MemTableConfig {
    fun setBucketCount(value: Long): HashSkipListMemTableConfig
    fun bucketCount(): Long
    fun setHeight(value: Int): HashSkipListMemTableConfig
    fun height(): Int
    fun setBranchingFactor(value: Int): HashSkipListMemTableConfig
    fun branchingFactor(): Int
}

/** Settings for the built-in HashLinkedList memtable representation. */
expect class HashLinkedListMemTableConfig() : MemTableConfig {
    fun setBucketCount(value: Long): HashLinkedListMemTableConfig
    fun bucketCount(): Long
    fun setHugePageTlbSize(value: Long): HashLinkedListMemTableConfig
    fun hugePageTlbSize(): Long
    fun setBucketEntriesLoggingThreshold(value: Int): HashLinkedListMemTableConfig
    fun bucketEntriesLoggingThreshold(): Int
    fun setIfLogBucketDistWhenFlush(value: Boolean): HashLinkedListMemTableConfig
    fun ifLogBucketDistWhenFlush(): Boolean
    fun setThresholdUseSkiplist(value: Int): HashLinkedListMemTableConfig
    fun thresholdUseSkiplist(): Int
}

/** Settings for the built-in Vector memtable representation. */
expect class VectorMemTableConfig() : MemTableConfig {
    fun setReservedSize(size: Int): VectorMemTableConfig
    fun reservedSize(): Int
}
