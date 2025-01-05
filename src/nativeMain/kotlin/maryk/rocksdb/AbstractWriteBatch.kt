package maryk.rocksdb

import cnames.structs.rocksdb_writebatch_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toCValues
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_writebatch_clear
import rocksdb.rocksdb_writebatch_count
import rocksdb.rocksdb_writebatch_delete
import rocksdb.rocksdb_writebatch_delete_cf
import rocksdb.rocksdb_writebatch_delete_range
import rocksdb.rocksdb_writebatch_delete_range_cf
import rocksdb.rocksdb_writebatch_destroy
import rocksdb.rocksdb_writebatch_merge
import rocksdb.rocksdb_writebatch_merge_cf
import rocksdb.rocksdb_writebatch_pop_save_point
import rocksdb.rocksdb_writebatch_put
import rocksdb.rocksdb_writebatch_put_cf
import rocksdb.rocksdb_writebatch_put_log_data
import rocksdb.rocksdb_writebatch_rollback_to_save_point
import rocksdb.rocksdb_writebatch_set_save_point

actual abstract class AbstractWriteBatch internal constructor(
    private val nativeBase: CPointer<rocksdb_writebatch_t>
) : RocksObject(), WriteBatchInterface {
    override fun close() {
        if (isOwningHandle()) {
            rocksdb_writebatch_destroy(nativeBase)
            super.close()
        }
    }

    actual override fun singleDelete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_singledelete(nativeBase, key.toCValues(), key.size.toULong(), error)
        }
    }

    actual override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb.rocksdb_writebatch_singledelete_cf(nativeBase, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), error)
        }
    }

    actual override fun count(): Int =
        rocksdb_writebatch_count(nativeBase)

    actual override fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_put(nativeBase, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    actual override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_put_cf(nativeBase, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    actual override fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_merge(nativeBase, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    actual override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_merge_cf(nativeBase, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), value.toCValues(), value.size.toULong(), error)
        }
    }

    actual override fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete(nativeBase, key.toCValues(), key.size.toULong(), error)
        }
    }

    actual override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete_cf(nativeBase, columnFamilyHandle.native, key.toCValues(), key.size.toULong(), error)
        }
    }

    actual override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete_range(nativeBase, beginKey.toCValues(), beginKey.size.toULong(), endKey.toCValues(), endKey.size.toULong(), error)
        }
    }

    actual override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_delete_range_cf(nativeBase, columnFamilyHandle.native, beginKey.toCValues(), beginKey.size.toULong(), endKey.toCValues(), endKey.size.toULong(), error)
        }
    }

    actual override fun putLogData(blob: ByteArray) {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_put_log_data(nativeBase, blob.toCValues(), blob.size.toULong(), error)
        }
    }

    actual override fun clear() {
        wrapWithErrorThrower { error ->
            rocksdb_writebatch_clear(nativeBase)
        }
    }

    actual override fun setSavePoint() {
        rocksdb_writebatch_set_save_point(nativeBase)
    }

    actual override fun rollbackToSavePoint() {
        wrapWithNullErrorThrower { error ->
            rocksdb_writebatch_rollback_to_save_point(nativeBase, error)
        }
    }

    actual override fun popSavePoint() {
        wrapWithNullErrorThrower { error ->
            rocksdb_writebatch_pop_save_point(nativeBase, error)
        }
    }

    actual override fun setMaxBytes(maxBytes: Long) {
        rocksdb.rocksdb_writebatch_set_max_bytes(nativeBase, maxBytes.toULong())
    }

    actual override fun getWriteBatch(): WriteBatch {
        return WriteBatch(nativeBase)
    }
}
