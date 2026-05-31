@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_transactiondb_t
import kotlinx.cinterop.*
import maryk.asSizeT
import maryk.asUInt32
import maryk.sizeTToInt
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

private fun transactionBaseDb(native: CPointer<rocksdb_transactiondb_t>) =
    requireNotNull(rocksdb.rocksdb_transactiondb_get_base_db(native)) {
        "RocksDB returned null base DB for transaction DB"
    }

actual open class TransactionDB
internal constructor(
    internal val tnative: CPointer<rocksdb_transactiondb_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
) : RocksDB(transactionBaseDb(tnative), ownedComparators, retainedReferences), TransactionOwner {
    val defaultTransactionOptions: TransactionOptions = TransactionOptions()
    private val borrowedTransactions = mutableSetOf<Transaction>()

    override fun close() {
        if (tryClose()) {
            invalidateBorrowedTransactions()
            invalidateBorrowedIterators()
            invalidateBorrowedTransactionLogIterators()
            releaseBorrowedSnapshots()
            invalidateColumnFamilyHandles()
            defaultTransactionOptions.close()
            closeDefaultReferences()
            rocksdb.rocksdb_transactiondb_close_base_db(native)
            rocksdb.rocksdb_transactiondb_close(tnative)
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

    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        return requireNotNull(
            rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, null)
        ) {
            "RocksDB returned null transaction"
        }.let { Transaction(it).attachTo(this) }
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions
    ): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        transactionOptions.checkOwningHandle()
        return requireNotNull(
            rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, transactionOptions.native, null)
        ) {
            "RocksDB returned null transaction"
        }.let { Transaction(it).attachTo(this) }
    }

    actual fun beginTransaction(writeOptions: WriteOptions, oldTransaction: Transaction): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        oldTransaction.prepareForReuse()
        rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, oldTransaction.native)
        return oldTransaction.attachTo(this)
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        checkOwningHandle()
        writeOptions.checkOwningHandle()
        transactionOptions.checkOwningHandle()
        oldTransaction.prepareForReuse()
        rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, transactionOptions.native, oldTransaction.native)
        return oldTransaction.attachTo(this)
    }

    actual fun getAllPreparedTransactions(): List<Transaction> {
        checkOwningHandle()
        memScoped {
            val count = alloc<size_tVar>()
            val transactions = rocksdb.rocksdb_transactiondb_get_prepared_transactions(tnative, count.ptr)
            var preparedTransactions: ArrayList<Transaction>? = null
            var countInt = 0

            try {
                countInt = sizeTToInt(count.value, "prepared transaction count")
                val preparedTransactionsList = ArrayList<Transaction>(countInt)
                preparedTransactions = preparedTransactionsList
                if (countInt == 0) return preparedTransactionsList
                val transactionPointers = requireNotNull(transactions) {
                    "RocksDB returned null prepared transaction array for $countInt transactions"
                }
                transactionPointers.let {
                    for (index in 0 until countInt) {
                        val rawTransaction = requireNotNull(transactionPointers[index]) {
                            "RocksDB returned null prepared transaction at index $index"
                        }
                        transactionPointers[index] = null
                        val transaction = try {
                            Transaction(rawTransaction).attachTo(this@TransactionDB)
                        } catch (throwable: Throwable) {
                            rocksdb.rocksdb_transaction_destroy(rawTransaction)
                            throw throwable
                        }
                        try {
                            preparedTransactionsList += transaction
                        } catch (throwable: Throwable) {
                            transaction.close()
                            throw throwable
                        }
                    }
                }
                return preparedTransactionsList
            } catch (throwable: Throwable) {
                preparedTransactions?.forEach { it.close() }
                throw throwable
            } finally {
                transactions?.let {
                    for (index in 0 until countInt) {
                        transactions[index]?.let { transaction ->
                            rocksdb.rocksdb_transaction_destroy(transaction)
                        }
                    }
                    rocksdb.rocksdb_free(it)
                }
            }
        }
    }

    actual fun setDeadlockInfoBufferSize(targetSize: Int) {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_set_deadlock_info_buffer_size(tnative, targetSize.asUInt32())
    }
}
