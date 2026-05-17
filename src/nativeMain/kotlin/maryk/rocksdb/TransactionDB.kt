@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_transactiondb_t
import kotlinx.cinterop.*
import maryk.asSizeT
import platform.posix.size_tVar
import kotlin.experimental.ExperimentalNativeApi

actual open class TransactionDB
internal constructor(
    internal val tnative: CPointer<rocksdb_transactiondb_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
) : RocksDB(rocksdb.rocksdb_transactiondb_get_base_db(tnative)!!, ownedComparators, retainedReferences) {
    val defaultTransactionOptions: TransactionOptions = TransactionOptions()

    override fun close() {
        if (tryClose()) {
            defaultTransactionOptions.close()
            closeDefaultReferences()
            rocksdb.rocksdb_transactiondb_close_base_db(native)
            rocksdb.rocksdb_transactiondb_close(tnative)
            closeOwnedComparators()
            clearRetainedReferences()
            super.close()
        }
    }

    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        return rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, null)!!.let(::Transaction)
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions
    ): Transaction {
        return rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, transactionOptions.native, null)!!.let(::Transaction)
    }

    actual fun beginTransaction(writeOptions: WriteOptions, oldTransaction: Transaction): Transaction {
        rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, oldTransaction.native)
        return oldTransaction
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        rocksdb.rocksdb_transaction_begin(tnative, writeOptions.native, transactionOptions.native, oldTransaction.native)
        return oldTransaction
    }

    actual fun getAllPreparedTransactions(): List<Transaction> {
        memScoped {
            val count = alloc<size_tVar>()
            val transactions = rocksdb.rocksdb_transactiondb_get_prepared_transactions(tnative, count.ptr)
            var preparedTransactions: ArrayList<Transaction>? = null

            try {
                val preparedTransactionsList = ArrayList<Transaction>(count.value.toInt())
                preparedTransactions = preparedTransactionsList
                transactions?.let {
                    for (index in 0.asSizeT() until count.value) {
                        val rawTransaction = transactions[index.toInt()]!!
                        transactions[index.toInt()] = null
                        val transaction = try {
                            Transaction(rawTransaction)
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
                    for (index in 0.asSizeT() until count.value) {
                        transactions[index.toInt()]?.let { transaction ->
                            rocksdb.rocksdb_transaction_destroy(transaction)
                        }
                    }
                    rocksdb.rocksdb_free(it)
                }
            }
        }
    }

    actual fun setDeadlockInfoBufferSize(targetSize: Int) {
        rocksdb.rocksdb_transactiondb_set_deadlock_info_buffer_size(tnative, targetSize.convert())
    }
}
