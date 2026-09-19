package maryk.rocksdb

actual typealias BackgroundErrorReason = org.rocksdb.BackgroundErrorReason

actual typealias WriteStallCondition = org.rocksdb.WriteStallCondition

actual class WriteStallInfo internal constructor(
    internal val delegate: org.rocksdb.WriteStallInfo
) {
    actual fun columnFamilyName(): String = delegate.columnFamilyName

    actual fun currentCondition(): WriteStallCondition = delegate.currentCondition

    actual fun previousCondition(): WriteStallCondition = delegate.previousCondition
}

actual class MemTableInfo internal constructor(
    internal val delegate: org.rocksdb.MemTableInfo
) {
    actual fun columnFamilyName(): String = delegate.columnFamilyName

    actual fun firstSeqno(): Long = delegate.firstSeqno

    actual fun earliestSeqno(): Long = delegate.earliestSeqno

    actual fun numEntries(): Long = delegate.numEntries

    actual fun numDeletes(): Long = delegate.numDeletes
}
