package maryk.rocksdb

expect abstract class AbstractWriteBatch : RocksObject, WriteBatchInterface {
    override fun count(): Int

    override fun put(key: ByteArray, value: ByteArray)

    override fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    override fun merge(key: ByteArray, value: ByteArray)

    override fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    override fun delete(key: ByteArray)

    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray)

    override fun singleDelete(key: ByteArray)

    override fun singleDelete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    )

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray)

    override fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray,
        endKey: ByteArray
    )

    override fun putLogData(blob: ByteArray)

    override fun clear()

    override fun setSavePoint()

    override fun rollbackToSavePoint()

    override fun popSavePoint()

    override fun setMaxBytes(maxBytes: Long)

    override fun getWriteBatch(): WriteBatch
}
