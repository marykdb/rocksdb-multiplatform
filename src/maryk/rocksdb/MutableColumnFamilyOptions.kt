package maryk.rocksdb

/** Mutable column family options entry point. */
expect class MutableColumnFamilyOptions : AbstractMutableOptions

expect fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder

expect fun parseMutableColumnFamilyOptions(
    str: String,
    ignoreUnknown: Boolean = false
): MutableColumnFamilyOptionsBuilder

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
}
