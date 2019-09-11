package maryk.rocksdb

actual enum class TransactionState(
    private val value: Byte
) {
    STARTED(0x0),
    AWAITING_PREPARE(0x1),
    PREPARED(0x2),
    AWAITING_COMMIT(0x3),
    COMMITED(0x4),
    AWAITING_ROLLBACK(0x5),
    ROLLEDBACK(0x6),
    LOCKS_STOLEN(0x7)
}
