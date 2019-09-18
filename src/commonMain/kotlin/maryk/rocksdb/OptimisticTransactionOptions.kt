package maryk.rocksdb

expect class OptimisticTransactionOptions() : RocksObject {
    /**
     * True indicates snapshots will be set, just like if
     * {@link Transaction#setSnapshot()} had been called
     *
     * @return whether a snapshot will be set
     */
    fun isSetSnapshot(): Boolean

    /**
     * Setting the setSnapshot to true is the same as calling
     * {@link Transaction#setSnapshot()}.
     *
     * Default: false
     *
     * @param <T> The type of transactional options.
     * @param setSnapshot Whether to set a snapshot
     *
     * @return this TransactionalOptions instance
     */
    fun setSetSnapshot(
        setSnapshot: Boolean
    ): OptimisticTransactionOptions

    /**
     * Should be set if the DB has a non-default comparator.
     * See comment in
     * [WriteBatchWithIndex.WriteBatchWithIndex]
     * constructor.
     *
     * @param comparator The comparator to use for the transaction.
     *
     * @return this OptimisticTransactionOptions instance
     */
    fun setComparator(
        comparator: AbstractComparator<out AbstractSlice<*>>
    ): OptimisticTransactionOptions
}
