package maryk.rocksdb

import cnames.structs.rocksdb_snapshot_t
import kotlinx.cinterop.CPointer
import maryk.toCheckedLong

actual class Snapshot internal constructor(
    internal val native: CPointer<rocksdb_snapshot_t>,
    private var owner: RocksDB? = null,
    private var transactionOwner: Transaction? = null,
    private val freeWrapperOnClose: Boolean = false,
) : RocksObject() {
    init {
        owner?.registerBorrowedSnapshot(this)
    }

    actual fun getSequenceNumber(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_snapshot_get_sequence_number(native).toCheckedLong("snapshot sequence number")
    }

    override fun close() {
        val dbOwner = owner
        when {
            dbOwner != null -> releaseFrom(dbOwner)
            transactionOwner != null -> closeTransactionSnapshot()
            freeWrapperOnClose && tryClose() -> {
                rocksdb.rocksdb_transaction_snapshot_destroy(native)
                super.close()
            }
            else -> super.close()
        }
    }

    internal fun releaseFrom(db: RocksDB) {
        db.checkOwningHandle()
        owner = null
        if (tryClose()) {
            db.unregisterBorrowedSnapshot(this)
            rocksdb.rocksdb_release_snapshot(db.native, native)
            super.close()
        }
    }

    internal fun invalidateFromOwner(db: RocksDB) {
        owner = null
        if (tryClose()) {
            rocksdb.rocksdb_release_snapshot(db.native, native)
            super.close()
        }
    }

    private fun closeTransactionSnapshot() {
        val owner = transactionOwner
        transactionOwner = null
        if (tryClose()) {
            if (freeWrapperOnClose) {
                rocksdb.rocksdb_transaction_snapshot_destroy(native)
            }
            owner?.unregisterBorrowedSnapshot(this)
            super.close()
        }
    }

    internal fun invalidateFromTransaction() {
        transactionOwner = null
        if (tryClose()) {
            if (freeWrapperOnClose) {
                rocksdb.rocksdb_transaction_snapshot_destroy(native)
            }
            super.close()
        }
    }
}
