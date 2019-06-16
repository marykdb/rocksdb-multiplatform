package maryk.rocksdb

/** Database with Transaction support. */
expect class OptimisticTransactionDB : RocksDB {
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
        optimisticTransactionOptions: OptimisticTransactionOptions
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
        optimisticTransactionOptions: OptimisticTransactionOptions,
        oldTransaction: Transaction
    ): Transaction

    /**
     * Get the underlying database that was opened.
     *
     * @return The underlying database that was opened.
     */
    fun getBaseDB(): RocksDB
}
