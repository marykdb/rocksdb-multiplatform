package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractWriteBatch protected
    constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle), WriteBatchInterface {
    actual override fun count(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun put(key: ByteArray, value: ByteArray) {
    }

    actual override fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual override fun merge(key: ByteArray, value: ByteArray) {
    }

    actual override fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    ) {
    }

    actual override fun delete(key: ByteArray) {
    }

    actual override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
    }

    actual override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
    }

    actual override fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        beginKey: ByteArray,
        endKey: ByteArray
    ) {
    }

    actual override fun putLogData(blob: ByteArray) {
    }

    actual override fun clear() {
    }

    actual override fun setSavePoint() {
    }

    actual override fun rollbackToSavePoint() {
    }

    actual override fun popSavePoint() {
    }

    actual override fun setMaxBytes(maxBytes: Long) {
    }

    actual override fun getWriteBatch(): WriteBatch {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
