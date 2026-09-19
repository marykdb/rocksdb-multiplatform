@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_iterator_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.toByteArray
import platform.posix.size_tVar
import rocksdb.rocksdb_iter_key
import rocksdb.rocksdb_iter_value

actual class SstFileReaderIterator internal constructor(
    native: CPointer<rocksdb_iterator_t>,
    private var readerOwner: SstFileReader?,
) : AbstractRocksIterator<SstFileReader>(native) {
    init {
        readerOwner?.registerBorrowedIterator(this)
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
        val owner = readerOwner
        readerOwner = null
        owner?.unregisterBorrowedIterator(this)
        super.close()
    }

    internal fun invalidateFromOwner() {
        readerOwner = null
        super.close()
    }
}
