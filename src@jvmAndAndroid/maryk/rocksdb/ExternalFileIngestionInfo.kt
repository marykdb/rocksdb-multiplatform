package maryk.rocksdb

actual class ExternalFileIngestionInfo internal constructor(
    internal val delegate: org.rocksdb.ExternalFileIngestionInfo
) {
    actual fun columnFamilyName(): String = delegate.columnFamilyName

    actual fun externalFilePath(): String = delegate.externalFilePath

    actual fun internalFilePath(): String = delegate.internalFilePath

    actual fun globalSequenceNumber(): Long = delegate.globalSeqno

    actual fun tableProperties(): TableProperties? =
        delegate.tableProperties?.let(::TableProperties)
}
