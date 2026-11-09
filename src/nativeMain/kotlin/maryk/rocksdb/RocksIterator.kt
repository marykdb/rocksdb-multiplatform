@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_iterator_t
import kotlinx.cinterop.*
import maryk.asSizeT
import maryk.toByteArray
import platform.posix.size_tVar
import rocksdb.rocksdb_iter_key
import rocksdb.rocksdb_iter_value

internal data class IteratorOwnerTransfer(
    val dbOwner: RocksDB?,
    val transactionOwner: Transaction?,
    val writeBatchWithIndexOwners: Set<WriteBatchWithIndex>,
    val readOptionsOwners: Set<ReadOptions>,
)

actual class RocksIterator internal constructor(
    native: CPointer<rocksdb_iterator_t>,
    private var dbOwner: RocksDB? = null,
    private var transactionOwner: Transaction? = null,
    writeBatchWithIndexOwner: WriteBatchWithIndex? = null,
    inheritedWriteBatchWithIndexOwners: Set<WriteBatchWithIndex> = emptySet(),
    readOptionsDependencies: Set<ReadOptions> = emptySet(),
) : AbstractRocksIterator<RocksDB>(native) {
    private val writeBatchWithIndexOwners = inheritedWriteBatchWithIndexOwners.toMutableSet().apply {
        writeBatchWithIndexOwner?.let(::add)
    }
    init {
        dbOwner?.registerBorrowedIterator(this)
        transactionOwner?.registerBorrowedIterator(this)
        writeBatchWithIndexOwners.forEach { it.registerBorrowedIterator(this) }
        readOptionsDependencies.forEach(::dependOnReadOptions)
    }

    actual fun key(): ByteArray = memScoped {
        check(isValid()) { "Iterator is not valid." }
        val length = alloc<size_tVar>()
        val key = rocksdb_iter_key(native, length.ptr)
        if (length.value == 0.asSizeT()) {
            ByteArray(0)
        } else {
            requireNotNull(key) {
                "RocksDB returned null iterator key for ${length.value} bytes."
            }.toByteArray(length.value)
        }
    }

    actual fun value(): ByteArray = memScoped {
        check(isValid()) { "Iterator is not valid." }
        val length = alloc<size_tVar>()
        val value = rocksdb_iter_value(native, length.ptr)
        if (length.value == 0.asSizeT()) {
            ByteArray(0)
        } else {
            requireNotNull(value) {
                "RocksDB returned null iterator value for ${length.value} bytes."
            }.toByteArray(length.value)
        }
    }

    override fun close() {
        val db = dbOwner
        if (db != null) {
            db.withLifecycleLock { closeLocked() }
        } else {
            closeLocked()
        }
    }

    private fun closeLocked() {
        if (!isOwningHandle()) return
        val db = dbOwner
        val owner = transactionOwner
        val batches = writeBatchWithIndexOwners.toList()
        // Mark closed and destroy the iterator before detaching its DB lock.
        // A concurrent ownership transfer must still acquire that lock until
        // the wrapper can no longer pass its open-handle check.
        super.close()
        dbOwner = null
        transactionOwner = null
        writeBatchWithIndexOwners.clear()
        db?.unregisterBorrowedIterator(this)
        owner?.unregisterBorrowedIterator(this)
        batches.forEach { it.unregisterBorrowedIterator(this) }
    }

    internal fun invalidateFromOwner() {
        close()
    }

    internal fun transferWrapperOwnershipToNativeAndDetachOwners(): IteratorOwnerTransfer {
        val db = dbOwner
        return if (db != null) {
            db.withLifecycleLock { transferWrapperOwnershipLocked() }
        } else {
            transferWrapperOwnershipLocked()
        }
    }

    internal fun <T> withDatabaseLifecycleLock(block: () -> T): T {
        val db = dbOwner
        return if (db != null) db.withLifecycleLock(block) else block()
    }

    private fun transferWrapperOwnershipLocked(): IteratorOwnerTransfer {
        check(disownHandle()) { "Iterator is already closed or transferred." }
        return IteratorOwnerTransfer(
            dbOwner,
            transactionOwner,
            writeBatchWithIndexOwners.toSet(),
            detachReadOptionsOwners(),
        ).also {
            dbOwner?.unregisterBorrowedIterator(this)
            transactionOwner?.unregisterBorrowedIterator(this)
            writeBatchWithIndexOwners.forEach { it.unregisterBorrowedIterator(this) }
            dbOwner = null
            transactionOwner = null
            writeBatchWithIndexOwners.clear()
        }
    }
}
