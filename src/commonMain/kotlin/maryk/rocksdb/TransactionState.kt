package maryk.rocksdb

expect enum class TransactionState {
    STARTED,
    AWAITING_PREPARE,
    PREPARED,
    AWAITING_COMMIT,
    COMMITED,
    AWAITING_ROLLBACK,
    ROLLEDBACK,
    LOCKS_STOLEN
}
