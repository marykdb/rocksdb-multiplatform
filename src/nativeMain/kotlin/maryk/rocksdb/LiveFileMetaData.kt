package maryk.rocksdb

actual class LiveFileMetaData internal constructor(
    private val columnFamilyNameValue: ByteArray,
    private val levelValue: Int,
    fileName: String,
    path: String,
    size: ULong,
    smallestKey: ByteArray,
    largestKey: ByteArray,
    smallestSeqno: ULong,
    largestSeqno: ULong,
    numReadsSampled: ULong,
    beingCompacted: Boolean,
    numEntries: ULong,
    numDeletions: ULong,
    fileChecksum: ByteArray,
) : SstFileMetaData(fileName, path, size, smallestKey, largestKey,
    smallestSeqno, largestSeqno, numReadsSampled, beingCompacted, numEntries, numDeletions, fileChecksum) {
    actual fun columnFamilyName(): ByteArray = columnFamilyNameValue

    actual fun level(): Int = levelValue
}
