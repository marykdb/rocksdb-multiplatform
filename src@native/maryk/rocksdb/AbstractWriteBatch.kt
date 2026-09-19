package maryk.rocksdb

actual abstract class AbstractWriteBatch : RocksObject(), WriteBatchInterface {

    actual override fun singleDelete(key: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun count(): Int =
        throw NotImplementedError()

    actual override fun put(key: ByteArray, value: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun merge(key: ByteArray, value: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun merge(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun delete(key: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun deleteRange(columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray, endKey: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun putLogData(blob: ByteArray) {
        throw NotImplementedError()
    }

    actual override fun clear() {
        throw NotImplementedError()
    }

    actual override fun setSavePoint() {
        throw NotImplementedError()
    }

    actual override fun rollbackToSavePoint() {
        throw NotImplementedError()
    }

    actual override fun popSavePoint() {
        throw NotImplementedError()
    }

    actual override fun setMaxBytes(maxBytes: Long) {
        throw NotImplementedError()
    }

    actual override fun getWriteBatch(): WriteBatch {
        throw NotImplementedError()
    }
}
