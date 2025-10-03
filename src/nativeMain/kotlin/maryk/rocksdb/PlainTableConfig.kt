package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import maryk.toUByte
import rocksdb.rocksdb_options_set_plain_table_factory

actual class PlainTableConfig actual constructor() : TableFormatConfig() {
    private var keySize: Int = 0
    private var bloomBitsPerKey: Int = 10
    private var hashTableRatio: Double = 0.75
    private var indexSparseness: Long = 16
    private var hugePageTlbSize: Int = 0
    private var encodingType: EncodingType = EncodingType.kPlain
    private var fullScanMode: Boolean = false
    private var storeIndexInFile: Boolean = false

    actual fun keySize(): Int = keySize

    actual fun setKeySize(keySize: Int): PlainTableConfig {
        this.keySize = keySize
        return this
    }

    actual fun bloomBitsPerKey(): Int = bloomBitsPerKey

    actual fun setBloomBitsPerKey(bloomBitsPerKey: Int): PlainTableConfig {
        this.bloomBitsPerKey = bloomBitsPerKey
        return this
    }

    actual fun hashTableRatio(): Double = hashTableRatio

    actual fun setHashTableRatio(hashTableRatio: Double): PlainTableConfig {
        this.hashTableRatio = hashTableRatio
        return this
    }

    actual fun indexSparseness(): Long = indexSparseness

    actual fun setIndexSparseness(indexSparseness: Int): PlainTableConfig {
        this.indexSparseness = indexSparseness.toLong()
        return this
    }

    actual fun hugePageTlbSize(): Int = hugePageTlbSize

    actual fun setHugePageTlbSize(hugePageTlbSize: Int): PlainTableConfig {
        this.hugePageTlbSize = hugePageTlbSize
        return this
    }

    actual fun encodingType(): EncodingType = encodingType

    actual fun setEncodingType(encodingType: EncodingType): PlainTableConfig {
        this.encodingType = encodingType
        return this
    }

    actual fun fullScanMode(): Boolean = fullScanMode

    actual fun setFullScanMode(fullScanMode: Boolean): PlainTableConfig {
        this.fullScanMode = fullScanMode
        return this
    }

    actual fun storeIndexInFile(): Boolean = storeIndexInFile

    actual fun setStoreIndexInFile(storeIndexInFile: Boolean): PlainTableConfig {
        this.storeIndexInFile = storeIndexInFile
        return this
    }

    internal fun applyToOptions(options: CPointer<rocksdb_options_t>) {
        rocksdb_options_set_plain_table_factory(
            options,
            keySize.toUInt(),
            bloomBitsPerKey,
            hashTableRatio,
            indexSparseness.toULong(),
            hugePageTlbSize.toULong(),
            encodingType.value,
            fullScanMode.toUByte(),
            storeIndexInFile.toUByte()
        )
    }
}
