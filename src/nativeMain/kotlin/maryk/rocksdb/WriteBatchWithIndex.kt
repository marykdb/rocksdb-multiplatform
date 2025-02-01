package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_wi_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.value
import maryk.toByteArray
import maryk.wrapWithErrorThrower
import platform.posix.uint64_tVar
import rocksdb.rocksdb_writebatch_wi_create

actual class WriteBatchWithIndex(
    internal val native: CPointer<rocksdb_writebatch_wi_t>
) : AbstractWriteBatch() {
    actual constructor() : this(false)

    actual constructor(overwriteKey: Boolean) : this(rocksdb_writebatch_wi_create(0.convert(), if(overwriteKey) 1.convert() else 0.convert())!!)

    actual constructor(fallbackIndexComparator: AbstractComparator, reservedBytes: Int, overwriteKey: Boolean) : this(
        rocksdb.rocksdb_writebatch_wi_create_with_params(fallbackIndexComparator.native, reservedBytes.convert(), if(overwriteKey) 1.convert() else 0.convert(), 0.convert(), 0.convert())!!
    )

    actual fun newIteratorWithBase(columnFamilyHandle: ColumnFamilyHandle, baseIterator: RocksIterator): RocksIterator {
        return rocksdb.rocksdb_writebatch_wi_create_iterator_with_base_cf(native, baseIterator.native, columnFamilyHandle.native)!!.let(::RocksIterator)
    }

    actual fun newIteratorWithBase(baseIterator: RocksIterator): RocksIterator {
        return rocksdb.rocksdb_writebatch_wi_create_iterator_with_base(native, baseIterator.native)!!.let(::RocksIterator)
    }

    actual fun getFromBatch(columnFamilyHandle: ColumnFamilyHandle, options: DBOptions, key: ByteArray): ByteArray? {
        var a: ByteArray? = null
        wrapWithErrorThrower { error ->
            memScoped {
                val length = alloc<uint64_tVar>()
                a = rocksdb.rocksdb_writebatch_wi_get_from_batch_cf(
                    native,
                    options.native,
                    columnFamilyHandle.native,
                    key.toCValues(),
                    key.size.toULong(),
                    length.ptr,
                    error
                )?.toByteArray(length.value)
            }
        }
        return a
    }

    actual fun getFromBatch(options: DBOptions, key: ByteArray): ByteArray? {
        var a: ByteArray? = null
        memScoped {
            wrapWithErrorThrower { error ->
                val length = alloc<uint64_tVar>()
                a = rocksdb.rocksdb_writebatch_wi_get_from_batch(
                    native,
                    options.native,
                    key.toCValues(),
                    key.size.toULong(),
                    length.ptr,
                    error
                )?.toByteArray(length.value)
            }
        }
        return a
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_writebatch_wi_destroy(native)
            super.close()
        }
    }

    override fun singleDelete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_singledelete(native, key.toCValues(), key.size.toULong(), error)
        }
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_singledelete_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), error)
        }
    }

    override fun count(): Int =
        rocksdb.rocksdb_writebatch_wi_count(native)

    override fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_put(native, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_put_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    override fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_merge(native, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_merge_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    override fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_delete(native, key.toCValues(), key.size.toULong(), error)
        }
    }

    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_delete_cf(native, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), error)
        }
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_delete_range(native, beginKey.toCValues(), beginKey.size.toULong(), endKey.toCValues(), endKey.size.toULong(), error)
        }
    }

    override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_delete_range_cf(native, columnFamilyHandle.native, beginKey.toCValues(), beginKey.size.toULong(), endKey.toCValues(), endKey.size.toULong(), error)
        }
    }

    override fun putLogData(blob: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_put_log_data(native, blob.toCValues(), blob.size.toULong(), error)
        }
    }

    override fun clear() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_clear(native)
        }
    }

    override fun setSavePoint() {
        rocksdb.rocksdb_writebatch_wi_set_save_point(native)
    }

    override fun rollbackToSavePoint() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_rollback_to_save_point(native, error)
        }
    }

    override fun popSavePoint() {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_wi_pop_save_point(native, error)
        }
    }

    override fun setMaxBytes(maxBytes: Long) {
        rocksdb.rocksdb_writebatch_wi_set_max_bytes(native, maxBytes.toULong())
    }

    override fun getWriteBatch(): WriteBatch = TODO()
}
