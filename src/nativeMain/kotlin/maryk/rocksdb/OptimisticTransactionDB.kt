@file:OptIn(ExperimentalNativeApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_optimistictransactiondb_t
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

actual open class OptimisticTransactionDB
internal constructor(
    internal val tnative: CPointer<rocksdb_optimistictransactiondb_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
) : RocksDB(rocksdb.rocksdb_optimistictransactiondb_get_base_db(tnative)!!, ownedComparators, retainedReferences) {
    val defaultTransactionOptions: OptimisticTransactionOptions = OptimisticTransactionOptions()

    override fun close() {
        if (tryClose()) {
            defaultTransactionOptions.close()
            closeDefaultReferences()
            rocksdb.rocksdb_optimistictransactiondb_close_base_db(native)
            rocksdb.rocksdb_optimistictransactiondb_close(tnative)
            closeOwnedComparators()
            clearRetainedReferences()
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
