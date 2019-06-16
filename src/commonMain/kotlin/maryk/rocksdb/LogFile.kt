package maryk.rocksdb

expect class LogFile {
    /**
     * Returns log file's pathname relative to the main db dir
     * Eg. For a live-log-file = /000003.log
     * For an archived-log-file = /archive/000003.log
     *
     * @return log file's pathname
     */
    fun pathName(): String

    /**
     * Primary identifier for log file.
     * This is directly proportional to creation time of the log file
     *
     * @return the log number
     */
    fun logNumber(): Long

    /**
     * Log file can be either alive or archived.
     * @return the type of the log file.
     */
    fun type(): WalFileType

    /** Starting sequence number of writebatch written in this log file. */
    fun startSequence(): Long

    /** Size of log file on disk in Bytes. */
    fun sizeFileBytes(): Long
}
