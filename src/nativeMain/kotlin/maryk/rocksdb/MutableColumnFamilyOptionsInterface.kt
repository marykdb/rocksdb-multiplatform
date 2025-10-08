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
}
