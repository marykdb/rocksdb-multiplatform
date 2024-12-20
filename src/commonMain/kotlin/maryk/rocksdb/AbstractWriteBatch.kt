
package maryk.rocksdb

expect abstract class AbstractWriteBatch : RocksObject, WriteBatchInterface {
    /** Returns the number of updates in the batch. */
    override fun count(): Int

    /**
     * Store the mapping "key-&gt;value" in the database.
     *
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun put(key: ByteArray, value: ByteArray)

    /**
     * Store the mapping "key->value" within given column
     * family.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     *
     * Merge "value" with the existing value of "key" in the database.
     * "key->merge(existing, value)"
     *
     * @param key the specified key to be merged.
     * @param value the value to be merged with the current value for
     * the specified key.
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun merge(key: ByteArray, value: ByteArray)

    /**
     * Merge "value" with the existing value of "key" in given column family.
     * "key->merge(existing, value)"
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key the specified key to be merged.
     * @param value the value to be merged with the current value for
     * the specified key.
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     * If the database contains a mapping for "key", erase it.  Else do nothing.
     *
     * @param key Key to delete within database
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun delete(key: ByteArray)

    /**
     * If column family contains a mapping for "key", erase it.  Else do nothing.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key Key to delete within database
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun delete(columnFamilyHandle: ColumnFamilyHandle, key: ByteArray)

    /**
     * Remove the database entry for `key`. Requires that the key exists
     * and was not overwritten. It is not an error if the key did not exist
     * in the database.
     *
     * If a key is overwritten (by calling [.put] multiple
     * times), then the result of calling SingleDelete() on this key is undefined.
     * SingleDelete() only behaves correctly if there has been only one Put()
     * for this key since the previous call to SingleDelete() for this key.
     *
     * This feature is currently an experimental performance optimization
     * for a very specific workload. It is up to the caller to ensure that
     * SingleDelete is only used for a key that is not deleted using Delete() or
     * written using Merge(). Mixing SingleDelete operations with Deletes and
     * Merges can result in undefined behavior.
     *
     * @param key Key to delete within database
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    // @Experimental("Performance optimization for a very specific workload")
    override fun singleDelete(key: ByteArray)

    /**
     * Remove the database entry for `key`. Requires that the key exists
     * and was not overwritten. It is not an error if the key did not exist
     * in the database.
     *
     * If a key is overwritten (by calling [.put] multiple
     * times), then the result of calling SingleDelete() on this key is undefined.
     * SingleDelete() only behaves correctly if there has been only one Put()
     * for this key since the previous call to SingleDelete() for this key.
     *
     * This feature is currently an experimental performance optimization
     * for a very specific workload. It is up to the caller to ensure that
     * SingleDelete is only used for a key that is not deleted using Delete() or
     * written using Merge(). Mixing SingleDelete operations with Deletes and
     * Merges can result in undefined behavior.
     *
     * @param columnFamilyHandle The column family to delete the key from
     * @param key Key to delete within database
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    // @Experimental("Performance optimization for a very specific workload")
    override fun singleDelete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    )

    /**
     * Removes the database entries in the range ["beginKey", "endKey"), i.e.,
     * including "beginKey" and excluding "endKey". a non-OK status on error. It
     * is not an error if no keys exist in the range ["beginKey", "endKey").
     *
     * Delete the database entry (if any) for "key". Returns OK on success, and a
     * non-OK status on error. It is not an error if "key" did not exist in the
     * database.
     *
     * @param beginKey
     * First key to delete within database (included)
     * @param endKey
     * Last key to delete within database (excluded)
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray)

    /**
     * Removes the database entries in the range ["beginKey", "endKey"), i.e.,
     * including "beginKey" and excluding "endKey". a non-OK status on error. It
     * is not an error if no keys exist in the range ["beginKey", "endKey").
     *
     * Delete the database entry (if any) for "key". Returns OK on success, and a
     * non-OK status on error. It is not an error if "key" did not exist in the
     * database.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param beginKey
     * First key to delete within database (included)
     * @param endKey
     * Last key to delete within database (excluded)
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle, beginKey: ByteArray,
        endKey: ByteArray
    )

    /**
     * Append a blob of arbitrary size to the records in this batch. The blob will
     * be stored in the transaction log but not in any other file. In particular,
     * it will not be persisted to the SST files. When iterating over this
     * WriteBatch, WriteBatch::Handler::LogData will be called with the contents
     * of the blob as it is encountered. Blobs, puts, deletes, and merges will be
     * encountered in the same order in thich they were inserted. The blob will
     * NOT consume sequence number(s) and will NOT increase the count of the batch
     *
     * Example application: add timestamps to the transaction log for use in
     * replication.
     *
     * @param blob binary object to be inserted
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    override fun putLogData(blob: ByteArray)

    /**
     * Clear all updates buffered in this batch
     */
    override fun clear()

    /**
     * Records the state of the batch for future calls to RollbackToSavePoint().
     * May be called multiple times to set multiple save points.
     */
    override fun setSavePoint()

    /**
     * Remove all entries in this batch (Put, Merge, Delete, PutLogData) since
     * the most recent call to SetSavePoint() and removes the most recent save
     * point.
     *
     * @throws RocksDBException if there is no previous call to SetSavePoint()
     */
    override fun rollbackToSavePoint()

    /**
     * Pop the most recent save point.
     *
     * That is to say that it removes the last save point,
     * which was set by [.setSavePoint].
     *
     * @throws RocksDBException If there is no previous call to
     * [.setSavePoint], an exception with
     * [Status.Code.NotFound] will be thrown.
     */
    override fun popSavePoint()

    /**
     * Set the maximum size of the write batch.
     * @param maxBytes the maximum size in bytes.
     */
    override fun setMaxBytes(maxBytes: Long)

    /** Get the underlying Write Batch. */
    override fun getWriteBatch(): WriteBatch
}
