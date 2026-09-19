package maryk.rocksdb

/**
 * Read-only metadata exported for a flushed or ingested SST table.
 */
expect class TableProperties {
    fun dataSize(): Long
    fun indexSize(): Long
    fun filterSize(): Long
    fun rawKeySize(): Long
    fun rawValueSize(): Long
    fun numDataBlocks(): Long
    fun numEntries(): Long
    fun numDeletions(): Long
    fun numMergeOperands(): Long
    fun numRangeDeletions(): Long
    fun formatVersion(): Long
    fun columnFamilyId(): Long
    fun columnFamilyName(): String?
    fun creationTime(): Long
    fun oldestKeyTime(): Long
    fun slowCompressionEstimatedDataSize(): Long
    fun fastCompressionEstimatedDataSize(): Long
    fun filterPolicyName(): String?
    fun comparatorName(): String?
    fun mergeOperatorName(): String?
    fun prefixExtractorName(): String?
    fun propertyCollectorsNames(): String?
    fun compressionName(): String?
}
