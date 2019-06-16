package maryk.rocksdb

expect class TransactionOptions() : RocksObject {
    fun isSetSnapshot(): Boolean

    fun setSetSnapshot(setSnapshot: Boolean): TransactionOptions

    /**
     * True means that before acquiring locks, this transaction will
     * check if doing so will cause a deadlock. If so, it will return with
     * [Status.Code.Busy]. The user should retry their transaction.
     *
     * @return true if a deadlock is detected.
     */
    fun isDeadlockDetect(): Boolean

    /**
     * Setting to true means that before acquiring locks, this transaction will
     * check if doing so will cause a deadlock. If so, it will return with
     * [Status.Code.Busy]. The user should retry their transaction.
     *
     * @param deadlockDetect true if we should detect deadlocks.
     *
     * @return this TransactionOptions instance
     */
    fun setDeadlockDetect(deadlockDetect: Boolean): TransactionOptions

    /**
     * The wait timeout in milliseconds when a transaction attempts to lock a key.
     *
     * If 0, no waiting is done if a lock cannot instantly be acquired.
     * If negative, [TransactionDBOptions.getTransactionLockTimeout]
     * will be used
     *
     * @return the lock timeout in milliseconds
     */
    fun getLockTimeout(): Long

    /**
     * If positive, specifies the wait timeout in milliseconds when
     * a transaction attempts to lock a key.
     *
     * If 0, no waiting is done if a lock cannot instantly be acquired.
     * If negative, [TransactionDBOptions.getTransactionLockTimeout]
     * will be used
     *
     * Default: -1
     *
     * @param lockTimeout the lock timeout in milliseconds
     *
     * @return this TransactionOptions instance
     */
    fun setLockTimeout(lockTimeout: Long): TransactionOptions

    /**
     * Expiration duration in milliseconds.
     *
     * If non-negative, transactions that last longer than this many milliseconds
     * will fail to commit. If not set, a forgotten transaction that is never
     * committed, rolled back, or deleted will never relinquish any locks it
     * holds. This could prevent keys from being written by other writers.
     *
     * @return expiration the expiration duration in milliseconds
     */
    fun getExpiration(): Long

    /**
     * Expiration duration in milliseconds.
     *
     * If non-negative, transactions that last longer than this many milliseconds
     * will fail to commit. If not set, a forgotten transaction that is never
     * committed, rolled back, or deleted will never relinquish any locks it
     * holds. This could prevent keys from being written by other writers.
     *
     * Default: -1
     *
     * @param expiration the expiration duration in milliseconds
     *
     * @return this TransactionOptions instance
     */
    fun setExpiration(expiration: Long): TransactionOptions

    /**
     * Gets the number of traversals to make during deadlock detection.
     *
     * @return the number of traversals to make during
     * deadlock detection
     */
    fun getDeadlockDetectDepth(): Long

    /**
     * Sets the number of traversals to make during deadlock detection.
     *
     * Default: 50
     *
     * @param deadlockDetectDepth the number of traversals to make during
     * deadlock detection
     *
     * @return this TransactionOptions instance
     */
    fun setDeadlockDetectDepth(
        deadlockDetectDepth: Long
    ): TransactionOptions

    /**
     * Get the maximum number of bytes that may be used for the write batch.
     *
     * @return the maximum number of bytes, 0 means no limit.
     */
    fun getMaxWriteBatchSize(): Long

    /**
     * Set the maximum number of bytes that may be used for the write batch.
     *
     * @param maxWriteBatchSize the maximum number of bytes, 0 means no limit.
     *
     * @return this TransactionOptions instance
     */
    fun setMaxWriteBatchSize(maxWriteBatchSize: Long): TransactionOptions
}
