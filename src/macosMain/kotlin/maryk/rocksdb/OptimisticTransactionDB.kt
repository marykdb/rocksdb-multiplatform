package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class OptimisticTransactionDB
    private constructor(nativeHandle: CPointer<*>)
: RocksDB(nativeHandle) {
    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        optimisticTransactionOptions: OptimisticTransactionOptions
    ): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        oldTransaction: Transaction
    ): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        optimisticTransactionOptions: OptimisticTransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getBaseDB(): RocksDB {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
