package maryk.rocksdb

/**
 * BatchResult represents a data structure returned
 * by a TransactionLogIterator containing a sequence
 * number and a [WriteBatch] instance.
 */
expect class BatchResult
/**
 * Constructor of BatchResult class.
 *
 * @param sequenceNumber related to this BatchResult instance.
 * @param nativeHandle to [WriteBatch]
 * native instance.
 */
constructor(
    sequenceNumber: Long,
    nativeHandle: Long
) {
    /**
     * Return sequence number related to this BatchResult.
     * @return Sequence number.
     */
    fun sequenceNumber(): Long

    /**
     * Return contained [WriteBatch]
     * instance
     * @return [WriteBatch] instance.
     */
    fun writeBatch(): WriteBatch
}
