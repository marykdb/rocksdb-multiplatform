package maryk.rocksdb

import rocksdb.RocksDBPlainTableOptions

actual class PlainTableConfig internal constructor(
    val native: RocksDBPlainTableOptions
) : TableFormatConfig() {
    actual constructor() : this(RocksDBPlainTableOptions())

    actual fun setKeySize(keySize: Int): PlainTableConfig {
        native.userKeyLen = keySize.toUInt()
        return this
    }

    actual fun keySize(): Int {
        return native.userKeyLen.toInt()
    }

    actual fun setBloomBitsPerKey(bitsPerKey: Int): PlainTableConfig {
        native.bloomBitsPerKey = bitsPerKey
        return this
    }

    actual fun bloomBitsPerKey(): Int {
        return native.bloomBitsPerKey
    }

    actual fun setHashTableRatio(ratio: Double): PlainTableConfig {
        native.hashTableRatio = ratio
        return this
    }

    actual fun hashTableRatio(): Double {
        return native.hashTableRatio
    }

    actual fun setIndexSparseness(sparseness: Int): PlainTableConfig {
        native.indexSparseness = sparseness.toULong()
        return this
    }

    actual fun indexSparseness(): Long {
        return native.indexSparseness.toLong()
    }

    actual fun setHugePageTlbSize(hugePageTlbSize: Int): PlainTableConfig {
        native.hugePageTlbSize = hugePageTlbSize.toULong()
        return this
    }

    actual fun hugePageTlbSize(): Int {
        return native.hugePageTlbSize.toInt()
    }

    actual fun setEncodingType(encodingType: EncodingType): PlainTableConfig {
        native.encodingType = encodingType.value
        return this
    }

    actual fun encodingType(): EncodingType? {
        return toEncodingType(native.encodingType)
    }

    actual fun setFullScanMode(fullScanMode: Boolean): PlainTableConfig {
        native.fullScanMode = fullScanMode
        return this
    }

    actual fun fullScanMode(): Boolean {
        return native.fullScanMode
    }

    actual fun setStoreIndexInFile(storeIndexInFile: Boolean): PlainTableConfig {
        native.storeIndexInFile = storeIndexInFile
        return this
    }

    actual fun storeIndexInFile(): Boolean {
        return native.storeIndexInFile
    }
}
