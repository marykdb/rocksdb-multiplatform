package maryk.rocksdb

expect open class SstFileMetaData {
    /** Get the name of the file. */
    fun fileName(): String

    /** Get the full path where the file locates. */
    fun path(): String

    /** Get the file size in bytes. */
    fun size(): Long

    /** Get the smallest user defined key in the file. */
    fun smallestKey(): ByteArray

    /** Get the largest user defined key in the file. */
    fun largestKey(): ByteArray
}
