package maryk.rocksdb

/**
 * Reads a consistent DB snapshot through temporary [ReadOptions]. The options
 * are closed before the snapshot is released, including when [block] throws.
 * Keep the DB open and do not retain the options, snapshot, or their iterators
 * beyond the callback. Writes inside the callback are not part of its read view.
 */
fun <T> RocksDB.withSnapshot(block: (ReadOptions) -> T): T {
    val snapshot = checkNotNull(getSnapshot()) { "RocksDB could not create a snapshot" }
    return withOperationCleanup({ releaseSnapshot(snapshot) }) {
        ReadOptions().use { readOptions ->
            readOptions.setSnapshot(snapshot)
            block(readOptions)
        }
    }
}

/**
 * Runs [block] once in a pessimistic transaction using temporary write options.
 * Commits on success; on callback or commit failure, attempts rollback and
 * preserves the original exception. Always closes the transaction. The callback
 * must not commit, roll back, close, prepare, or retain the transaction. Nested
 * calls create independent transactions, not savepoints. No retry is performed.
 */
fun <T> TransactionDB.withTransaction(block: (Transaction) -> T): T =
    WriteOptions().use { withTransaction(it, block = block) }

/** Like [withTransaction], borrowing the caller's options without closing them. */
fun <T> TransactionDB.withTransaction(
    writeOptions: WriteOptions,
    transactionOptions: TransactionOptions? = null,
    block: (Transaction) -> T,
): T = runScopedTransaction(
    if (transactionOptions == null) beginTransaction(writeOptions)
    else beginTransaction(writeOptions, transactionOptions),
    block,
)

/**
 * Runs [block] once in an optimistic transaction using temporary write options.
 * Conflicts are propagated to the caller; the callback is never automatically
 * retried. Commit, rollback, and close follow the same rules as [TransactionDB.withTransaction].
 */
fun <T> OptimisticTransactionDB.withTransaction(block: (Transaction) -> T): T =
    WriteOptions().use { withTransaction(it, block = block) }

/** Like [withTransaction], borrowing the caller's options without closing them. */
fun <T> OptimisticTransactionDB.withTransaction(
    writeOptions: WriteOptions,
    transactionOptions: OptimisticTransactionOptions? = null,
    block: (Transaction) -> T,
): T = runScopedTransaction(
    if (transactionOptions == null) beginTransaction(writeOptions)
    else beginTransaction(writeOptions, transactionOptions),
    block,
)

private fun <T> runScopedTransaction(transaction: Transaction, block: (Transaction) -> T): T =
    transaction.use {
        try {
            val result = block(transaction)
            transaction.commit()
            result
        } catch (failure: Throwable) {
            try {
                transaction.rollback()
            } catch (rollbackFailure: Throwable) {
                if (rollbackFailure !== failure) failure.addSuppressed(rollbackFailure)
            }
            throw failure
        }
    }

private fun <T> withOperationCleanup(cleanup: () -> Unit, block: () -> T): T {
    var failure: Throwable? = null
    try {
        return block()
    } catch (error: Throwable) {
        failure = error
        throw error
    } finally {
        try {
            cleanup()
        } catch (cleanupFailure: Throwable) {
            val original = failure
            if (original == null) throw cleanupFailure
            if (original !== cleanupFailure) original.addSuppressed(cleanupFailure)
        }
    }
}
