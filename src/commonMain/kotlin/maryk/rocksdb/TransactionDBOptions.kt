package maryk.rocksdb

expect class TransactionDBOptions() : RocksObject {
    /**
     * Specifies the maximum number of keys that can be locked at the same time
     * per column family.
     *
     * If the number of locked keys is greater than [.getMaxNumLocks],
     * transaction writes (or GetForUpdate) will return an error.
     *
     * @return The maximum number of keys that can be locked
     */
    fun getMaxNumLocks(): Long

    /**
     * Specifies the maximum number of keys that can be locked at the same time
     * per column family.
     *
     * If the number of locked keys is greater than [.getMaxNumLocks],
     * transaction writes (or GetForUpdate) will return an error.
     *
     * @param maxNumLocks The maximum number of keys that can be locked;
     * If this value is not positive, no limit will be enforced.
     *
     * @return this TransactionDBOptions instance
     */
    fun setMaxNumLocks(maxNumLocks: Long): TransactionDBOptions

    /**
     * The number of sub-tables per lock table (per column family)
     *
     * @return The number of sub-tables
     */
    fun getNumStripes(): Long

    /**
     * Increasing this value will increase the concurrency by dividing the lock
     * table (per column family) into more sub-tables, each with their own
     * separate mutex.
     *
     * Default: 16
     *
     * @param numStripes The number of sub-tables
     *
     * @return this TransactionDBOptions instance
     */
    fun setNumStripes(numStripes: Long): TransactionDBOptions

    /**
     * The default wait timeout in milliseconds when
     * a transaction attempts to lock a key if not specified by
     * [TransactionOptions.setLockTimeout]
     *
     * If 0, no waiting is done if a lock cannot instantly be acquired.
     * If negative, there is no timeout.
     *
     * @return the default wait timeout in milliseconds
     */
    fun getTransactionLockTimeout(): Long

    /**
     * If positive, specifies the default wait timeout in milliseconds when
     * a transaction attempts to lock a key if not specified by
     * [TransactionOptions.setLockTimeout]
     *
     * If 0, no waiting is done if a lock cannot instantly be acquired.
     * If negative, there is no timeout. Not using a timeout is not recommended
     * as it can lead to deadlocks.  Currently, there is no deadlock-detection to
     * recover from a deadlock.
     *
     * Default: 1000
     *
     * @param transactionLockTimeout the default wait timeout in milliseconds
     *
     * @return this TransactionDBOptions instance
     */
    fun setTransactionLockTimeout(
        transactionLockTimeout: Long
    ): TransactionDBOptions

    /**
     * The wait timeout in milliseconds when writing a key
     * OUTSIDE of a transaction (ie by calling [RocksDB.put],
     * [RocksDB.merge], [RocksDB.remove] or [RocksDB.write]
     * directly).
     *
     * If 0, no waiting is done if a lock cannot instantly be acquired.
     * If negative, there is no timeout and will block indefinitely when acquiring
     * a lock.
     *
     * @return the timeout in milliseconds when writing a key OUTSIDE of a
     * transaction
     */
    fun getDefaultLockTimeout(): Long

    /**
     * If positive, specifies the wait timeout in milliseconds when writing a key
     * OUTSIDE of a transaction (ie by calling [RocksDB.put],
     * [RocksDB.merge], [RocksDB.remove] or [RocksDB.write]
     * directly).
     *
     * If 0, no waiting is done if a lock cannot instantly be acquired.
     * If negative, there is no timeout and will block indefinitely when acquiring
     * a lock.
     *
     * Not using a timeout can lead to deadlocks. Currently, there
     * is no deadlock-detection to recover from a deadlock.  While DB writes
     * cannot deadlock with other DB writes, they can deadlock with a transaction.
     * A negative timeout should only be used if all transactions have a small
     * expiration set.
     *
     * Default: 1000
     *
     * @param defaultLockTimeout the timeout in milliseconds when writing a key
     * OUTSIDE of a transaction
     * @return this TransactionDBOptions instance
     */
    fun setDefaultLockTimeout(
        defaultLockTimeout: Long
    ): TransactionDBOptions

    /**
     * The policy for when to write the data into the DB. The default policy is to
     * write only the committed data [TxnDBWritePolicy.WRITE_COMMITTED].
     * The data could be written before the commit phase. The DB then needs to
     * provide the mechanisms to tell apart committed from uncommitted data.
     *
     * @return The write policy.
     */
    fun getWritePolicy(): TxnDBWritePolicy

    /**
     * The policy for when to write the data into the DB. The default policy is to
     * write only the committed data [TxnDBWritePolicy.WRITE_COMMITTED].
     * The data could be written before the commit phase. The DB then needs to
     * provide the mechanisms to tell apart committed from uncommitted data.
     *
     * @param writePolicy The write policy.
     *
     * @return this TransactionDBOptions instance
     */
    fun setWritePolicy(
        writePolicy: TxnDBWritePolicy
    ): TransactionDBOptions
}
