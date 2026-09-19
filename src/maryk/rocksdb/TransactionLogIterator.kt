package maryk.rocksdb

/**
 * Streams batches from RocksDB's write-ahead log.
 */
expect class TransactionLogIterator : RocksObject {
    /**
     * @return true when the iterator currently points at a valid batch.
     */
    fun isValid(): Boolean

    /**
     * Advances the iterator to the next WAL batch.
     */
    fun next()

    /**
     * Throws a [RocksDBException] if the iterator encountered an error.
     */
    @Throws(RocksDBException::class)
    fun status()

    /**
     * Returns the current batch.
     */
    fun getBatch(): TransactionLogBatchResult
}

/**
 * Result containing the sequence number and write batch for a WAL entry.
 */
expect class TransactionLogBatchResult {
    fun sequenceNumber(): Long

    fun writeBatch(): WriteBatch
}
