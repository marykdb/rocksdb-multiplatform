package maryk.rocksdb
/**
 * Represents a collection of waiting transactions.
 */
expect class WaitingTransactions {
    /** The column family ID */
    fun getColumnFamilyId(): Long

    /** The key on which the transactions are waiting. */
    fun getKey(): String

    /** The IDs of the waiting transactions. */
    fun getTransactionIds(): LongArray
}
