package maryk.rocksdb

expect class OptimisticTransactionOptions() : RocksObject {

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
    fun setSetSnapshot(setSnapshot: Boolean): OptimisticTransactionOptions
}
