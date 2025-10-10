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
) : RocksDB(rocksdb.rocksdb_transactiondb_get_base_db(tnative)!!) {
    val defaultTransactionOptions: TransactionOptions = TransactionOptions()

    override fun close() {
        if (isOwningHandle()) {
            defaultTransactionOptions.close()
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

            return buildList {
                transactions?.let {
                    for (index in 0.asSizeT() until count.value) {
                        add(
                            Transaction(
                                transactions[index.toInt()]!!
                            )
                        )
                    }
                    rocksdb.rocksdb_free(transactions)
                }
            }
        }
    }

    actual fun setDeadlockInfoBufferSize(targetSize: Int) {
        rocksdb.rocksdb_transactiondb_set_deadlock_info_buffer_size(tnative, targetSize.convert())
    }
}
