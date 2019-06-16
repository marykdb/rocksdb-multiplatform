package maryk.rocksdb

/**
 * Handler callback for iterating over the contents of a batch.
 */
expect abstract class WriteBatchHandler() : RocksCallbackObject {
    // @Throws(RocksDBException::class)
    abstract fun put(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    )

    abstract fun put(key: ByteArray, value: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun merge(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    )

    abstract fun merge(key: ByteArray, value: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun delete(columnFamilyId: Int, key: ByteArray)

    abstract fun delete(key: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun singleDelete(
        columnFamilyId: Int,
        key: ByteArray
    )

    abstract fun singleDelete(key: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun deleteRange(
        columnFamilyId: Int,
        beginKey: ByteArray, endKey: ByteArray
    )

    abstract fun deleteRange(
        beginKey: ByteArray,
        endKey: ByteArray
    )

    abstract fun logData(blob: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun putBlobIndex(
        columnFamilyId: Int,
        key: ByteArray, value: ByteArray
    )

    // @Throws(RocksDBException::class)
    abstract fun markBeginPrepare()

    // @Throws(RocksDBException::class)
    abstract fun markEndPrepare(xid: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun markNoop(emptyBatch: Boolean)

    // @Throws(RocksDBException::class)
    abstract fun markRollback(xid: ByteArray)

    // @Throws(RocksDBException::class)
    abstract fun markCommit(xid: ByteArray)

    /**
     * shouldContinue is called by the underlying iterator
     * [WriteBatch.iterate]. If it returns false,
     * iteration is halted. Otherwise, it continues
     * iterating. The default implementation always
     * returns true.
     *
     * @return boolean value indicating if the
     * iteration is halted.
     */
    fun shouldContinue(): Boolean
}
