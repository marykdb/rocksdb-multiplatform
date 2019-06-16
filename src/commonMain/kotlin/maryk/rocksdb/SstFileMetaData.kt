package maryk.rocksdb

expect class SstFileMetaData {
    /** Get the name of the file. */
    fun fileName(): String

    /** Get the full path where the file locates. */
    fun path(): String

    /** Get the file size in bytes. */
    fun size(): Long

    /** Get the smallest sequence number in file. */
    fun smallestSeqno(): Long

    /** Get the largest sequence number in file. */
    fun largestSeqno(): Long

    /** Get the smallest user defined key in the file. */
    fun smallestKey(): ByteArray

    /** Get the largest user defined key in the file. */
    fun largestKey(): ByteArray

    /** Get the number of times the file has been read. */
    fun numReadsSampled(): Long

    /** Returns true if the file is currently being compacted. */
    fun beingCompacted(): Boolean

    /** Get the number of entries. */
    fun numEntries(): Long

    /** Get the number of deletions. */
    fun numDeletions(): Long
}
