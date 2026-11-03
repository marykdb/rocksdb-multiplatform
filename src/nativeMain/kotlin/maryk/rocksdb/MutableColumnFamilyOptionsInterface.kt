package maryk.rocksdb

actual interface MutableColumnFamilyOptionsInterface<T : MutableColumnFamilyOptionsInterface<T>> {
    actual fun setWriteBufferSize(writeBufferSize: Long): T
    actual fun writeBufferSize(): Long
    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): T
    actual fun disableAutoCompactions(): Boolean
    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): T
    actual fun level0FileNumCompactionTrigger(): Int
    actual fun setMaxCompactionBytes(maxCompactionBytes: Long): T
    actual fun maxCompactionBytes(): Long
    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): T
    actual fun maxBytesForLevelBase(): Long
    actual fun setCompressionType(compressionType: CompressionType): T
    actual fun compressionType(): CompressionType

    actual fun setMaxWriteBufferNumber(value: Int): T
    actual fun maxWriteBufferNumber(): Int
    actual fun setArenaBlockSize(value: Long): T
    actual fun arenaBlockSize(): Long
    actual fun setMemtablePrefixBloomSizeRatio(value: Double): T
    actual fun memtablePrefixBloomSizeRatio(): Double
    actual fun setMemtableWholeKeyFiltering(value: Boolean): T
    actual fun memtableWholeKeyFiltering(): Boolean
    actual fun setMemtableHugePageSize(value: Long): T
    actual fun memtableHugePageSize(): Long
    actual fun setMaxSuccessiveMerges(value: Long): T
    actual fun maxSuccessiveMerges(): Long
    actual fun setMaxSequentialSkipInIterations(value: Long): T
    actual fun maxSequentialSkipInIterations(): Long
    actual fun setInplaceUpdateNumLocks(value: Long): T
    actual fun inplaceUpdateNumLocks(): Long
    actual fun setLevel0SlowdownWritesTrigger(value: Int): T
    actual fun level0SlowdownWritesTrigger(): Int
    actual fun setLevel0StopWritesTrigger(value: Int): T
    actual fun level0StopWritesTrigger(): Int
    actual fun setTargetFileSizeBase(value: Long): T
    actual fun targetFileSizeBase(): Long
    actual fun setTargetFileSizeMultiplier(value: Int): T
    actual fun targetFileSizeMultiplier(): Int
    actual fun setSoftPendingCompactionBytesLimit(value: Long): T
    actual fun softPendingCompactionBytesLimit(): Long
    actual fun setHardPendingCompactionBytesLimit(value: Long): T
    actual fun hardPendingCompactionBytesLimit(): Long
    actual fun setTtl(value: Long): T
    actual fun ttl(): Long
    actual fun setPeriodicCompactionSeconds(value: Long): T
    actual fun periodicCompactionSeconds(): Long
    actual fun setReportBgIoStats(value: Boolean): T
    actual fun reportBgIoStats(): Boolean
    actual fun setParanoidFileChecks(value: Boolean): T
    actual fun paranoidFileChecks(): Boolean
    actual fun setEnableBlobFiles(value: Boolean): T
    actual fun enableBlobFiles(): Boolean
    actual fun setMinBlobSize(value: Long): T
    actual fun minBlobSize(): Long
    actual fun setBlobFileSize(value: Long): T
    actual fun blobFileSize(): Long
    actual fun setEnableBlobGarbageCollection(value: Boolean): T
    actual fun enableBlobGarbageCollection(): Boolean
    actual fun setBlobGarbageCollectionAgeCutoff(value: Double): T
    actual fun blobGarbageCollectionAgeCutoff(): Double
    actual fun setBlobGarbageCollectionForceThreshold(value: Double): T
    actual fun blobGarbageCollectionForceThreshold(): Double
    actual fun setBlobCompactionReadaheadSize(value: Long): T
    actual fun blobCompactionReadaheadSize(): Long
    actual fun setBlobFileStartingLevel(value: Int): T
    actual fun blobFileStartingLevel(): Int
    actual fun setExperimentalMempurgeThreshold(value: Double): T
    actual fun experimentalMempurgeThreshold(): Double
    actual fun setMinTombstonesForRangeConversion(value: Int): T
    actual fun minTombstonesForRangeConversion(): Int
    actual fun setReadTriggeredCompactionThreshold(value: Double): T
    actual fun readTriggeredCompactionThreshold(): Double

}
