package maryk.rocksdb

actual class LiveFileMetaData internal constructor(
    private val columnFamilyNameValue: ByteArray,
    private val levelValue: Int,
    fileName: String,
    path: String,
    size: ULong,
    smallestKey: ByteArray,
    largestKey: ByteArray,
) : SstFileMetaData(fileName, path, size, smallestKey, largestKey) {
    actual fun columnFamilyName(): ByteArray = columnFamilyNameValue

    actual fun level(): Int = levelValue
}
