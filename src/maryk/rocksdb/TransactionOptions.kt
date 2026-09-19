package maryk.rocksdb

/**
 * Represents transaction options for RocksDB.
 *
 * This class provides configuration options for transactions, such as
 * deadlock detection, lock timeouts, expiration durations, and maximum write batch sizes.
 */
expect class TransactionOptions() : RocksObject {

    /**
     * Checks whether a snapshot is set for this transaction.
     *
     * A snapshot ensures that the transaction operates on a consistent view of the database
     * at a specific point in time.
     *
     * @return `true` if a snapshot is set, otherwise `false`.
     */
    fun isSetSnapshot(): Boolean

    /**
     * Sets whether a snapshot should be used for this transaction.
     *
     * Setting this to `true` is equivalent to invoking {@link Transaction#setSnapshot()}.
     *
     * **Default:** `false`
     *
     * @param setSnapshot `true` to set a snapshot, `false` otherwise.
     * @return The current instance of [TransactionalOptions].
     */
    fun setSetSnapshot(setSnapshot: Boolean): TransactionOptions

    /**
     * Determines if deadlock detection is enabled for this transaction.
     *
     * When enabled, the transaction will check for potential deadlocks before acquiring locks.
     * If a deadlock is detected, the transaction will return a busy status, prompting the user to retry.
     *
     * @return `true` if deadlock detection is enabled, otherwise `false`.
     */
    fun isDeadlockDetect(): Boolean

    /**
     * Enables or disables deadlock detection for this transaction.
     *
     * @param deadlockDetect `true` to enable deadlock detection, `false` to disable.
     * @return The current instance of [TransactionOptions].
     */
    fun setDeadlockDetect(deadlockDetect: Boolean): TransactionOptions

    /**
     * Retrieves the lock timeout duration for this transaction.
     *
     * The lock timeout specifies how long (in milliseconds) the transaction will wait to acquire a lock.
     * - If set to `0`, no waiting is performed if a lock cannot be immediately acquired.
     * - If set to a negative value, a default transaction lock timeout is used.
     *
     * @return The lock timeout in milliseconds.
     */
    fun getLockTimeout(): Long

    /**
     * Sets the lock timeout duration for this transaction.
     *
     * @param lockTimeout The desired lock timeout in milliseconds.
     * @return The current instance of [TransactionOptions].
     */
    fun setLockTimeout(lockTimeout: Long): TransactionOptions

    /**
     * Retrieves the expiration duration for this transaction.
     *
     * The expiration duration specifies how long (in milliseconds) a transaction can remain active
     * before it fails to commit. If not set (i.e., negative), forgotten transactions retain their locks indefinitely.
     *
     * @return The expiration duration in milliseconds.
     */
    fun getExpiration(): Long

    /**
     * Sets the expiration duration for this transaction.
     *
     * @param expiration The desired expiration duration in milliseconds.
     * @return The current instance of [TransactionOptions].
     */
    fun setExpiration(expiration: Long): TransactionOptions

    /**
     * Retrieves the depth for deadlock detection traversals.
     *
     * This value determines the number of traversals the system will perform to detect deadlocks.
     *
     * @return The number of traversals for deadlock detection.
     */
    fun getDeadlockDetectDepth(): Long

    /**
     * Sets the depth for deadlock detection traversals.
     *
     * @param deadlockDetectDepth The desired number of traversals for deadlock detection.
     * @return The current instance of [TransactionOptions].
     */
    fun setDeadlockDetectDepth(deadlockDetectDepth: Long): TransactionOptions

    /**
     * Retrieves the maximum number of bytes allowed for the write batch.
     *
     * This limit helps control the memory usage for write operations.
     * - A value of `0` indicates no limit.
     *
     * @return The maximum write batch size in bytes.
     */
    fun getMaxWriteBatchSize(): Long

    /**
     * Sets the maximum number of bytes allowed for the write batch.
     *
     * @param maxWriteBatchSize The desired maximum write batch size in bytes.
     * @return The current instance of [TransactionOptions].
     */
    fun setMaxWriteBatchSize(maxWriteBatchSize: Long): TransactionOptions
}
