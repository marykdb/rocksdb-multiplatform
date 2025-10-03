package maryk.rocksdb

/**
 * Configuration wrapper for plain table factories.
 */
expect class PlainTableConfig() : TableFormatConfig {
    fun keySize(): Int
    fun setKeySize(keySize: Int): PlainTableConfig

    fun bloomBitsPerKey(): Int
    fun setBloomBitsPerKey(bloomBitsPerKey: Int): PlainTableConfig

    fun hashTableRatio(): Double
    fun setHashTableRatio(hashTableRatio: Double): PlainTableConfig

    fun indexSparseness(): Long
    fun setIndexSparseness(indexSparseness: Int): PlainTableConfig

    fun hugePageTlbSize(): Int
    fun setHugePageTlbSize(hugePageTlbSize: Int): PlainTableConfig

    fun encodingType(): EncodingType
    fun setEncodingType(encodingType: EncodingType): PlainTableConfig

    fun fullScanMode(): Boolean
    fun setFullScanMode(fullScanMode: Boolean): PlainTableConfig

    fun storeIndexInFile(): Boolean
    fun setStoreIndexInFile(storeIndexInFile: Boolean): PlainTableConfig
}
