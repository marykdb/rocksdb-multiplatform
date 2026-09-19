package maryk.rocksdb

/**
 * Database with Transaction support.
 */
expect class OptimisticTransactionDB : RocksDB {
    /**
     * Returns a wrapper around the underlying base DB.
     */
    fun getBaseDB(): RocksDB

    /**
     * Starts a new Transaction.
     *
     * Caller is responsible for calling [Transaction.close] on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction.
     * @return A new [Transaction] instance.
     */
    fun beginTransaction(writeOptions: WriteOptions): Transaction

    /**
     * Starts a new Transaction with specified transaction options.
     *
     * Caller is responsible for calling [Transaction.close] on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction.
     * @param transactionOptions Any options for the transaction.
     * @return A new [Transaction] instance.
     */
    fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: OptimisticTransactionOptions
    ): Transaction

    /**
     * Starts a new Transaction by reusing an existing transaction.
     *
     * This is an optimization to avoid extra allocations when repeatedly creating transactions.
     * Caller is responsible for calling [Transaction.close] on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction.
     * @param oldTransaction The existing [Transaction] to be reused.
     * @return The [oldTransaction] reinitialized as a new transaction.
     */
    fun beginTransaction(writeOptions: WriteOptions, oldTransaction: Transaction): Transaction

    /**
     * Starts a new Transaction with specified transaction options by reusing an existing transaction.
     *
     * This allows for creating a nested transaction with additional configurations,
     * optimizing resource usage by reusing the provided transaction instance.
     * Caller is responsible for calling [Transaction.close] on the returned
     * transaction when it is no longer needed.
     *
     * @param writeOptions Any write options for the transaction.
     * @param transactionOptions Any options for the transaction.
     * @param oldTransaction The existing [Transaction] to be reused.
     * @return The [oldTransaction] reinitialized as a new transaction.
     */
    fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: OptimisticTransactionOptions,
        oldTransaction: Transaction
    ): Transaction
}
