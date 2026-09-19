@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
import maryk.sizeTToLong

actual class TransactionDBOptions actual constructor(): RocksObject() {
    val native = requireNotNull(rocksdb.rocksdb_transactiondb_options_create()) {
        "Unable to allocate RocksDB transaction DB options"
    }

    actual fun getMaxNumLocks(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transactiondb_options_get_max_num_locks(native)
    }

    actual fun setMaxNumLocks(maxNumLocks: Long): TransactionDBOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_options_set_max_num_locks(native, maxNumLocks)
        return this
    }

    actual fun getNumStripes(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb.rocksdb_transactiondb_options_get_num_stripes(native), "transaction DB options stripe count")
    }

    actual fun setNumStripes(numStripes: Long): TransactionDBOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_options_set_num_stripes(native, numStripes.asSizeT())
        return this
    }

    actual fun getTransactionLockTimeout(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transactiondb_options_get_transaction_lock_timeout(native)
    }

    actual fun setTransactionLockTimeout(transactionLockTimeout: Long): TransactionDBOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_options_set_transaction_lock_timeout(native, transactionLockTimeout)
        return this
    }

    actual fun getDefaultLockTimeout(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transactiondb_options_get_default_lock_timeout(native)
    }

    actual fun setDefaultLockTimeout(defaultLockTimeout: Long): TransactionDBOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_options_set_default_lock_timeout(native, defaultLockTimeout)
        return this
    }

    actual fun getWritePolicy(): TxnDBWritePolicy {
        checkOwningHandle()
        return getTxnDBWritePolicy(rocksdb.rocksdb_transactiondb_options_get_write_policy(native).toByte())
    }

    actual fun setWritePolicy(writePolicy: TxnDBWritePolicy): TransactionDBOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transactiondb_options_set_write_policy(native, writePolicy.getValue().toInt())
        return this
    }

    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_transactiondb_options_destroy(native)
            super.close()
        }
    }
}
