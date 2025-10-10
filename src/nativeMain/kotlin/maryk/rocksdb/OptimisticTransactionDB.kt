@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_optimistictransactiondb_t
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

actual open class OptimisticTransactionDB
internal constructor(
    internal val tnative: CPointer<rocksdb_optimistictransactiondb_t>,
) : RocksDB(rocksdb.rocksdb_optimistictransactiondb_get_base_db(tnative)!!) {
    val defaultTransactionOptions: OptimisticTransactionOptions = OptimisticTransactionOptions()

    override fun close() {
        if (isOwningHandle()) {
            defaultTransactionOptions.close()
            super.close()
        }
    }

    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        return rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, null)!!.let(::Transaction)
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: OptimisticTransactionOptions
    ): Transaction {
        return rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, transactionOptions.native, null)!!.let(::Transaction)
    }

    actual fun beginTransaction(writeOptions: WriteOptions, oldTransaction: Transaction): Transaction {
        rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, defaultTransactionOptions.native, oldTransaction.native)
        return oldTransaction
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: OptimisticTransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        rocksdb.rocksdb_optimistictransaction_begin(tnative, writeOptions.native, transactionOptions.native, oldTransaction.native)
        return oldTransaction
    }
}
