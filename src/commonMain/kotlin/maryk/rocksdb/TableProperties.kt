package maryk.rocksdb

/**
 * Read-only metadata exported for a flushed or ingested SST table.
 */
expect class TableProperties {
    fun dataSize(): Long
    fun indexSize(): Long
    fun indexPartitions(): Long
    fun topLevelIndexSize(): Long
    fun indexKeyIsUserKey(): Long
    fun indexValueIsDeltaEncoded(): Long
    fun filterSize(): Long
    fun rawKeySize(): Long
    fun rawValueSize(): Long
    fun numDataBlocks(): Long
    fun numEntries(): Long
    fun numDeletions(): Long
    fun numMergeOperands(): Long
    fun numRangeDeletions(): Long
    /** Fixed user-key length, or zero when keys are variable length. */
    fun fixedKeyLen(): Long
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
    /** Copied Java-compatible string properties; binary values are not a lossless byte API. */
    fun userCollectedProperties(): Map<String, String>
    fun readableProperties(): Map<String, String>
}
