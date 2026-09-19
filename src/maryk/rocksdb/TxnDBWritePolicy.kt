package maryk.rocksdb

/**
 * Represents the transaction database write policies for RocksDB.
 *
 * This enum defines the different policies that determine when data is written
 * to the database in the context of transactions. Each policy corresponds to
 * a specific phase in the two-phase commit (2PC) protocol.
 */
expect enum class TxnDBWritePolicy {
    /**
     * Write only the committed data.
     *
     * This policy ensures that only data from transactions that have been
     * successfully committed are written to the database.
     */
    WRITE_COMMITTED,

    /**
     * Write data after the prepare phase of 2PC.
     *
     * In this policy, data is written to the database after the transaction
     * has passed the prepare phase but before the commit phase of the two-phase
     * commit protocol.
     */
    WRITE_PREPARED,

    /**
     * Write data before the prepare phase of 2PC.
     *
     * This policy allows data to be written to the database before the transaction
     * enters the prepare phase of the two-phase commit protocol.
     */
    WRITE_UNPREPARED;

    /**
     * Retrieves the byte value associated with the write policy.
     *
     * @return The byte representation of the write policy.
     */
    fun getValue(): Byte
}
