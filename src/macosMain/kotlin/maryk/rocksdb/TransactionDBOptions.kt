package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class TransactionDBOptions actual constructor() : RocksObject(newTransactionDBOptions()) {
    actual fun getMaxNumLocks(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxNumLocks(maxNumLocks: Long): TransactionDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getNumStripes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setNumStripes(numStripes: Long): TransactionDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTransactionLockTimeout(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTransactionLockTimeout(transactionLockTimeout: Long): TransactionDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getDefaultLockTimeout(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDefaultLockTimeout(defaultLockTimeout: Long): TransactionDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getWritePolicy(): TxnDBWritePolicy {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWritePolicy(writePolicy: TxnDBWritePolicy): TransactionDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun newTransactionDBOptions(): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
