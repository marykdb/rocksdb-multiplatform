package maryk.rocksdb

import cnames.structs.rocksdb_writeoptions_t
import kotlinx.cinterop.CPointer
import maryk.toBoolean
import maryk.toUByte
import rocksdb.rocksdb_writeoptions_get_memtable_insert_hint_per_batch
import rocksdb.rocksdb_writeoptions_set_memtable_insert_hint_per_batch
import rocksdb.rocksdb_writeoptions_create
import rocksdb.rocksdb_writeoptions_destroy
import rocksdb.rocksdb_writeoptions_disable_WAL
import rocksdb.rocksdb_writeoptions_get_disable_WAL
import rocksdb.rocksdb_writeoptions_get_ignore_missing_column_families
import rocksdb.rocksdb_writeoptions_get_low_pri
import rocksdb.rocksdb_writeoptions_get_no_slowdown
import rocksdb.rocksdb_writeoptions_get_sync
import rocksdb.rocksdb_writeoptions_set_ignore_missing_column_families
import rocksdb.rocksdb_writeoptions_set_low_pri
import rocksdb.rocksdb_writeoptions_set_no_slowdown
import rocksdb.rocksdb_writeoptions_set_sync

actual class WriteOptions internal constructor(
    internal val native: CPointer<rocksdb_writeoptions_t>,
    private val ownsNative: Boolean = true,
    private var transactionOwner: Transaction? = null,
) : RocksObject() {
    init {
        if (!ownsNative) {
            borrowHandle()
        }
        transactionOwner?.registerBorrowedWriteOptions(this)
    }

    actual constructor() : this(requireNotNull(rocksdb_writeoptions_create()) { "Unable to allocate RocksDB write options" })

    override fun close() {
        transactionOwner?.unregisterBorrowedWriteOptions(this)
        transactionOwner = null
        if (ownsNative && tryClose()) {
            rocksdb_writeoptions_destroy(native)
            super.close()
        } else if (!ownsNative && tryCloseBorrowed()) {
            super.close()
        }
    }

    internal fun invalidateFromOwner() = close()

    actual fun setSync(flag: Boolean): WriteOptions {
        checkOpenHandle()
        rocksdb_writeoptions_set_sync(native, flag.toUByte())
        return this
    }

    actual fun sync(): Boolean {
        checkOpenHandle()
        return rocksdb_writeoptions_get_sync(native).toBoolean()
    }

    actual fun setDisableWAL(flag: Boolean): WriteOptions {
        checkOpenHandle()
        rocksdb_writeoptions_disable_WAL(native, if (flag) 1 else 0)
        return this
    }

    actual fun disableWAL(): Boolean {
        checkOpenHandle()
        return rocksdb_writeoptions_get_disable_WAL(native).toBoolean()
    }

    actual fun setIgnoreMissingColumnFamilies(ignoreMissingColumnFamilies: Boolean): WriteOptions {
        checkOpenHandle()
        rocksdb_writeoptions_set_ignore_missing_column_families(native, ignoreMissingColumnFamilies.toUByte())
        return this
    }

    actual fun ignoreMissingColumnFamilies(): Boolean {
        checkOpenHandle()
        return rocksdb_writeoptions_get_ignore_missing_column_families(native).toBoolean()
    }

    actual fun setNoSlowdown(noSlowdown: Boolean): WriteOptions {
        checkOpenHandle()
        rocksdb_writeoptions_set_no_slowdown(native, noSlowdown.toUByte())
        return this
    }

    actual fun noSlowdown(): Boolean {
        checkOpenHandle()
        return rocksdb_writeoptions_get_no_slowdown(native).toBoolean()
    }

    actual fun setLowPri(lowPri: Boolean): WriteOptions {
        checkOpenHandle()
        rocksdb_writeoptions_set_low_pri(native, lowPri.toUByte())
        return this
    }

    actual fun lowPri(): Boolean {
        checkOpenHandle()
        return rocksdb_writeoptions_get_low_pri(native).toBoolean()
    }

    actual fun setMemtableInsertHintPerBatch(memtableInsertHintPerBatch: Boolean): WriteOptions {
        checkOpenHandle()
        rocksdb_writeoptions_set_memtable_insert_hint_per_batch(native, memtableInsertHintPerBatch.toUByte())
        return this
    }

    actual fun memtableInsertHintPerBatch(): Boolean {
        checkOpenHandle()
        return rocksdb_writeoptions_get_memtable_insert_hint_per_batch(native).toBoolean()
    }
}
