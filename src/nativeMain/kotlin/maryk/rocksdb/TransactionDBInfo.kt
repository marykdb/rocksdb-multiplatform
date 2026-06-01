package maryk.rocksdb

actual class KeyLockInfo actual constructor(
    private val key: String,
    private val transactionIDs: LongArray,
    private val exclusive: Boolean
) {
    actual fun getKey(): String = key
    actual fun getTransactionIDs(): LongArray = transactionIDs
    actual fun isExclusive(): Boolean = exclusive
}

actual class DeadlockInfo internal constructor(
    private val transactionID: Long,
    private val columnFamilyId: Long,
    private val waitingKey: String,
    private val exclusive: Boolean
) {
    actual fun getTransactionID(): Long = transactionID
    actual fun getColumnFamilyId(): Long = columnFamilyId
    actual fun getWaitingKey(): String = waitingKey
    actual fun isExclusive(): Boolean = exclusive
}

actual class DeadlockPath internal constructor(
    val path: Array<DeadlockInfo>,
    val limitExceeded: Boolean,
    val deadlockTime: Long
) {
    actual fun isEmpty(): Boolean = path.isEmpty() && !limitExceeded
}
