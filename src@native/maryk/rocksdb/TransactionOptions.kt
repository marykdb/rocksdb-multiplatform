@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
import maryk.sizeTToLong
import maryk.toBoolean
import maryk.toUByte

actual class TransactionOptions actual constructor(): RocksObject() {
    val native = requireNotNull(rocksdb.rocksdb_transaction_options_create()) {
        "Unable to allocate RocksDB transaction options"
    }

    actual fun isSetSnapshot(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_options_get_set_snapshot(native).toBoolean()
    }

    actual fun setSetSnapshot(setSnapshot: Boolean): TransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_options_set_set_snapshot(native, setSnapshot.toUByte())
        return this
    }

    actual fun isDeadlockDetect(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_options_get_deadlock_detect(native).toBoolean()
    }

    actual fun setDeadlockDetect(deadlockDetect: Boolean): TransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_options_set_deadlock_detect(native, deadlockDetect.toUByte())
        return this
    }

    actual fun getLockTimeout(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_options_get_lock_timeout(native)
    }

    actual fun setLockTimeout(lockTimeout: Long): TransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_options_set_lock_timeout(native, lockTimeout)
        return this
    }

    actual fun getExpiration(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_options_get_expiration(native)
    }

    actual fun setExpiration(expiration: Long): TransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_options_set_expiration(native, expiration)
        return this
    }

    actual fun getDeadlockDetectDepth(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_transaction_options_get_deadlock_detect_depth(native)
    }

    actual fun setDeadlockDetectDepth(deadlockDetectDepth: Long): TransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_options_set_deadlock_detect_depth(native, deadlockDetectDepth)
        return this
    }

    actual fun getMaxWriteBatchSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb.rocksdb_transaction_options_get_max_write_batch_size(native), "transaction options max write batch size")
    }

    actual fun setMaxWriteBatchSize(maxWriteBatchSize: Long): TransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_transaction_options_set_max_write_batch_size(native, maxWriteBatchSize.asSizeT())
        return this
    }

    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_transaction_options_destroy(native)
            super.close()
        }
    }
}
