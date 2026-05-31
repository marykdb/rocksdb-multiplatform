package maryk.rocksdb

import maryk.toCheckedLong

actual open class SstFileMetaData protected constructor() {
    private lateinit var fileNameValue: String
    private lateinit var pathValue: String
    private var sizeValue: ULong = 0u
    private lateinit var smallestKeyValue: ByteArray
    private lateinit var largestKeyValue: ByteArray

    internal constructor(
        fileName: String,
        path: String,
        size: ULong,
        smallestKey: ByteArray,
        largestKey: ByteArray,
    ) : this() {
        fileNameValue = fileName
        pathValue = path
        sizeValue = size
        smallestKeyValue = smallestKey
        largestKeyValue = largestKey
    }

    actual fun fileName(): String = fileNameValue

    actual fun path(): String = pathValue

    actual fun size(): Long = sizeValue.toCheckedLong("SST file metadata size")

    actual fun smallestKey(): ByteArray = smallestKeyValue

    actual fun largestKey(): ByteArray = largestKeyValue
}
