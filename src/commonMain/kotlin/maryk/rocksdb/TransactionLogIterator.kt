package maryk.rocksdb

/**
 * A TransactionLogIterator is used to iterate over the transactions in a db.
 * One run of the iterator is continuous, i.e. the iterator will stop at the
 * beginning of any gap in sequences.
 */
expect class TransactionLogIterator : RocksObject {
    /**
     * An iterator is either positioned at a WriteBatch
     * or not valid. This method returns true if the iterator
     * is valid. Can read data from a valid iterator.
     *
     * @return true if iterator position is valid.
     */
    fun isValid(): Boolean

    /**
     * If iterator position is valid, return the current
     * write_batch and the sequence number of the earliest
     * transaction contained in the batch.
     *
     * ONLY use if Valid() is true and status() is OK.
     *
     * @return [maryk.rocksdb.TransactionLogIterator.BatchResult]
     * instance.
     */
    fun getBatch(): BatchResult

    /**
     * Moves the iterator to the next WriteBatch.
     * **REQUIRES**: Valid() to be true.
     */
    operator fun next()

    /**
     *
     * Throws RocksDBException if something went wrong.
     *
     * @throws org.rocksdb.RocksDBException if something went
     * wrong in the underlying C++ code.
     */
    fun status()
}

