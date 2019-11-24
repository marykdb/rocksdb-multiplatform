package maryk.rocksdb

actual interface WriteBatchInterface {
    actual fun count(): Int

    actual fun put(key: ByteArray, value: ByteArray)

    actual fun put(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray, value: ByteArray)

    actual fun merge(key: ByteArray, value: ByteArray)

    actual fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray,
        value: ByteArray
    )

    actual fun delete(key: ByteArray)

    actual fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray)

    actual fun singleDelete(key: ByteArray)

    actual fun singleDelete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray)

    actual fun deleteRange(beginKey: ByteArray, endKey: ByteArray)

    actual fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        beginKey: ByteArray,
        endKey: ByteArray
    )

    actual fun putLogData(blob: ByteArray)

    actual fun clear()

    actual fun setSavePoint()

    actual fun rollbackToSavePoint()

    actual fun popSavePoint()

    actual fun setMaxBytes(maxBytes: Long)

    actual fun getWriteBatch(): WriteBatch
}
