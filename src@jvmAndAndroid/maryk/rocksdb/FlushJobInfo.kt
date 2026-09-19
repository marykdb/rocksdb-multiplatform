package maryk.rocksdb

actual class FlushJobInfo internal constructor(
    internal val delegate: org.rocksdb.FlushJobInfo
) {
    actual fun columnFamilyId(): Long = delegate.columnFamilyId

    actual fun columnFamilyName(): String = delegate.columnFamilyName

    actual fun filePath(): String = delegate.filePath

    actual fun threadId(): Long = delegate.threadId

    actual fun jobId(): Int = delegate.jobId

    actual fun triggeredWritesSlowdown(): Boolean = delegate.isTriggeredWritesSlowdown

    actual fun triggeredWritesStop(): Boolean = delegate.isTriggeredWritesStop

    actual fun smallestSeqno(): Long = delegate.smallestSeqno

    actual fun largestSeqno(): Long = delegate.largestSeqno

    actual fun tableProperties(): TableProperties = TableProperties(delegate.tableProperties)

    actual fun flushReason(): FlushReason = delegate.flushReason
}
