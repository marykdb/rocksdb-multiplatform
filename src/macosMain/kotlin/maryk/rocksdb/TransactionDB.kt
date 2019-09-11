package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class TransactionDB private constructor(nativeHandle: CPointer<*>) : RocksDB(nativeHandle) {
    actual fun beginTransaction(writeOptions: WriteOptions): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun beginTransaction(
        writeOptions: WriteOptions,
        transactionOptions: TransactionOptions
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
        transactionOptions: TransactionOptions,
        oldTransaction: Transaction
    ): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTransactionByName(transactionName: String): Transaction? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getAllPreparedTransactions(): List<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getLockStatusData(): Map<Long, KeyLockInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getDeadlockInfoBuffer(): Array<DeadlockPath> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeadlockInfoBufferSize(targetSize: Int) {
    }
}

actual class KeyLockInfo actual constructor(
    key: String,
    transactionIDs: LongArray,
    exclusive: Boolean
) {
    actual fun getKey(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTransactionIDs(): LongArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isExclusive(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual class DeadlockInfo {
    actual fun getTransactionID(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getColumnFamilyId(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getWaitingKey(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isExclusive(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual class DeadlockPath actual constructor(
    path: Array<DeadlockInfo>,
    limitExceeded: Boolean
) {
    actual fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
