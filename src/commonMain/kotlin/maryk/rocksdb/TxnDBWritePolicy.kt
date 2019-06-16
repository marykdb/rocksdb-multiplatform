package maryk.rocksdb

expect enum class TxnDBWritePolicy {
    /** Write only the committed data. */
    WRITE_COMMITTED,

    /** Write data after the prepare phase of 2pc. */
    WRITE_PREPARED,

    /** Write data before the prepare phase of 2pc. */
    WRITE_UNPREPARED;

    /**
     * Returns the byte value of the enumerations value.
     */
    fun getValue(): Byte
}
