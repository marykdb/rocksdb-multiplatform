package maryk.rocksdb

import cnames.structs.rocksdb_wal_iterator_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toCheckedLong
import maryk.toBoolean
import maryk.wrapWithErrorThrower
import platform.posix.uint64_tVar
import rocksdb.rocksdb_wal_iter_destroy
import rocksdb.rocksdb_wal_iter_get_batch
import rocksdb.rocksdb_wal_iter_next
import rocksdb.rocksdb_wal_iter_status
import rocksdb.rocksdb_wal_iter_valid

@OptIn(ExperimentalForeignApi::class)
actual class TransactionLogIterator internal constructor(
    internal val native: CPointer<rocksdb_wal_iterator_t>,
    private var owner: RocksDB? = null,
) : RocksObject() {
    actual fun isValid(): Boolean {
        checkOwningHandle()
        return rocksdb_wal_iter_valid(native).toBoolean()
    }

    actual fun next() {
        checkOwningHandle()
        rocksdb_wal_iter_next(native)
    }

    @Throws(RocksDBException::class)
    actual fun status() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_wal_iter_status(native, error)
        }
    }

    actual fun getBatch(): TransactionLogBatchResult = memScoped {
        check(isValid()) { "Transaction log iterator is not valid." }
        val sequenceNumber = alloc<uint64_tVar>()
        val batchPointer = requireNotNull(rocksdb_wal_iter_get_batch(native, sequenceNumber.ptr)) {
            "RocksDB returned null WAL batch for a valid iterator"
        }
        val batch = WriteBatch(batchPointer)
        try {
            TransactionLogBatchResult(sequenceNumber.value.toCheckedLong("WAL sequence number"), batch)
        } catch (throwable: Throwable) {
            batch.close()
            throw throwable
        }
    }

    override fun close() {
        val db = owner
        owner = null
        db?.unregisterBorrowedTransactionLogIterator(this)
        if (tryClose()) {
            rocksdb_wal_iter_destroy(native)
            super.close()
        }
    }

    internal fun invalidateFromOwner() {
        owner = null
        if (tryClose()) {
            rocksdb_wal_iter_destroy(native)
            super.close()
        }
    }
}

actual class TransactionLogBatchResult internal constructor(
    private val sequenceNumberValue: Long,
    private val writeBatchValue: WriteBatch,
) {
    actual fun sequenceNumber(): Long = sequenceNumberValue

    actual fun writeBatch(): WriteBatch = writeBatchValue
}
