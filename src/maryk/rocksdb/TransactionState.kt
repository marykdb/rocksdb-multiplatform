package maryk.rocksdb

/**
 * Represents the execution state of a transaction.
 */
expect enum class TransactionState {
    STARTED,
    AWAITING_PREPARE,
    PREPARED,
    AWAITING_COMMIT,
    COMMITTED,
    AWAITING_ROLLBACK,
    ROLLEDBACK,
    LOCKS_STOLEN;
}
