package maryk.rocksdb

/** A compaction concurrency budget which can be shared by multiple column families. */
expect abstract class ConcurrentTaskLimiter : RocksObject {
    abstract fun name(): String
    /** Zero prevents new tasks; a negative limit allows unlimited tasks. */
    abstract fun setMaxOutstandingTask(maxOutstandingTask: Int): ConcurrentTaskLimiter
    abstract fun resetMaxOutstandingTask(): ConcurrentTaskLimiter
    abstract fun outstandingTask(): Int
}

expect class ConcurrentTaskLimiterImpl(name: String, maxOutstandingTask: Int) : ConcurrentTaskLimiter {
    override fun name(): String
    override fun setMaxOutstandingTask(maxOutstandingTask: Int): ConcurrentTaskLimiter
    override fun resetMaxOutstandingTask(): ConcurrentTaskLimiter
    override fun outstandingTask(): Int
}
