package maryk.rocksdb

/**
 * Represents transaction database configuration options for RocksDB.
 *
 * This class provides configuration options specific to transaction databases,
 * such as maximum number of locks, number of stripes for lock tables, transaction
 * lock timeouts, and write policies.
 */
expect class TransactionDBOptions() : RocksObject {

    /**
     * Retrieves the maximum number of keys that can be locked simultaneously
     * per column family.
     *
     * If the number of locked keys exceeds this limit, transaction writes
     * (or `GetForUpdate`) will return an error.
     *
     * @return The maximum number of keys that can be locked.
     */
    fun getMaxNumLocks(): Long

    /**
     * Sets the maximum number of keys that can be locked simultaneously
     * per column family.
     *
     * If the provided value is not positive, no limit will be enforced.
     *
     * @param maxNumLocks The maximum number of keys that can be locked.
     * @return The current instance of [TransactionDBOptions].
     */
    fun setMaxNumLocks(maxNumLocks: Long): TransactionDBOptions

    /**
     * Retrieves the number of sub-tables per lock table for each column family.
     *
     * Increasing this value enhances concurrency by dividing the lock table
     * into more sub-tables, each protected by its own mutex.
     *
     * @return The number of sub-tables.
     */
    fun getNumStripes(): Long

    /**
     * Sets the number of sub-tables per lock table for each column family.
     *
     * Increasing this value increases concurrency by dividing the lock table
     * into more sub-tables, each with its own separate mutex.
     *
     * **Default:** 16
     *
     * @param numStripes The number of sub-tables.
     * @return The current instance of [TransactionDBOptions].
     */
    fun setNumStripes(numStripes: Long): TransactionDBOptions

    /**
     * Retrieves the default wait timeout in milliseconds for transactions
     * attempting to lock a key, if not specified by [TransactionOptions.setLockTimeout].
     *
     * - If set to `0`, no waiting is performed if a lock cannot be immediately acquired.
     * - If set to a negative value, there is no timeout.
     *
     * @return The default wait timeout in milliseconds.
     */
    fun getTransactionLockTimeout(): Long

    /**
     * Sets the default wait timeout in milliseconds for transactions
     * attempting to lock a key, if not specified by [TransactionOptions.setLockTimeout].
     *
     * - If set to `0`, no waiting is performed if a lock cannot be immediately acquired.
     * - If set to a negative value, there is no timeout. **Note:** Not using a timeout
     *   is not recommended as it can lead to deadlocks. Currently, there is no
     *   deadlock detection to recover from a deadlock.
     *
     * **Default:** 1000
     *
     * @param transactionLockTimeout The default wait timeout in milliseconds.
     * @return The current instance of [TransactionDBOptions].
     */
    fun setTransactionLockTimeout(transactionLockTimeout: Long): TransactionDBOptions

    /**
     * Retrieves the wait timeout in milliseconds when writing a key
     * outside of a transaction (e.g., by calling [RocksDB.put], [RocksDB.merge],
     * [RocksDB.delete], or [RocksDB.write] directly).
     *
     * - If set to `0`, no waiting is performed if a lock cannot be immediately acquired.
     * - If set to a negative value, there is no timeout, and it will block indefinitely
     *   when acquiring a lock.
     *
     * **Note:** Not using a timeout can lead to deadlocks. Currently, there is no
     * deadlock detection to recover from a deadlock. While DB writes cannot deadlock
     * with other DB writes, they can deadlock with a transaction. A negative timeout
     * should only be used if all transactions have a small expiration set.
     *
     * **Default:** 1000
     *
     * @return The default lock timeout in milliseconds when writing a key outside of a transaction.
     */
    fun getDefaultLockTimeout(): Long

    /**
     * Sets the wait timeout in milliseconds when writing a key
     * outside of a transaction (e.g., by calling [RocksDB.put], [RocksDB.merge],
     * [RocksDB.delete], or [RocksDB.write] directly).
     *
     * - If set to `0`, no waiting is performed if a lock cannot be immediately acquired.
     * - If set to a negative value, there is no timeout, and it will block indefinitely
     *   when acquiring a lock.
     *
     * **Note:** Not using a timeout can lead to deadlocks. Currently, there is no
     * deadlock detection to recover from a deadlock. While DB writes cannot deadlock
     * with other DB writes, they can deadlock with a transaction. A negative timeout
     * should only be used if all transactions have a small expiration set.
     *
     * **Default:** 1000
     *
     * @param defaultLockTimeout The wait timeout in milliseconds when writing a key outside of a transaction.
     * @return The current instance of [TransactionDBOptions].
     */
    fun setDefaultLockTimeout(defaultLockTimeout: Long): TransactionDBOptions

    /**
     * Retrieves the write policy for when data is written into the database.
     *
     * The default policy is to write only the committed data ([TxnDBWritePolicy.WRITE_COMMITTED]).
     * Alternatively, data could be written before the commit phase, in which case the
     * database needs to provide mechanisms to distinguish between committed and uncommitted data.
     *
     * @return The current [TxnDBWritePolicy].
     */
    fun getWritePolicy(): TxnDBWritePolicy

    /**
     * Sets the write policy for when data is written into the database.
     *
     * The default policy is to write only the committed data ([TxnDBWritePolicy.WRITE_COMMITTED]).
     * Alternatively, data could be written before the commit phase, in which case the
     * database needs to provide mechanisms to distinguish between committed and uncommitted data.
     *
     * @param writePolicy The desired [TxnDBWritePolicy].
     * @return The current instance of [TransactionDBOptions].
     */
    fun setWritePolicy(writePolicy: TxnDBWritePolicy): TransactionDBOptions
}
