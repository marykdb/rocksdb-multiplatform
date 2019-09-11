package maryk.rocksdb

actual class HashLinkedListMemTableConfig actual constructor() : MemTableConfig() {
    actual fun setBucketCount(count: Long): HashLinkedListMemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bucketCount(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setHugePageTlbSize(size: Long): HashLinkedListMemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hugePageTlbSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBucketEntriesLoggingThreshold(threshold: Int): HashLinkedListMemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bucketEntriesLoggingThreshold(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setIfLogBucketDistWhenFlush(logDistribution: Boolean): HashLinkedListMemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ifLogBucketDistWhenFlush(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setThresholdUseSkiplist(threshold: Int): HashLinkedListMemTableConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun thresholdUseSkiplist(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual companion object {
        actual val DEFAULT_BUCKET_COUNT: Long
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        actual val DEFAULT_HUGE_PAGE_TLB_SIZE: Long
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        actual val DEFAULT_BUCKET_ENTRIES_LOG_THRES: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        actual val DEFAULT_IF_LOG_BUCKET_DIST_WHEN_FLUSH: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        actual val DEFAUL_THRESHOLD_USE_SKIPLIST: Int
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }
}
