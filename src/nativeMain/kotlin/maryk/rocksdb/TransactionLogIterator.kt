package maryk.rocksdb

import cnames.structs.rocksdb_wal_iterator_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
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
) : RocksObject() {
    actual fun isValid(): Boolean = rocksdb_wal_iter_valid(native).toBoolean()

    actual fun next() {
        rocksdb_wal_iter_next(native)
    }

    @Throws(RocksDBException::class)
    actual fun status() {
        wrapWithErrorThrower { error ->
            rocksdb_wal_iter_status(native, error)
        }
    }

    actual fun getBatch(): TransactionLogBatchResult = memScoped {
        val sequenceNumber = alloc<uint64_tVar>()
        val batchPointer = rocksdb_wal_iter_get_batch(native, sequenceNumber.ptr)!!
        val batch = WriteBatch(batchPointer).also { it.disownHandle() }
        TransactionLogBatchResult(sequenceNumber.value.toLong(), batch)
    }

    override fun close() {
        if (isOwningHandle()) {
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
