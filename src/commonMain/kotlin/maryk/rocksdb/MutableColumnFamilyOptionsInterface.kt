package maryk.rocksdb

/**
 * Mutable column family options.
 */
expect interface MutableColumnFamilyOptionsInterface<T : MutableColumnFamilyOptionsInterface<T>> {
    /** Amount of data to build up in memory before converting to a sorted on-disk file. */
    fun setWriteBufferSize(writeBufferSize: Long): T

    /** Size of the write buffer. */
    fun writeBufferSize(): Long

    /** Disable automatic compactions. */
    fun setDisableAutoCompactions(disableAutoCompactions: Boolean): T

    /** Returns whether automatic compactions are disabled. */
    fun disableAutoCompactions(): Boolean

    /** Number of files to trigger level-0 compaction. */
    fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): T

    /** Current number of level-0 files required to trigger compaction. */
    fun level0FileNumCompactionTrigger(): Int

    /** Limit the total number of bytes processed in a single compaction. */
    fun setMaxCompactionBytes(maxCompactionBytes: Long): T

    /** Maximum number of bytes in a single compaction. */
    fun maxCompactionBytes(): Long

    /**
     * Upper bound for the total size of level-1 files. Higher levels are derived
     * from this value based on the level multiplier.
     */
    fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): T

    /** Returns the configured upper bound for the total size of level-1 files. */
    fun maxBytesForLevelBase(): Long

    /** Set the compression algorithm for newly created SST files. */
    fun setCompressionType(compressionType: CompressionType): T

    /** Compression algorithm applied to new SST files. */
    fun compressionType(): CompressionType

    fun setMaxWriteBufferNumber(value: Int): T
    fun maxWriteBufferNumber(): Int
    fun setArenaBlockSize(value: Long): T
    fun arenaBlockSize(): Long
    fun setMemtablePrefixBloomSizeRatio(value: Double): T
    fun memtablePrefixBloomSizeRatio(): Double
    fun setMemtableWholeKeyFiltering(value: Boolean): T
    fun memtableWholeKeyFiltering(): Boolean
    fun setMemtableHugePageSize(value: Long): T
    fun memtableHugePageSize(): Long
    fun setMaxSuccessiveMerges(value: Long): T
    fun maxSuccessiveMerges(): Long
    fun setMaxSequentialSkipInIterations(value: Long): T
    fun maxSequentialSkipInIterations(): Long
    fun setInplaceUpdateNumLocks(value: Long): T
    fun inplaceUpdateNumLocks(): Long
    fun setLevel0SlowdownWritesTrigger(value: Int): T
    fun level0SlowdownWritesTrigger(): Int
    fun setLevel0StopWritesTrigger(value: Int): T
    fun level0StopWritesTrigger(): Int
    fun setTargetFileSizeBase(value: Long): T
    fun targetFileSizeBase(): Long
    fun setTargetFileSizeMultiplier(value: Int): T
    fun targetFileSizeMultiplier(): Int
    fun setSoftPendingCompactionBytesLimit(value: Long): T
    fun softPendingCompactionBytesLimit(): Long
    fun setHardPendingCompactionBytesLimit(value: Long): T
    fun hardPendingCompactionBytesLimit(): Long
    /** SST compaction age in seconds; -2 selects the engine default, 0 disables the trigger. */
    fun setTtl(value: Long): T
    fun ttl(): Long
    /** Periodic-compaction age in seconds; -2 selects the engine default, 0 disables the trigger. */
    fun setPeriodicCompactionSeconds(value: Long): T
    fun periodicCompactionSeconds(): Long
    fun setReportBgIoStats(value: Boolean): T
    fun reportBgIoStats(): Boolean
    fun setParanoidFileChecks(value: Boolean): T
    fun paranoidFileChecks(): Boolean
    fun setEnableBlobFiles(value: Boolean): T
    fun enableBlobFiles(): Boolean
    fun setMinBlobSize(value: Long): T
    fun minBlobSize(): Long
    fun setBlobFileSize(value: Long): T
    fun blobFileSize(): Long
    fun setEnableBlobGarbageCollection(value: Boolean): T
    fun enableBlobGarbageCollection(): Boolean
    fun setBlobGarbageCollectionAgeCutoff(value: Double): T
    fun blobGarbageCollectionAgeCutoff(): Double
    fun setBlobGarbageCollectionForceThreshold(value: Double): T
    fun blobGarbageCollectionForceThreshold(): Double
    fun setBlobCompactionReadaheadSize(value: Long): T
    fun blobCompactionReadaheadSize(): Long
    fun setBlobFileStartingLevel(value: Int): T
    fun blobFileStartingLevel(): Int
    fun setExperimentalMempurgeThreshold(value: Double): T
    fun experimentalMempurgeThreshold(): Double
    fun setMinTombstonesForRangeConversion(value: Int): T
    fun minTombstonesForRangeConversion(): Int
    fun setReadTriggeredCompactionThreshold(value: Double): T
    fun readTriggeredCompactionThreshold(): Double

}
