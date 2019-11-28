package maryk.rocksdb

import maryk.toNSData
import maryk.wrapWithErrorThrower
import rocksdb.RocksDBKeyRange
import rocksdb.RocksDBWriteBatchBase
import rocksdb.getWriteBatch

actual abstract class AbstractWriteBatch internal constructor(
    private val nativeBase: RocksDBWriteBatchBase
) : RocksObject(), WriteBatchInterface {
    override fun singleDelete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.singleDelete(key.toNSData(), error)
        }
    }

    override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.singleDelete(key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    override fun count(): Int {
        return nativeBase.count()
    }

    override fun put(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.setData(value.toNSData(), key.toNSData(), error)
        }
    }

    override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.setData(value.toNSData(), key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    override fun merge(key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.mergeData(value.toNSData(), key.toNSData(), error)
        }
    }

    override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.mergeData(value.toNSData(), key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    override fun delete(key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.deleteDataForKey(key.toNSData(), error)
        }
    }

    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.deleteDataForKey(key.toNSData(), columnFamilyHandle.native, error)
        }
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            nativeBase.deleteRange(range, error)
        }
    }

    override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        wrapWithErrorThrower { error ->
            val range = RocksDBKeyRange()
            range.start = beginKey.toNSData()
            range.end = endKey.toNSData()
            nativeBase.deleteRange(range, columnFamilyHandle.native, error)
        }
    }

    override fun putLogData(blob: ByteArray) {
        wrapWithErrorThrower { error ->
            nativeBase.putLogData(blob.toNSData(), error)
        }
    }

    override fun clear() {
        nativeBase.clear()
    }

    override fun setSavePoint() {
        nativeBase.setSavePoint()
    }

    override fun rollbackToSavePoint() {
        wrapWithErrorThrower { error ->
            nativeBase.rollbackToSavePoint(error)
        }
    }

    override fun popSavePoint() {
        wrapWithErrorThrower { error ->
            nativeBase.popSavePoint(error)
        }
    }

    override fun setMaxBytes(maxBytes: Long) {
        nativeBase.setMaxBytes(maxBytes.toULong())
    }

    override fun getWriteBatch(): WriteBatch {
        return WriteBatch(nativeBase.getWriteBatch()!!)
    }
}
