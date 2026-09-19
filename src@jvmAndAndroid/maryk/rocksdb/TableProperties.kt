package maryk.rocksdb

actual class TableProperties internal constructor(
    internal val delegate: org.rocksdb.TableProperties
) {
    actual fun dataSize(): Long = delegate.dataSize
    actual fun indexSize(): Long = delegate.indexSize
    actual fun filterSize(): Long = delegate.filterSize
    actual fun rawKeySize(): Long = delegate.rawKeySize
    actual fun rawValueSize(): Long = delegate.rawValueSize
    actual fun numDataBlocks(): Long = delegate.numDataBlocks
    actual fun numEntries(): Long = delegate.numEntries
    actual fun numDeletions(): Long = delegate.numDeletions
    actual fun numMergeOperands(): Long = delegate.numMergeOperands
    actual fun numRangeDeletions(): Long = delegate.numRangeDeletions
    actual fun formatVersion(): Long = delegate.formatVersion
    actual fun columnFamilyId(): Long = delegate.columnFamilyId
    actual fun columnFamilyName(): String? =
        delegate.columnFamilyName?.let { String(it) }
    actual fun creationTime(): Long = delegate.creationTime
    actual fun oldestKeyTime(): Long = delegate.oldestKeyTime
    actual fun slowCompressionEstimatedDataSize(): Long =
        delegate.slowCompressionEstimatedDataSize
    actual fun fastCompressionEstimatedDataSize(): Long =
        delegate.fastCompressionEstimatedDataSize
    actual fun filterPolicyName(): String? = delegate.filterPolicyName
    actual fun comparatorName(): String? = delegate.comparatorName
    actual fun mergeOperatorName(): String? = delegate.mergeOperatorName
    actual fun prefixExtractorName(): String? = delegate.prefixExtractorName
    actual fun propertyCollectorsNames(): String? = delegate.propertyCollectorsNames
    actual fun compressionName(): String? = delegate.compressionName
}
