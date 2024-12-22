package maryk.rocksdb

import maryk.toNSData
import maryk.wrapWithErrorThrower
import rocksdb.RocksDBKeyRange
import rocksdb.RocksDBWriteBatchBase
import rocksdb.getWriteBatch

actual abstract class AbstractWriteBatch internal constructor(
    private val nativeBase: RocksDBWriteBatchBase
) : RocksObject(), WriteBatchInterface {
    actual override fun singleDelete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.singleDelete(key.toNSData(), error)
        }
    }

    actual override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.singleDelete(key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    actual override fun count(): Int {
        return nativeBase.count()
    }

    actual override fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.setData(value.toNSData(), key.toNSData(), error)
        }
    }

    actual override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.setData(value.toNSData(), key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    actual override fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.mergeData(value.toNSData(), key.toNSData(), error)
        }
    }

    actual override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.mergeData(value.toNSData(), key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    actual override fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.deleteDataForKey(key.toNSData(), error)
        }
    }

    actual override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.deleteDataForKey(key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    actual override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            nativeBase.deleteRange(range, error)
        }
    }

    actual override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            nativeBase.deleteRange(range, columnFamilyHandle.native, error)
        }
    }

    actual override fun putLogData(blob: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.putLogData(blob.toNSData(), error)
        }
    }

    actual override fun clear() {
        nativeBase.clear()
    }

    actual override fun setSavePoint() {
        nativeBase.setSavePoint()
    }

    actual override fun rollbackToSavePoint() {
        wrapWithErrorThrower { error ->
            nativeBase.rollbackToSavePoint(error)
        }
    }

    actual override fun popSavePoint() {
        wrapWithErrorThrower { error ->
            nativeBase.popSavePoint(error)
        }
    }

    actual override fun setMaxBytes(maxBytes: Long) {
        nativeBase.setMaxBytes(maxBytes.toULong())
    }

    actual override fun getWriteBatch(): WriteBatch {
        return WriteBatch(nativeBase.getWriteBatch()!!)
    }
}
