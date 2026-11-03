package maryk.rocksdb

/** Mutable column family options entry point. */
expect class MutableColumnFamilyOptions : AbstractMutableOptions

expect fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder

expect fun parseMutableColumnFamilyOptions(
    str: String,
    ignoreUnknown: Boolean = false
): MutableColumnFamilyOptionsBuilder

/** Query values after explicitly setting or parsing them; an unset value has no portable getter default. */
expect class MutableColumnFamilyOptionsBuilder :
    MutableColumnFamilyOptionsInterface<MutableColumnFamilyOptionsBuilder> {
    /** Builds the immutable options object. */
    fun build(): MutableColumnFamilyOptions

    override fun setWriteBufferSize(writeBufferSize: Long): MutableColumnFamilyOptionsBuilder
    override fun writeBufferSize(): Long
    override fun setDisableAutoCompactions(disableAutoCompactions: Boolean): MutableColumnFamilyOptionsBuilder
    override fun disableAutoCompactions(): Boolean
    override fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): MutableColumnFamilyOptionsBuilder
    override fun level0FileNumCompactionTrigger(): Int
    override fun setMaxCompactionBytes(maxCompactionBytes: Long): MutableColumnFamilyOptionsBuilder
    override fun maxCompactionBytes(): Long
    override fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): MutableColumnFamilyOptionsBuilder
    override fun maxBytesForLevelBase(): Long
    override fun setCompressionType(compressionType: CompressionType): MutableColumnFamilyOptionsBuilder
    override fun compressionType(): CompressionType

    override fun setMaxWriteBufferNumber(value: Int): MutableColumnFamilyOptionsBuilder
    override fun maxWriteBufferNumber(): Int
    override fun setArenaBlockSize(value: Long): MutableColumnFamilyOptionsBuilder
    override fun arenaBlockSize(): Long
    override fun setMemtablePrefixBloomSizeRatio(value: Double): MutableColumnFamilyOptionsBuilder
    override fun memtablePrefixBloomSizeRatio(): Double
    override fun setMemtableWholeKeyFiltering(value: Boolean): MutableColumnFamilyOptionsBuilder
    override fun memtableWholeKeyFiltering(): Boolean
    override fun setMemtableHugePageSize(value: Long): MutableColumnFamilyOptionsBuilder
    override fun memtableHugePageSize(): Long
    override fun setMaxSuccessiveMerges(value: Long): MutableColumnFamilyOptionsBuilder
    override fun maxSuccessiveMerges(): Long
    override fun setMaxSequentialSkipInIterations(value: Long): MutableColumnFamilyOptionsBuilder
    override fun maxSequentialSkipInIterations(): Long
    override fun setInplaceUpdateNumLocks(value: Long): MutableColumnFamilyOptionsBuilder
    override fun inplaceUpdateNumLocks(): Long
    override fun setLevel0SlowdownWritesTrigger(value: Int): MutableColumnFamilyOptionsBuilder
    override fun level0SlowdownWritesTrigger(): Int
    override fun setLevel0StopWritesTrigger(value: Int): MutableColumnFamilyOptionsBuilder
    override fun level0StopWritesTrigger(): Int
    override fun setTargetFileSizeBase(value: Long): MutableColumnFamilyOptionsBuilder
    override fun targetFileSizeBase(): Long
    override fun setTargetFileSizeMultiplier(value: Int): MutableColumnFamilyOptionsBuilder
    override fun targetFileSizeMultiplier(): Int
    override fun setSoftPendingCompactionBytesLimit(value: Long): MutableColumnFamilyOptionsBuilder
    override fun softPendingCompactionBytesLimit(): Long
    override fun setHardPendingCompactionBytesLimit(value: Long): MutableColumnFamilyOptionsBuilder
    override fun hardPendingCompactionBytesLimit(): Long
    override fun setTtl(value: Long): MutableColumnFamilyOptionsBuilder
    override fun ttl(): Long
    override fun setPeriodicCompactionSeconds(value: Long): MutableColumnFamilyOptionsBuilder
    override fun periodicCompactionSeconds(): Long
    override fun setReportBgIoStats(value: Boolean): MutableColumnFamilyOptionsBuilder
    override fun reportBgIoStats(): Boolean
    override fun setParanoidFileChecks(value: Boolean): MutableColumnFamilyOptionsBuilder
    override fun paranoidFileChecks(): Boolean
    override fun setEnableBlobFiles(value: Boolean): MutableColumnFamilyOptionsBuilder
    override fun enableBlobFiles(): Boolean
    override fun setMinBlobSize(value: Long): MutableColumnFamilyOptionsBuilder
    override fun minBlobSize(): Long
    override fun setBlobFileSize(value: Long): MutableColumnFamilyOptionsBuilder
    override fun blobFileSize(): Long
    override fun setEnableBlobGarbageCollection(value: Boolean): MutableColumnFamilyOptionsBuilder
    override fun enableBlobGarbageCollection(): Boolean
    override fun setBlobGarbageCollectionAgeCutoff(value: Double): MutableColumnFamilyOptionsBuilder
    override fun blobGarbageCollectionAgeCutoff(): Double
    override fun setBlobGarbageCollectionForceThreshold(value: Double): MutableColumnFamilyOptionsBuilder
    override fun blobGarbageCollectionForceThreshold(): Double
    override fun setBlobCompactionReadaheadSize(value: Long): MutableColumnFamilyOptionsBuilder
    override fun blobCompactionReadaheadSize(): Long
    override fun setBlobFileStartingLevel(value: Int): MutableColumnFamilyOptionsBuilder
    override fun blobFileStartingLevel(): Int
    override fun setExperimentalMempurgeThreshold(value: Double): MutableColumnFamilyOptionsBuilder
    override fun experimentalMempurgeThreshold(): Double
    override fun setMinTombstonesForRangeConversion(value: Int): MutableColumnFamilyOptionsBuilder
    override fun minTombstonesForRangeConversion(): Int
    override fun setReadTriggeredCompactionThreshold(value: Double): MutableColumnFamilyOptionsBuilder
    override fun readTriggeredCompactionThreshold(): Double

}
