package maryk.rocksdb

import maryk.toCheckedLong

actual open class SstFileMetaData protected constructor() {
    private lateinit var fileNameValue: String
    private lateinit var pathValue: String
    private var sizeValue: ULong = 0u
    private lateinit var smallestKeyValue: ByteArray
    private lateinit var largestKeyValue: ByteArray
    private var smallestSeqnoValue: ULong = 0u
    private var largestSeqnoValue: ULong = 0u
    private var numReadsSampledValue: ULong = 0u
    private var beingCompactedValue: Boolean = false
    private var numEntriesValue: ULong = 0u
    private var numDeletionsValue: ULong = 0u
    private var fileChecksumValue: ByteArray = byteArrayOf()

    internal constructor(
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
    ) : this() {
        fileNameValue = fileName
        pathValue = path
        sizeValue = size
        smallestKeyValue = smallestKey
        largestKeyValue = largestKey
        smallestSeqnoValue = smallestSeqno
        largestSeqnoValue = largestSeqno
        numReadsSampledValue = numReadsSampled
        beingCompactedValue = beingCompacted
        numEntriesValue = numEntries
        numDeletionsValue = numDeletions
        fileChecksumValue = fileChecksum
    }

    actual fun fileName(): String = fileNameValue

    actual fun path(): String = pathValue

    actual fun size(): Long = sizeValue.toCheckedLong("SST file metadata size")

    actual fun smallestKey(): ByteArray = smallestKeyValue

    actual fun largestKey(): ByteArray = largestKeyValue
    actual fun smallestSeqno(): Long = smallestSeqnoValue.toCheckedLong("smallest SST sequence number")
    actual fun largestSeqno(): Long = largestSeqnoValue.toCheckedLong("largest SST sequence number")
    actual fun numReadsSampled(): Long = numReadsSampledValue.toCheckedLong("SST sampled read count")
    actual fun beingCompacted(): Boolean = beingCompactedValue
    actual fun numEntries(): Long = numEntriesValue.toCheckedLong("SST entry count")
    actual fun numDeletions(): Long = numDeletionsValue.toCheckedLong("SST deletion count")
    actual fun fileChecksum(): ByteArray = fileChecksumValue.copyOf()
}
