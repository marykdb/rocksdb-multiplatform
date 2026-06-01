@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_wbwi_iterator_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import maryk.DirectByteBuffer
import maryk.asSizeT
import maryk.sizeTToInt
import maryk.toBoolean
import maryk.usePointer
import maryk.wrapWithErrorThrower
import platform.posix.size_tVar
import rocksdb.rocksdb_wbwi_iterator_destroy
import rocksdb.rocksdb_wbwi_iterator_entry_key
import rocksdb.rocksdb_wbwi_iterator_entry_type
import rocksdb.rocksdb_wbwi_iterator_entry_value
import rocksdb.rocksdb_wbwi_iterator_next
import rocksdb.rocksdb_wbwi_iterator_prev
import rocksdb.rocksdb_wbwi_iterator_seek
import rocksdb.rocksdb_wbwi_iterator_seek_for_prev
import rocksdb.rocksdb_wbwi_iterator_seek_to_first
import rocksdb.rocksdb_wbwi_iterator_seek_to_last
import rocksdb.rocksdb_wbwi_iterator_status
import rocksdb.rocksdb_wbwi_iterator_valid

actual class WBWIRocksIterator internal constructor(
    internal val native: CPointer<rocksdb_wbwi_iterator_t>,
    private val owner: WriteBatchWithIndex,
) : RocksObject(), RocksIteratorInterface {
    override fun isValid(): Boolean {
        checkOwningHandle()
        return rocksdb_wbwi_iterator_valid(native).toBoolean()
    }

    override fun seekToFirst() {
        checkOwningHandle()
        rocksdb_wbwi_iterator_seek_to_first(native)
    }

    override fun seekToLast() {
        checkOwningHandle()
        rocksdb_wbwi_iterator_seek_to_last(native)
    }

    override fun seek(target: ByteArray) {
        checkOwningHandle()
        target.usePointer { targetPointer ->
            rocksdb_wbwi_iterator_seek(native, targetPointer, target.size.asSizeT())
        }
    }

    override fun seekForPrev(target: ByteArray) {
        checkOwningHandle()
        target.usePointer { targetPointer ->
            rocksdb_wbwi_iterator_seek_for_prev(native, targetPointer, target.size.asSizeT())
        }
    }

    override fun next() {
        checkOwningHandle()
        rocksdb_wbwi_iterator_next(native)
    }

    override fun prev() {
        checkOwningHandle()
        rocksdb_wbwi_iterator_prev(native)
    }

    override fun status() {
        checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_wbwi_iterator_status(native, error)
        }
    }

    actual fun entry(): WriteEntry {
        checkOwningHandle()
        check(isValid()) { "WriteBatchWithIndex iterator is not valid." }
        return memScoped {
            val keyLength = alloc<size_tVar>()
            val valueLength = alloc<size_tVar>()
            val keyPointer = rocksdb_wbwi_iterator_entry_key(native, keyLength.ptr)
            val valuePointer = rocksdb_wbwi_iterator_entry_value(native, valueLength.ptr)
            val keySize = sizeTToInt(keyLength.value, "write batch iterator entry key")
            val valueSize = sizeTToInt(valueLength.value, "write batch iterator entry value")
            val keySlice = if (keySize == 0) {
                DirectSlice("")
            } else {
                DirectSlice(DirectByteBuffer(
                    requireNotNull(keyPointer) { "RocksDB returned null write batch iterator entry key for $keySize bytes." }
                        .reinterpret(),
                    keySize
                ))
            }
            val writeType = getWriteTypeByValue(rocksdb_wbwi_iterator_entry_type(native).toUInt())
            val valueSlice = valuePointer?.let {
                DirectSlice(DirectByteBuffer(it.reinterpret(), valueSize))
            } ?: if (valueSize == 0 && writeType.hasValue()) DirectSlice("") else null
            WriteEntry(
                writeType,
                keySlice,
                valueSlice,
            )
        }
    }

    actual override fun close() {
        if (tryClose()) {
            owner.unregisterBorrowedWBWIIterator(this)
            rocksdb_wbwi_iterator_destroy(native)
            super.close()
        }
    }

    internal fun invalidateFromOwner() {
        if (tryClose()) {
            rocksdb_wbwi_iterator_destroy(native)
            super.close()
        }
    }
}

private fun WriteType.hasValue(): Boolean =
    this == WriteType.PUT || this == WriteType.MERGE || this == WriteType.XID
