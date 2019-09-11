package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class TransactionOptions actual constructor() : RocksObject(newTransactionOptions()) {
    actual fun isSetSnapshot(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSetSnapshot(setSnapshot: Boolean): TransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isDeadlockDetect(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeadlockDetect(deadlockDetect: Boolean): TransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getLockTimeout(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLockTimeout(lockTimeout: Long): TransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getExpiration(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setExpiration(expiration: Long): TransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getDeadlockDetectDepth(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeadlockDetectDepth(deadlockDetectDepth: Long): TransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getMaxWriteBatchSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxWriteBatchSize(maxWriteBatchSize: Long): TransactionOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun newTransactionOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
