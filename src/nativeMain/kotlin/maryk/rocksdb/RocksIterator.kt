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
)

actual class RocksIterator internal constructor(
    native: CPointer<rocksdb_iterator_t>,
    private var dbOwner: RocksDB? = null,
    private var transactionOwner: Transaction? = null,
    private var writeBatchWithIndexOwner: WriteBatchWithIndex? = null,
) : AbstractRocksIterator<RocksDB>(native) {
    init {
        dbOwner?.registerBorrowedIterator(this)
        transactionOwner?.registerBorrowedIterator(this)
        writeBatchWithIndexOwner?.registerBorrowedIterator(this)
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
        val owner = transactionOwner
        val writeBatchWithIndex = writeBatchWithIndexOwner
        dbOwner = null
        transactionOwner = null
        writeBatchWithIndexOwner = null
        db?.unregisterBorrowedIterator(this)
        owner?.unregisterBorrowedIterator(this)
        writeBatchWithIndex?.unregisterBorrowedIterator(this)
        super.close()
    }

    internal fun invalidateFromOwner() {
        dbOwner = null
        transactionOwner = null
        writeBatchWithIndexOwner = null
        super.close()
    }

    internal fun transferWrapperOwnershipToNativeAndDetachOwners(): IteratorOwnerTransfer {
        check(disownHandle()) { "Iterator is already closed or transferred." }
        val transfer = IteratorOwnerTransfer(dbOwner, transactionOwner)
        dbOwner?.unregisterBorrowedIterator(this)
        transactionOwner?.unregisterBorrowedIterator(this)
        writeBatchWithIndexOwner?.unregisterBorrowedIterator(this)
        dbOwner = null
        transactionOwner = null
        writeBatchWithIndexOwner = null
        return transfer
    }
}
