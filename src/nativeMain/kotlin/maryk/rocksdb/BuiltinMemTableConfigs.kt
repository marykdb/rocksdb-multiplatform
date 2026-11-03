package maryk.rocksdb

actual class SkipListMemTableConfig actual constructor() : MemTableConfig() {
    private var lookaheadValue: Long = 0L
    actual fun setLookahead(value: Long): SkipListMemTableConfig {
        lookaheadValue = value
        return this
    }
    actual fun lookahead(): Long = lookaheadValue
}

actual class HashSkipListMemTableConfig actual constructor() : MemTableConfig() {
    private var bucketCountValue: Long = 1000000L
    actual fun setBucketCount(value: Long): HashSkipListMemTableConfig {
        bucketCountValue = value
        return this
    }
    actual fun bucketCount(): Long = bucketCountValue
    private var heightValue: Int = 4
    actual fun setHeight(value: Int): HashSkipListMemTableConfig {
        heightValue = value
        return this
    }
    actual fun height(): Int = heightValue
    private var branchingFactorValue: Int = 4
    actual fun setBranchingFactor(value: Int): HashSkipListMemTableConfig {
        branchingFactorValue = value
        return this
    }
    actual fun branchingFactor(): Int = branchingFactorValue
}

actual class HashLinkedListMemTableConfig actual constructor() : MemTableConfig() {
    private var bucketCountValue: Long = 50000L
    actual fun setBucketCount(value: Long): HashLinkedListMemTableConfig {
        bucketCountValue = value
        return this
    }
    actual fun bucketCount(): Long = bucketCountValue
    private var hugePageTlbSizeValue: Long = 0L
    actual fun setHugePageTlbSize(value: Long): HashLinkedListMemTableConfig {
        hugePageTlbSizeValue = value
        return this
    }
    actual fun hugePageTlbSize(): Long = hugePageTlbSizeValue
    private var bucketEntriesLoggingThresholdValue: Int = 4096
    actual fun setBucketEntriesLoggingThreshold(value: Int): HashLinkedListMemTableConfig {
        bucketEntriesLoggingThresholdValue = value
        return this
    }
    actual fun bucketEntriesLoggingThreshold(): Int = bucketEntriesLoggingThresholdValue
    private var ifLogBucketDistWhenFlushValue: Boolean = true
    actual fun setIfLogBucketDistWhenFlush(value: Boolean): HashLinkedListMemTableConfig {
        ifLogBucketDistWhenFlushValue = value
        return this
    }
    actual fun ifLogBucketDistWhenFlush(): Boolean = ifLogBucketDistWhenFlushValue
    private var thresholdUseSkiplistValue: Int = 256
    actual fun setThresholdUseSkiplist(value: Int): HashLinkedListMemTableConfig {
        thresholdUseSkiplistValue = value
        return this
    }
    actual fun thresholdUseSkiplist(): Int = thresholdUseSkiplistValue
}

actual class VectorMemTableConfig actual constructor() : MemTableConfig() {
    private var reservedSizeValue: Int = 0
    actual fun setReservedSize(size: Int): VectorMemTableConfig {
        reservedSizeValue = size
        return this
    }
    actual fun reservedSize(): Int = reservedSizeValue
}
