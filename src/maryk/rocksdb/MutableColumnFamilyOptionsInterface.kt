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
}
