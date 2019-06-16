package maryk.rocksdb

expect class WaitingTransactions {
    /** Get the Column Family ID. */
    fun getColumnFamilyId(): Long
    /** Get the key on which the transactions are waiting. */
    fun getKey(): String
    /** Get the IDs of the waiting transactions. */
    fun getTransactionIds(): LongArray
}
