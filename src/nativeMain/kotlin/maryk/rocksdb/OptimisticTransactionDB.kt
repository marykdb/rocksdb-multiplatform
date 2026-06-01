@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_optimistictransactiondb_t
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

private fun optimisticTransactionBaseDb(native: CPointer<rocksdb_optimistictransactiondb_t>) =
    requireNotNull(rocksdb.rocksdb_optimistictransactiondb_get_base_db(native)) {
        "RocksDB returned null base DB for optimistic transaction DB"
    }

actual open class OptimisticTransactionDB
internal constructor(
    internal val tnative: CPointer<rocksdb_optimistictransactiondb_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
) : RocksDB(optimisticTransactionBaseDb(tnative), ownedComparators, retainedReferences), TransactionOwner {
    val defaultTransactionOptions: OptimisticTransactionOptions = OptimisticTransactionOptions()
    private val borrowedTransactions = mutableSetOf<Transaction>()
    private val borrowedBaseDbs = mutableSetOf<BorrowedOptimisticBaseDB>()

    override fun close() {
        if (tryClose()) {
            invalidateBorrowedTransactions()
            invalidateBorrowedBaseDbs()
            invalidateBorrowedIterators()
            invalidateBorrowedTransactionLogIterators()
            releaseBorrowedSnapshots()
            invalidateColumnFamilyHandles()
            defaultTransactionOptions.close()
            closeDefaultReferences()
            rocksdb.rocksdb_optimistictransactiondb_close_base_db(native)
            rocksdb.rocksdb_optimistictransactiondb_close(tnative)
            closeOwnedComparators()
            clearRetainedReferences()
            super.close()
        }
    }

    override fun registerBorrowedTransaction(transaction: Transaction) {
        borrowedTransactions += transaction
    }

    override fun unregisterBorrowedTransaction(transaction: Transaction) {
        borrowedTransactions -= transaction
    }

    private fun invalidateBorrowedTransactions() {
        if (borrowedTransactions.isEmpty()) return
        val transactions = borrowedTransactions.toList()
        borrowedTransactions.clear()
        transactions.forEach { it.invalidateFromOwner() }
    }

    private fun invalidateBorrowedBaseDbs() {
        if (borrowedBaseDbs.isEmpty()) return
        val baseDbs = borrowedBaseDbs.toList()
        borrowedBaseDbs.clear()
        baseDbs.forEach { it.invalidateFromOwner() }
    }

    internal fun unregisterBorrowedBaseDb(db: BorrowedOptimisticBaseDB) {
        borrowedBaseDbs -= db
    }

    actual fun getBaseDB(): RocksDB {
        checkOwningHandle()
        return BorrowedOptimisticBaseDB(
            requireNotNull(rocksdb.rocksdb_optimistictransactiondb_get_base_db(tnative)) {
                "RocksDB returned null optimistic transaction base DB"
            },
            this
        ).also { borrowedBaseDbs += it }
    }

    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        return requireNotNull(
            rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, null)
        ) {
            "RocksDB returned null optimistic transaction"
        }.let { Transaction(it).attachTo(this) }
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: OptimisticTransactionOptions
    ): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        transactionOptions.checkOwningHandle()
        return requireNotNull(
            rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, transactionOptions.native, null)
        ) {
            "RocksDB returned null optimistic transaction"
        }.let { Transaction(it).attachTo(this) }
    }

    actual fun beginTransaction(writeOptions: WriteOptions, oldTransaction: Transaction): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        oldTransaction.prepareForReuse()
        rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, oldTransaction.native)
        return oldTransaction.attachTo(this)
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: OptimisticTransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        transactionOptions.checkOwningHandle()
        oldTransaction.prepareForReuse()
        rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, transactionOptions.native, oldTransaction.native)
        return oldTransaction.attachTo(this)
    }
}

internal class BorrowedOptimisticBaseDB(
    native: CPointer<cnames.structs.rocksdb_t>,
    private var owner: OptimisticTransactionDB?,
) : RocksDB(native) {
    override fun close() {
        val dbOwner = owner
        owner = null
        dbOwner?.unregisterBorrowedBaseDb(this)
        super.close()
    }

    internal fun invalidateFromOwner() {
        owner = null
        super.close()
    }
}
