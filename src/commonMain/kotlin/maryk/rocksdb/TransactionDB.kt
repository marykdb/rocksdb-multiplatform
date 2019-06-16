package maryk.rocksdb

/** Database with Transaction support */
expect class TransactionDB : RocksDB {
    /**
     * Starts a new Transaction.
     *
     * Caller is responsible for calling {@link #close()} on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction
     * @return a new transaction
     */
    fun beginTransaction(writeOptions: WriteOptions): Transaction

    /**
     * Starts a new Transaction.
     *
     * Caller is responsible for calling {@link #close()} on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction
     * @param transactionOptions Any options for the transaction
     * @return a new transaction
     */
    fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions
    ): Transaction

    /**
     * Starts a new Transaction.
     *
     * Caller is responsible for calling {@link #close()} on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction
     * @param oldTransaction this Transaction will be reused instead of allocating
     *     a new one. This is an optimization to avoid extra allocations
     *     when repeatedly creating transactions.
     * @return The oldTransaction which has been reinitialized as a new
     *     transaction
     */
    fun beginTransaction(
        writeOptions: WriteOptions,
        oldTransaction: Transaction
    ): Transaction

    /**
     * Starts a new Transaction.
     *
     * Caller is responsible for calling {@link #close()} on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction
     * @param transactionOptions Any options for the transaction
     * @param oldTransaction this Transaction will be reused instead of allocating
     *     a new one. This is an optimization to avoid extra allocations
     *     when repeatedly creating transactions.
     * @return The oldTransaction which has been reinitialized as a new
     *     transaction
     */
    fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions,
        oldTransaction: Transaction
    ): Transaction

    fun getTransactionByName(transactionName: String): Transaction?

    fun getAllPreparedTransactions(): List<Transaction>

    /**
     * Returns map of all locks held.
     *
     * @return a map of all the locks held.
     */
    fun getLockStatusData(): Map<Long, KeyLockInfo>

    fun getDeadlockInfoBuffer(): Array<DeadlockPath>

    fun setDeadlockInfoBufferSize(targetSize: Int)
}

expect class KeyLockInfo(
    key: String,
    transactionIDs: LongArray,
    exclusive: Boolean
) {
    /** Get the key. */
    fun getKey(): String

    /** Get the Transaction IDs. */
    fun getTransactionIDs(): LongArray

    /** Get the Lock status. */
    fun isExclusive(): Boolean
}

expect class DeadlockInfo {
    /**
     * Get the Transaction ID.
     * @return the transaction ID
     */
    fun getTransactionID(): Long

    /**
     * Get the Column Family ID.
     *
     * @return The column family ID
     */
    fun getColumnFamilyId(): Long

    /**
     * Get the key that we are waiting on.
     * @return the key that we are waiting on
     */
    fun getWaitingKey(): String

    /**
     * Get the Lock status.
     * @return true if the lock is exclusive, false if the lock is shared.
     */
    fun isExclusive(): Boolean
}

expect class DeadlockPath(
    path: Array<DeadlockInfo>,
    limitExceeded: Boolean
) {
    fun isEmpty(): Boolean
}
