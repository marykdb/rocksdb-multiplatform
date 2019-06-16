package maryk.rocksdb

expect val defaultColumnFamily: ByteArray
expect val rocksDBNotFound: Int

/**
 * A RocksDB is a persistent ordered map from keys to values.  It is safe for
 * concurrent access from multiple threads without any external synchronization.
 * All methods of this class could potentially throw RocksDBException, which
 * indicates something wrong at the RocksDB library side and the call failed.
 */
expect open class RocksDB : RocksObject {
    /**
     * This is similar to {@link #close()} except that it
     * throws an exception if any error occurs.
     *
     * This will not fsync the WAL files.
     * If syncing is required, the caller must first call {@link #syncWal()}
     * or {@link #write(WriteOptions, WriteBatch)} using an empty write batch
     * with {@link WriteOptions#setSync(boolean)} set to true.
     *
     * See also {@link #close()}.
     *
     * @throws RocksDBException if an error occurs whilst closing.
     */
    fun closeE()

    /**
     * This is similar to {@link #closeE()} except that it
     * silently ignores any errors.
     *
     * This will not fsync the WAL files.
     * If syncing is required, the caller must first call {@link #syncWal()}
     * or {@link #write(WriteOptions, WriteBatch)} using an empty write batch
     * with {@link WriteOptions#setSync(boolean)} set to true.
     *
     * See also {@link #close()}.
     */
    override fun close()

    /**
     * Creates a new column family with the name columnFamilyName and
     * allocates a ColumnFamilyHandle within an internal structure.
     * The ColumnFamilyHandle is automatically disposed with DB disposal.
     *
     * @param columnFamilyDescriptor column family to be created.
     * @return [ColumnFamilyHandle] instance.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun createColumnFamily(
        columnFamilyDescriptor: ColumnFamilyDescriptor
    ): ColumnFamilyHandle

    /**
     * Bulk create column families with the same column family options.
     *
     * @param columnFamilyOptions the options for the column families.
     * @param columnFamilyNames the names of the column families.
     * @return the handles to the newly created column families.
     */
    fun createColumnFamilies(
        columnFamilyOptions: ColumnFamilyOptions,
        columnFamilyNames: List<ByteArray>
    ): List<ColumnFamilyHandle>

    /**
     * Bulk create column families with the same column family options.
     * @param columnFamilyDescriptors the descriptions of the column families.
     * @return the handles to the newly created column families.
     */
    fun createColumnFamilies(
        columnFamilyDescriptors: List<ColumnFamilyDescriptor>
    ): List<ColumnFamilyHandle>

    /**
     * Drops the column family specified by `columnFamilyHandle`. This call
     * only records a drop record in the manifest and prevents the column
     * family from flushing and compacting.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun dropColumnFamily(columnFamilyHandle: ColumnFamilyHandle)

    /**
     * Bulk drop column families. This call only records drop records in the
     * manifest and prevents the column families from flushing and compacting.
     * In case of error, the request may succeed partially. User may call
     * ListColumnFamilies to check the result.
     */
    fun dropColumnFamilies(columnFamilies: List<ColumnFamilyHandle>)

    /**
     * Set the database entry for "key" to "value".
     *
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun put(key: ByteArray, value: ByteArray)

    /**
     * Set the database entry for "key" to "value".
     *
     * @param key The specified key to be inserted
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value associated with the specified key
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     *
     * @throws RocksDBException thrown if errors happens in underlying native
     * library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun put(
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    )

    /**
     * Set the database entry for "key" to "value" in the specified
     * column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * throws IllegalArgumentException if column family is not present
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     * Set the database entry for "key" to "value" in the specified
     * column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key The specified key to be inserted
     * @param offset the offset of the "key" array to be used, must
     * be non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value associated with the specified key
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length - offset)
     *
     * @throws RocksDBException thrown if errors happens in underlying native
     * library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    )

    /**
     * Set the database entry for "key" to "value".
     *
     * @param writeOpts [WriteOptions] instance.
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun put(
        writeOpts: WriteOptions, key: ByteArray,
        value: ByteArray
    )

    /**
     * Set the database entry for "key" to "value".
     *
     * @param writeOpts [WriteOptions] instance.
     * @param key The specified key to be inserted
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value associated with the specified key
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun put(
        writeOpts: WriteOptions,
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    )

    /**
     * Set the database entry for "key" to "value" for the specified
     * column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param writeOpts [WriteOptions] instance.
     * @param key the specified key to be inserted.
     * @param value the value associated with the specified key.
     *
     * throws IllegalArgumentException if column family is not present
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @see IllegalArgumentException
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions, key: ByteArray,
        value: ByteArray
    )

    /**
     * Set the database entry for "key" to "value" for the specified
     * column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param writeOpts [WriteOptions] instance.
     * @param key The specified key to be inserted
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value associated with the specified key
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun put(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions,
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    )

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param key Key to delete within database
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(key: ByteArray)

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param key Key to delete within database
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be
     * non-negative and no larger than ("key".length - offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(key: ByteArray, offset: Int, len: Int)

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @param key Key to delete within database
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    )

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @param key Key to delete within database
     * @param offset the offset of the "key" array to be used,
     * must be non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("value".length - offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, offset: Int, len: Int
    )

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param writeOpt WriteOptions to be used with delete operation
     * @param key Key to delete within database
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(writeOpt: WriteOptions, key: ByteArray)

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param writeOpt WriteOptions to be used with delete operation
     * @param key Key to delete within database
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be
     * non-negative and no larger than ("key".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(
        writeOpt: WriteOptions, key: ByteArray,
        offset: Int, len: Int
    )

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @param writeOpt WriteOptions to be used with delete operation
     * @param key Key to delete within database
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions, key: ByteArray
    )

    /**
     * Delete the database entry (if any) for "key".  Returns OK on
     * success, and a non-OK status on error.  It is not an error if "key"
     * did not exist in the database.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @param writeOpt WriteOptions to be used with delete operation
     * @param key Key to delete within database
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be
     * non-negative and no larger than ("key".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun delete(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions, key: ByteArray, offset: Int,
        len: Int
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
     * @param beginKey First key to delete within database (inclusive)
     * @param endKey Last key to delete within database (exclusive)
     *
     * @throws RocksDBException thrown if error happens in underlying native
     * library.
     */
    fun deleteRange(beginKey: ByteArray, endKey: ByteArray)

    /**
     * Removes the database entries in the range ["beginKey", "endKey"), i.e.,
     * including "beginKey" and excluding "endKey". a non-OK status on error. It
     * is not an error if no keys exist in the range ["beginKey", "endKey").
     *
     * Delete the database entry (if any) for "key". Returns OK on success, and a
     * non-OK status on error. It is not an error if "key" did not exist in the
     * database.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle] instance
     * @param beginKey First key to delete within database (inclusive)
     * @param endKey Last key to delete within database (exclusive)
     *
     * @throws RocksDBException thrown if error happens in underlying native
     * library.
     */
    fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        beginKey: ByteArray, endKey: ByteArray
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
     * @param writeOpt WriteOptions to be used with delete operation
     * @param beginKey First key to delete within database (inclusive)
     * @param endKey Last key to delete within database (exclusive)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun deleteRange(
        writeOpt: WriteOptions, beginKey: ByteArray,
        endKey: ByteArray
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
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle] instance
     * @param writeOpt WriteOptions to be used with delete operation
     * @param beginKey First key to delete within database (included)
     * @param endKey Last key to delete within database (excluded)
     *
     * @throws RocksDBException thrown if error happens in underlying native
     * library.
     */
    fun deleteRange(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpt: WriteOptions, beginKey: ByteArray, endKey: ByteArray
    )

    /**
     * Add merge operand for key/value pair.
     *
     * @param key the specified key to be merged.
     * @param value the value to be merged with the current value for the
     * specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun merge(key: ByteArray, value: ByteArray)

    /**
     * Add merge operand for key/value pair.
     *
     * @param key the specified key to be merged.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value to be merged with the current value for the
     * specified key.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and must be non-negative and no larger than
     * ("value".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun merge(
        key: ByteArray, offset: Int, len: Int, value: ByteArray,
        vOffset: Int, vLen: Int
    )

    /**
     * Add merge operand for key/value pair in a ColumnFamily.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key the specified key to be merged.
     * @param value the value to be merged with the current value for
     * the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: ByteArray
    )

    /**
     * Add merge operand for key/value pair in a ColumnFamily.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key the specified key to be merged.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value to be merged with the current value for
     * the specified key.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * must be non-negative and no larger than ("value".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, offset: Int, len: Int, value: ByteArray,
        vOffset: Int, vLen: Int
    )

    /**
     * Add merge operand for key/value pair.
     *
     * @param writeOpts [WriteOptions] for this write.
     * @param key the specified key to be merged.
     * @param value the value to be merged with the current value for
     * the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun merge(
        writeOpts: WriteOptions, key: ByteArray,
        value: ByteArray
    )

    /**
     * Add merge operand for key/value pair.
     *
     * @param writeOpts [WriteOptions] for this write.
     * @param key the specified key to be merged.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("value".length -  offset)
     * @param value the value to be merged with the current value for
     * the specified key.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun merge(
        writeOpts: WriteOptions,
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    )

    /**
     * Add merge operand for key/value pair.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param writeOpts [WriteOptions] for this write.
     * @param key the specified key to be merged.
     * @param value the value to be merged with the current value for the
     * specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun merge(
        columnFamilyHandle: ColumnFamilyHandle,
        writeOpts: WriteOptions, key: ByteArray, value: ByteArray
    )

    /**
     * Add merge operand for key/value pair.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param writeOpts [WriteOptions] for this write.
     * @param key the specified key to be merged.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the value to be merged with the current value for
     * the specified key.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IndexOutOfBoundsException if an offset or length is out of bounds
     */
    fun merge(
        columnFamilyHandle: ColumnFamilyHandle, writeOpts: WriteOptions,
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    )

    /**
     * Apply the specified updates to the database.
     *
     * @param writeOpts WriteOptions instance
     * @param updates WriteBatch instance
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun write(writeOpts: WriteOptions, updates: WriteBatch)

    /**
     * Apply the specified updates to the database.
     *
     * @param writeOpts WriteOptions instance
     * @param updates WriteBatchWithIndex instance
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun write(
        writeOpts: WriteOptions,
        updates: WriteBatchWithIndex
    )

    /**
     * Get the value associated with the specified key within column family*
     *
     * @param key the key to retrieve the value.
     * @param value the out-value to receive the retrieved value.
     *
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(key: ByteArray, value: ByteArray): Int

    /**
     * Get the value associated with the specified key within column family*
     *
     * @param key the key to retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the out-value to receive the retrieved value.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "value".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and and no larger than ("value".length -  offset)
     *
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    ): Int

    /**
     * Get the value associated with the specified key within column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the key to retrieve the value.
     * @param value the out-value to receive the retrieved value.
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        value: ByteArray
    ): Int

    /**
     * Get the value associated with the specified key within column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the key to retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * an no larger than ("key".length -  offset)
     * @param value the out-value to receive the retrieved value.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     *
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        offset: Int, len: Int, value: ByteArray, vOffset: Int,
        vLen: Int
    ): Int

    /**
     * Get the value associated with the specified key.
     *
     * @param opt [ReadOptions] instance.
     * @param key the key to retrieve the value.
     * @param value the out-value to receive the retrieved value.
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        opt: ReadOptions, key: ByteArray,
        value: ByteArray
    ): Int

    /**
     * Get the value associated with the specified key.
     *
     * @param opt [ReadOptions] instance.
     * @param key the key to retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param value the out-value to receive the retrieved value.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, must be
     * non-negative and no larger than ("value".length -  offset)
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        opt: ReadOptions, key: ByteArray, offset: Int,
        len: Int, value: ByteArray, vOffset: Int, vLen: Int
    ): Int

    /**
     * Get the value associated with the specified key within column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param opt [ReadOptions] instance.
     * @param key the key to retrieve the value.
     * @param value the out-value to receive the retrieved value.
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions, key: ByteArray, value: ByteArray
    ): Int

    /**
     * Get the value associated with the specified key within column family.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param opt [ReadOptions] instance.
     * @param key the key to retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be
     * non-negative and and no larger than ("key".length -  offset)
     * @param value the out-value to receive the retrieved value.
     * @param vOffset the offset of the "value" array to be used, must be
     * non-negative and no longer than "key".length
     * @param vLen the length of the "value" array to be used, and must be
     * non-negative and no larger than ("value".length -  offset)
     * @return The size of the actual value that matches the specified
     * `key` in byte.  If the return value is greater than the
     * length of `value`, then it indicates that the size of the
     * input buffer `value` is insufficient and partial result will
     * be returned.  RocksDB.NOT_FOUND will be returned if the value not
     * found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions, key: ByteArray, offset: Int, len: Int,
        value: ByteArray, vOffset: Int, vLen: Int
    ): Int

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param key the key retrieve the value.
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    operator fun get(key: ByteArray): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param key the key retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        key: ByteArray, offset: Int,
        len: Int
    ): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the key retrieve the value.
     * @return a byte array storing the value associated with the input key if
     * any.  null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray
    ): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the key retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, offset: Int, len: Int
    ): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param key the key retrieve the value.
     * @param opt Read options.
     * @return a byte array storing the value associated with the input key if
     * any.  null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(opt: ReadOptions, key: ByteArray): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param key the key retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param opt Read options.
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        opt: ReadOptions, key: ByteArray, offset: Int,
        len: Int
    ): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the key retrieve the value.
     * @param opt Read options.
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions, key: ByteArray
    ): ByteArray?

    /**
     * The simplified version of get which returns a new byte array storing
     * the value associated with the specified input key if any.  null will be
     * returned if the specified key is not found.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle]
     * instance
     * @param key the key retrieve the value.
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than ("key".length -  offset)
     * @param opt Read options.
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun get(
        columnFamilyHandle: ColumnFamilyHandle,
        opt: ReadOptions, key: ByteArray, offset: Int, len: Int
    ): ByteArray?


    /**
     * Takes a list of keys, and returns a list of values for the given list of
     * keys. List will contain null for keys which could not be found.
     *
     * @param keys List of keys for which values need to be retrieved.
     * @return List of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun multiGetAsList(keys: List<ByteArray>): List<ByteArray>

    /**
     * Returns a list of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * Note: Every key needs to have a related column family name in
     * `columnFamilyHandleList`.
     *
     * @param columnFamilyHandleList [List] containing
     * [maryk.rocksdb.ColumnFamilyHandle] instances.
     * @param keys List of keys for which values need to be retrieved.
     * @return List of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IllegalArgumentException thrown if the size of passed keys is not
     * equal to the amount of passed column family handles.
     */
    fun multiGetAsList(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray>

    /**
     * Returns a list of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * @param opt Read options.
     * @param keys of keys for which values need to be retrieved.
     * @return List of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun multiGetAsList(
        opt: ReadOptions,
        keys: List<ByteArray>
    ): List<ByteArray>

    /**
     * Returns a list of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * Note: Every key needs to have a related column family name in
     * `columnFamilyHandleList`.
     *
     * @param opt Read options.
     * @param columnFamilyHandleList [List] containing
     * [maryk.rocksdb.ColumnFamilyHandle] instances.
     * @param keys of keys for which values need to be retrieved.
     * @return List of values for the given list of keys. List will contain
     * null for keys which could not be found.
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     * @throws IllegalArgumentException thrown if the size of passed keys is not
     * equal to the amount of passed column family handles.
     */
    fun multiGetAsList(
        opt: ReadOptions,
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        keys: List<ByteArray>
    ): List<ByteArray>

    /**
     * If the [key] definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param key byte array of a key to search for
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(key: ByteArray, value: StringBuilder): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param key byte array of a key to search for
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than "key".length
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     *
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        key: ByteArray, offset: Int, len: Int,
        value: StringBuilder
    ): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key byte array of a key to search for
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, value: StringBuilder
    ): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key byte array of a key to search for
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than "key".length
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        columnFamilyHandle: ColumnFamilyHandle,
        key: ByteArray, offset: Int, len: Int, value: StringBuilder
    ): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param readOptions [ReadOptions] instance
     * @param key byte array of a key to search for
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray, value: StringBuilder
    ): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param readOptions [ReadOptions] instance
     * @param key byte array of a key to search for
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than "key".length
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        readOptions: ReadOptions,
        key: ByteArray, offset: Int, len: Int,
        value: StringBuilder
    ): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param readOptions [ReadOptions] instance
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key byte array of a key to search for
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        value: StringBuilder
    ): Boolean

    /**
     * If the key definitely does not exist in the database, then this method
     * returns false, else true.
     *
     * This check is potentially lighter-weight than invoking DB::Get(). One way
     * to make this lighter weight is to avoid doing any IOs.
     *
     * @param readOptions [ReadOptions] instance
     * @param columnFamilyHandle [ColumnFamilyHandle] instance
     * @param key byte array of a key to search for
     * @param offset the offset of the "key" array to be used, must be
     * non-negative and no larger than "key".length
     * @param len the length of the "key" array to be used, must be non-negative
     * and no larger than "key".length
     * @param value StringBuilder instance which is a out parameter if a value is
     * found in block-cache.
     * @return boolean value indicating if key does not exist or might exist.
     */
    fun keyMayExist(
        readOptions: ReadOptions,
        columnFamilyHandle: ColumnFamilyHandle, key: ByteArray,
        offset: Int, len: Int, value: StringBuilder
    ): Boolean

    /**
     * Return a heap-allocated iterator over the contents of the
     * database. The result of newIterator() is initially invalid
     * (caller must call one of the Seek methods on the iterator
     * before using it).
     *
     * Caller should close the iterator when it is no longer needed.
     * The returned iterator should be closed before this db is closed.
     *
     * @return instance of iterator object.
     */
    fun newIterator(): RocksIterator

    /**
     * Return a heap-allocated iterator over the contents of the
     * database. The result of newIterator() is initially invalid
     * (caller must call one of the Seek methods on the iterator
     * before using it).
     *
     * Caller should close the iterator when it is no longer needed.
     * The returned iterator should be closed before this db is closed.
     *
     * @param readOptions [ReadOptions] instance.
     * @return instance of iterator object.
     */
    fun newIterator(readOptions: ReadOptions): RocksIterator

    /**
     * Return a heap-allocated iterator over the contents of the
     * database. The result of newIterator() is initially invalid
     * (caller must call one of the Seek methods on the iterator
     * before using it).
     *
     * Caller should close the iterator when it is no longer needed.
     * The returned iterator should be closed before this db is closed.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @return instance of iterator object.
     */
    fun newIterator(
        columnFamilyHandle: ColumnFamilyHandle
    ): RocksIterator

    /**
     * Return a heap-allocated iterator over the contents of the
     * database. The result of newIterator() is initially invalid
     * (caller must call one of the Seek methods on the iterator
     * before using it).
     *
     * Caller should close the iterator when it is no longer needed.
     * The returned iterator should be closed before this db is closed.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance
     * @param readOptions [ReadOptions] instance.
     * @return instance of iterator object.
     */
    fun newIterator(
        columnFamilyHandle: ColumnFamilyHandle,
        readOptions: ReadOptions
    ): RocksIterator

    /**
     * Returns iterators from a consistent database state across multiple
     * column families. Iterators are heap allocated and need to be deleted
     * before the db is deleted
     *
     * @param columnFamilyHandleList [java.util.List] containing
     * [org.rocksdb.ColumnFamilyHandle] instances.
     * @return [java.util.List] containing [org.rocksdb.RocksIterator]
     * instances
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun newIterators(
        columnFamilyHandleList: List<ColumnFamilyHandle>
    ): List<RocksIterator>

    /**
     * Returns iterators from a consistent database state across multiple
     * column families. Iterators are heap allocated and need to be deleted
     * before the db is deleted
     *
     * @param columnFamilyHandleList [java.util.List] containing
     * [org.rocksdb.ColumnFamilyHandle] instances.
     * @param readOptions [ReadOptions] instance.
     * @return [java.util.List] containing [org.rocksdb.RocksIterator]
     * instances
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun newIterators(
        columnFamilyHandleList: List<ColumnFamilyHandle>,
        readOptions: ReadOptions
    ): List<RocksIterator>

    /**
     * Return a handle to the current DB state. Iterators created with
     * this handle will all observe a stable snapshot of the current DB
     * state. The caller must call ReleaseSnapshot(result) when the
     * snapshot is no longer needed.
     *
     * nullptr will be returned if the DB fails to take a snapshot or does
     * not support snapshot.
     *
     * @return Snapshot [Snapshot] instance
     */
    fun getSnapshot(): Snapshot?

    /**
     * Release a previously acquired snapshot.
     *
     * The caller must not use "snapshot" after this call.
     *
     * @param snapshot [Snapshot] instance
     */
    fun releaseSnapshot(snapshot: Snapshot?)

    /**
     * DB implements can export properties about their state
     * via this method on a per column family level.
     *
     * If `property` is a valid property understood by this DB
     * implementation, fills `value` with its current value and
     * returns true. Otherwise returns false.
     *
     * Valid property names include:
     *
     *  - "rocksdb.num-files-at-level&lt;N&gt;" - return the number of files at
     *    level &lt;N&gt;, where &lt;N&gt; is an ASCII representation of a level
     *    number (e.g. "0").
     *  - "rocksdb.stats" - returns a multi-line string that describes statistics
     *    about the internal operation of the DB.
     *  - "rocksdb.sstables" - returns a multi-line string that describes all
     *    of the sstables that make up the db contents.
     *
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family.
     * @param property to be fetched. See above for examples
     * @return property value
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun getProperty(
        columnFamilyHandle: ColumnFamilyHandle?,
        property: String
    ): String

    /**
     * DB implementations can export properties about their state
     * via this method.  If "property" is a valid property understood by this
     * DB implementation, fills "*value" with its current value and returns
     * true.  Otherwise returns false.
     *
     * Valid property names include:
     *
     *  - "rocksdb.num-files-at-level<N>" - return the number of files at
     *    level <N>, where <N> is an ASCII representation of a level
     *    number (e.g. "0").
     *  - "rocksdb.stats" - returns a multi-line string that describes statistics
     *    about the internal operation of the DB.
     *  - "rocksdb.sstables" - returns a multi-line string that describes all
     *    of the sstables that make up the db contents.
     *
     * @param property to be fetched. See above for examples
     * @return property value
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun getProperty(property: String): String

    /**
     * Gets a property map.
     *
     * @param property to be fetched.
     *
     * @return the property map
     *
     * @throws RocksDBException if an error happens in the underlying native code.
     */
    fun getMapProperty(property: String): Map<String, String>

    /**
     * Gets a property map.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family.
     * @param property to be fetched.
     *
     * @return the property map
     *
     * @throws RocksDBException if an error happens in the underlying native code.
     */
    fun getMapProperty(
        columnFamilyHandle: ColumnFamilyHandle?,
        property: String
    ): Map<String, String>

    /**
     * Similar to GetProperty(), but only works for a subset of properties
     * whose return value is a numerical value. Return the value as long.
     *
     * The value should be treated as unsigned long using provided methods
     * of type [Long].
     *
     * @param property to be fetched.
     *
     * @return numerical property value.
     *
     * @throws RocksDBException if an error happens in the underlying native code.
     */
    fun getLongProperty(property: String): Long

    /**
     * Similar to GetProperty(), but only works for a subset of properties
     * whose return value is a numerical value. Return the value as long.
     *
     * The value should be treated as unsigned long using provided methods
     * of type [Long].
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family
     * @param property to be fetched.
     *
     * @return numerical property value
     *
     * @throws RocksDBException if an error happens in the underlying native code.
     */
    fun getLongProperty(
        columnFamilyHandle: ColumnFamilyHandle?,
        property: String
    ) : Long

    /**
     * Reset internal stats for DB and all column families.
     *
     * Note this doesn't reset [Options.statistics] as it is not
     * owned by DB.
     */
    fun resetStats()

    /**
     * Return sum of the getLongProperty of all the column families
     *
     * The value should be treated as unsigned long using provided methods
     * of type [Long].
     *
     * @param property to be fetched.
     *
     * @return numerical property value
     *
     * @throws RocksDBException if an error happens in the underlying native code.
     */
    fun getAggregatedLongProperty(property: String): Long

    /**
     * Get the approximate file system space used by keys in each range.
     *
     * Note that the returned sizes measure file system space usage, so
     * if the user data compresses by a factor of ten, the returned
     * sizes will be one-tenth the size of the corresponding user data size.
     *
     * If `sizeApproximationFlags` defines whether the returned size
     * should include the recently written data in the mem-tables (if
     * the mem-table type supports it), data serialized to disk, or both.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family
     * @param ranges the ranges over which to approximate sizes
     * @param sizeApproximationFlags flags to determine what to include in the
     * approximation.
     *
     * @return the sizes
     */
    fun getApproximateSizes(
        columnFamilyHandle: ColumnFamilyHandle?,
        ranges: List<Range>,
        vararg sizeApproximationFlags: SizeApproximationFlag
    ): LongArray

    /**
     * Get the approximate file system space used by keys in each range for
     * the default column family.
     *
     * Note that the returned sizes measure file system space usage, so
     * if the user data compresses by a factor of ten, the returned
     * sizes will be one-tenth the size of the corresponding user data size.
     *
     * If `sizeApproximationFlags` defines whether the returned size
     * should include the recently written data in the mem-tables (if
     * the mem-table type supports it), data serialized to disk, or both.
     *
     * @param ranges the ranges over which to approximate sizes
     * @param sizeApproximationFlags flags to determine what to include in the
     * approximation.
     *
     * @return the sizes.
     */
    fun getApproximateSizes(
        ranges: List<Range>,
        vararg sizeApproximationFlags: SizeApproximationFlag
    ): LongArray

    /**
     * This method is similar to [.getApproximateSizes],
     * except that it returns approximate number of records and size in memtables.
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family
     * @param range the ranges over which to get the memtable stats
     *
     * @return the count and size for the range
     */
    fun getApproximateMemTableStats(
        columnFamilyHandle: ColumnFamilyHandle?,
        range: Range
    ): CountAndSize

    /**
     * This method is similar to [.getApproximateSizes],
     * except that it returns approximate number of records and size in memtables.
     *
     * @param range the ranges over which to get the memtable stats
     *
     * @return the count and size for the range
     */
    fun getApproximateMemTableStats(
        range: Range
    ): CountAndSize

    /**
     * Range compaction of database.
     *
     * **Note**: After the database has been compacted,
     * all data will have been pushed down to the last level containing
     * any data.
     *
     * **See also**
     *  - [.compactRange]
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactRange()

    /**
     * Range compaction of column family.
     *
     * **Note**: After the database has been compacted,
     * all data will have been pushed down to the last level containing
     * any data.
     *
     *
     * **See also**
     * - [.compactRange]
     *
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family.
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle?
    )

    /**
     * Range compaction of database.
     *
     * **Note**: After the database has been compacted,
     * all data will have been pushed down to the last level containing
     * any data.
     *
     * **See also**
     *  - [.compactRange]
     *
     * @param begin start of key range (included in range)
     * @param end end of key range (excluded from range)
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactRange(begin: ByteArray, end: ByteArray)

    /**
     * Range compaction of column family.
     *
     * **Note**: After the database has been compacted,
     * all data will have been pushed down to the last level containing
     * any data.
     *
     * **See also**
     * [.compactRange]
     *
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family.
     * @param begin start of key range (included in range)
     * @param end end of key range (excluded from range)
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle?,
        begin: ByteArray?, end: ByteArray?
    )

    /**
     *
     * Range compaction of column family.
     *
     * **Note**: After the database has been compacted,
     * all data will have been pushed down to the last level containing
     * any data.
     *
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle] instance.
     * @param begin start of key range (included in range)
     * @param end end of key range (excluded from range)
     * @param compactRangeOptions options for the compaction
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactRange(
        columnFamilyHandle: ColumnFamilyHandle,
        begin: ByteArray?, end: ByteArray?,
        compactRangeOptions: CompactRangeOptions
    )

    /**
     * Change the options for the column family handle.
     *
     * @param columnFamilyHandle [org.rocksdb.ColumnFamilyHandle]
     * instance, or null for the default column family.
     * @param mutableColumnFamilyOptions the options.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun setOptions(
        columnFamilyHandle: ColumnFamilyHandle,
        mutableColumnFamilyOptions: MutableColumnFamilyOptions
    )

    /**
     * Change the options for the default column family handle.
     *
     * @param mutableColumnFamilyOptions the options.
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun setOptions(
        mutableColumnFamilyOptions: MutableColumnFamilyOptions
    )

    /**
     * Set the options for the column family handle.
     *
     * @param mutableDBoptions the options.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun setDBOptions(mutableDBoptions: MutableDBOptions)

    /**
     * Takes a list of files specified by file names and
     * compacts them to the specified level.
     *
     * Note that the behavior is different from
     * [.compactRange]
     * in that CompactFiles() performs the compaction job using the CURRENT
     * thread.
     *
     * @param compactionOptions compaction options
     * @param inputFileNames the name of the files to compact
     * @param outputLevel the level to which they should be compacted
     * @param outputPathId the id of the output path, or -1
     * @param compactionJobInfo the compaction job info, this parameter
     * will be updated with the info from compacting the files,
     * can just be null if you don't need it.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactFiles(
        compactionOptions: CompactionOptions,
        inputFileNames: List<String>,
        outputLevel: Int,
        outputPathId: Int,
        compactionJobInfo: CompactionJobInfo?
    ): List<String>

    /**
     * Takes a list of files specified by file names and
     * compacts them to the specified level.
     *
     * Note that the behavior is different from
     * [.compactRange]
     * in that CompactFiles() performs the compaction job using the CURRENT
     * thread.
     *
     * @param compactionOptions compaction options
     * @param columnFamilyHandle columnFamilyHandle, or null for the
     * default column family
     * @param inputFileNames the name of the files to compact
     * @param outputLevel the level to which they should be compacted
     * @param outputPathId the id of the output path, or -1
     * @param compactionJobInfo the compaction job info, this parameter
     * will be updated with the info from compacting the files,
     * can just be null if you don't need it.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun compactFiles(
        compactionOptions: CompactionOptions,
        /* @Nullable */ columnFamilyHandle: ColumnFamilyHandle?,
        inputFileNames: List<String>,
        outputLevel: Int,
        outputPathId: Int,
        /* @Nullable */ compactionJobInfo: CompactionJobInfo?
    ): List<String>

    /**
     * This function will wait until all currently running background processes
     * finish. After it returns, no background process will be run until
     * [.continueBackgroundWork] is called
     *
     * @throws RocksDBException If an error occurs when pausing background work
     */
    fun pauseBackgroundWork()

    /**
     * Resumes background work which was suspended by
     * previously calling [.pauseBackgroundWork]
     *
     * @throws RocksDBException If an error occurs when resuming background work
     */
    fun continueBackgroundWork()

    /**
     * Enable automatic compactions for the given column
     * families if they were previously disabled.
     *
     * The function will first set the
     * [ColumnFamilyOptions.disableAutoCompactions] option for each
     * column family to false, after which it will schedule a flush/compaction.
     *
     * NOTE: Setting disableAutoCompactions to 'false' through
     * [.setOptions]
     * does NOT schedule a flush/compaction afterwards, and only changes the
     * parameter itself within the column family option.
     *
     * @param columnFamilyHandles the column family handles
     *
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun enableAutoCompaction(
        columnFamilyHandles: List<ColumnFamilyHandle>
    )

    /**
     * Number of levels used for this DB.
     *
     * @return the number of levels
     */
    fun numberLevels(): Int

    /**
     * Number of levels used for a column family in this DB.
     *
     * @param columnFamilyHandle the column family handle, or null
     * for the default column family
     *
     * @return the number of levels
     */
    fun numberLevels(columnFamilyHandle: ColumnFamilyHandle?): Int

    /**
     * Maximum level to which a new compacted memtable is pushed if it
     * does not create overlap.
     */
    fun maxMemCompactionLevel(): Int

    /**
     * Maximum level to which a new compacted memtable is pushed if it
     * does not create overlap.
     *
     * @param columnFamilyHandle the column family handle
     */
    fun maxMemCompactionLevel(
        /* @Nullable */columnFamilyHandle: ColumnFamilyHandle?
    ): Int

    /**
     * Number of files in level-0 that would stop writes.
     */
    fun level0StopWriteTrigger(): Int

    /**
     * Number of files in level-0 that would stop writes.
     *
     * @param columnFamilyHandle the column family handle
     */
    fun level0StopWriteTrigger(
        columnFamilyHandle: ColumnFamilyHandle?
    ): Int

    /**
     * Get DB name -- the exact same name that was provided as an argument to
     * as path to [.open].
     *
     * @return the DB name
     */
    fun getName(): String

    /**
     * Get the Env object from the DB
     *
     * @return the env
     */
    fun getEnv(): Env

    /**
     *
     * Flush all memory table data.
     *
     *
     * Note: it must be ensured that the FlushOptions instance
     * is not GC'ed before this method finishes. If the wait parameter is
     * set to false, flush processing is asynchronous.
     *
     * @param flushOptions [maryk.rocksdb.FlushOptions] instance.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun flush(flushOptions: FlushOptions)

    /**
     * Flush all memory table data.
     *
     * Note: it must be ensured that the FlushOptions instance
     * is not GC'ed before this method finishes. If the wait parameter is
     * set to false, flush processing is asynchronous.
     *
     * @param flushOptions [maryk.rocksdb.FlushOptions] instance.
     * @param columnFamilyHandle [maryk.rocksdb.ColumnFamilyHandle] instance.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun flush(
        flushOptions: FlushOptions,
        columnFamilyHandle: ColumnFamilyHandle?
    )

    /**
     * Flushes multiple column families.
     *
     * If atomic flush is not enabled, this is equivalent to calling
     * [.flush] multiple times.
     *
     * If atomic flush is enabled, this will flush all column families
     * specified up to the latest sequence number at the time when flush is
     * requested.
     *
     * @param flushOptions [maryk.rocksdb.FlushOptions] instance.
     * @param columnFamilyHandles column family handles.
     * @throws RocksDBException thrown if an error occurs within the native
     * part of the library.
     */
    fun flush(
        flushOptions: FlushOptions,
        /* @Nullable */ columnFamilyHandles: List<ColumnFamilyHandle>?
    )

    /**
     * Flush the WAL memory buffer to the file. If `sync` is true,
     * it calls [.syncWal] afterwards.
     *
     * @param sync true to also fsync to disk.
     */
    fun flushWal(sync: Boolean)

    /**
     * Sync the WAL.
     *
     * Note that [.write] followed by
     * [.syncWal] is not exactly the same as
     * [.write] with
     * [WriteOptions.sync] set to true; In the latter case the changes
     * won't be visible until the sync is done.
     *
     * Currently only works if [Options.allowMmapWrites] is set to false.
     */
    fun syncWal()

    /**
     * The sequence number of the most recent transaction.
     *
     * @return sequence number of the most
     * recent transaction.
     */
    fun getLatestSequenceNumber(): Long

    /**
     * Instructs DB to preserve deletes with sequence numbers &gt;= sequenceNumber.
     *
     * Has no effect if DBOptions#preserveDeletes() is set to false.
     *
     * This function assumes that user calls this function with monotonically
     * increasing seqnums (otherwise we can't guarantee that a particular delete
     * hasn't been already processed).
     *
     * @param sequenceNumber the minimum sequence number to preserve
     *
     * @return true if the value was successfully updated,
     * false if user attempted to call if with
     * sequenceNumber &lt;= current value.
     */
    fun setPreserveDeletesSequenceNumber(sequenceNumber: Long): Boolean

    /**
     *
     * Prevent file deletions. Compactions will continue to occur,
     * but no obsolete files will be deleted. Calling this multiple
     * times have the same effect as calling it once.
     *
     * @throws RocksDBException thrown if operation was not performed
     * successfully.
     */
    fun disableFileDeletions()

    /**
     *
     * Allow compactions to delete obsolete files.
     * If force == true, the call to EnableFileDeletions()
     * will guarantee that file deletions are enabled after
     * the call, even if DisableFileDeletions() was called
     * multiple times before.
     *
     *
     * If force == false, EnableFileDeletions will only
     * enable file deletion after it's been called at least
     * as many times as DisableFileDeletions(), enabling
     * the two methods to be called by two threads
     * concurrently without synchronization
     * -- i.e., file deletions will be enabled only after both
     * threads call EnableFileDeletions()
     *
     * @param force boolean value described above.
     *
     * @throws RocksDBException thrown if operation was not performed
     * successfully.
     */
    fun enableFileDeletions(force: Boolean)

    /**
     * Retrieve the list of all files in the database after flushing the memtable.
     *
     * See [.getLiveFiles].
     *
     * @return the live files
     */
    fun getLiveFiles(): LiveFiles

    /**
     * Retrieve the list of all files in the database.
     *
     * In case you have multiple column families, even if `flushMemtable`
     * is true, you still need to call [.getSortedWalFiles]
     * after [.getLiveFiles] to compensate for new data that
     * arrived to already-flushed column families while other column families
     * were flushing.
     *
     * NOTE: Calling [.getLiveFiles] followed by
     * [.getSortedWalFiles] can generate a lossless backup.
     *
     * @param flushMemtable set to true to flush before recoding the live
     * files. Setting to false is useful when we don't want to wait for flush
     * which may have to wait for compaction to complete taking an
     * indeterminate time.
     *
     * @return the live files
     */
    fun getLiveFiles(flushMemtable: Boolean): LiveFiles

    /**
     * Retrieve the sorted list of all wal files with earliest file first.
     *
     * @return the log files
     */
    fun getSortedWalFiles(): List<LogFile>

    /**
     *
     * Returns an iterator that is positioned at a write-batch containing
     * seq_number. If the sequence number is non existent, it returns an iterator
     * at the first available seq_no after the requested seq_no.
     *
     *
     * Must set WAL_ttl_seconds or WAL_size_limit_MB to large values to
     * use this api, else the WAL files will get
     * cleared aggressively and the iterator might keep getting invalid before
     * an update is read.
     *
     * @param sequenceNumber sequence number offset
     *
     * @return [maryk.rocksdb.TransactionLogIterator] instance.
     *
     * @throws RocksDBException if iterator cannot be retrieved
     * from native-side.
     */
    fun getUpdatesSince(sequenceNumber: Long): TransactionLogIterator

    /**
     * Delete the file name from the db directory and update the internal state to
     * reflect that. Supports deletion of sst and log files only. 'name' must be
     * path relative to the db directory. eg. 000001.sst, /archive/000003.log
     *
     * @param name the file name
     */
    fun deleteFile(name: String)

    /**
     * Gets a list of all table files metadata.
     *
     * @return table files metadata.
     */
    fun getLiveFilesMetaData(): List<LiveFileMetaData>

    /**
     * Obtains the meta data of the specified column family of the DB.
     *
     * @param columnFamilyHandle the column family
     *
     * @return the column family metadata
     */
    fun getColumnFamilyMetaData(
        columnFamilyHandle: ColumnFamilyHandle?
    ): ColumnFamilyMetaData

    /**
     * Obtains the meta data of the default column family of the DB.
     *
     * @return the column family metadata
     */
    fun GetColumnFamilyMetaData(): ColumnFamilyMetaData

    /**
     * ingestExternalFile will load a list of external SST files (1) into the DB
     * We will try to find the lowest possible level that the file can fit in, and
     * ingest the file into this level (2). A file that have a key range that
     * overlap with the memtable key range will require us to Flush the memtable
     * first before ingesting the file.
     *
     * (1) External SST files can be created using [SstFileWriter]
     * (2) We will try to ingest the files to the lowest possible level
     * even if the file compression doesn't match the level compression
     *
     * @param filePathList The list of files to ingest
     * @param ingestExternalFileOptions the options for the ingestion
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun ingestExternalFile(
        filePathList: List<String>,
        ingestExternalFileOptions: IngestExternalFileOptions
    )

    /**
     * ingestExternalFile will load a list of external SST files (1) into the DB
     * We will try to find the lowest possible level that the file can fit in, and
     * ingest the file into this level (2). A file that have a key range that
     * overlap with the memtable key range will require us to Flush the memtable
     * first before ingesting the file.
     *
     * (1) External SST files can be created using [SstFileWriter]
     * (2) We will try to ingest the files to the lowest possible level
     * even if the file compression doesn't match the level compression
     *
     * @param columnFamilyHandle The column family for the ingested files
     * @param filePathList The list of files to ingest
     * @param ingestExternalFileOptions the options for the ingestion
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun ingestExternalFile(
        columnFamilyHandle: ColumnFamilyHandle,
        filePathList: List<String>,
        ingestExternalFileOptions: IngestExternalFileOptions
    )

    /**
     * Verify checksum
     *
     * @throws RocksDBException if the checksum is not valid
     */
    fun verifyChecksum()

    /**
     * Gets the handle for the default column family
     * @return The handle of the default column family
     */
    fun getDefaultColumnFamily(): ColumnFamilyHandle

    /**
     * Get the properties of all tables.
     *
     * @param columnFamilyHandle the column family handle, or null for the default
     * column family.
     *
     * @return the properties
     */
    fun getPropertiesOfAllTables(
        columnFamilyHandle: ColumnFamilyHandle?
    ): Map<String, TableProperties>

    /**
     * Get the properties of all tables in the default column family.
     *
     * @return the properties
     */
    fun getPropertiesOfAllTables(): Map<String, TableProperties>

    /**
     * Get the properties of tables in range.
     *
     * @param columnFamilyHandle the column family handle, or null for the default
     * column family.
     * @param ranges the ranges over which to get the table properties
     *
     * @return the properties
     */
    fun getPropertiesOfTablesInRange(
        columnFamilyHandle: ColumnFamilyHandle?,
        ranges: List<Range>
    ): Map<String, TableProperties>

    /**
     * Get the properties of tables in range for the default column family.
     *
     * @param ranges the ranges over which to get the table properties
     *
     * @return the properties
     */
    fun getPropertiesOfTablesInRange(
        ranges: List<Range>
    ): Map<String, TableProperties>

    /**
     * Suggest the range to compact.
     *
     * @param columnFamilyHandle the column family handle, or null for the default
     * column family.
     *
     * @return the suggested range.
     */
    fun suggestCompactRange(
        /* @Nullable */columnFamilyHandle: ColumnFamilyHandle?
    ): Range

    /**
     * Suggest the range to compact for the default column family.
     *
     * @return the suggested range.
     */
    fun suggestCompactRange(): Range

    /**
     * Promote L0.
     *
     * @param columnFamilyHandle the column family handle,
     * or null for the default column family.
     */
    fun promoteL0(
        columnFamilyHandle: ColumnFamilyHandle?,
        targetLevel: Int
    )

    /** Promote L0 for the default column family. */
    fun promoteL0(targetLevel: Int)

    /**
     * Trace DB operations.
     *
     * Use [.endTrace] to stop tracing.
     *
     * @param traceOptions the options
     * @param traceWriter the trace writer
     */
    fun startTrace(
        traceOptions: TraceOptions,
        traceWriter: AbstractTraceWriter
    )

    /**
     * Stop tracing DB operations.
     *
     * See [.startTrace]
     */
    fun endTrace()
}


/**
 * Static method to destroy the contents of the specified database.
 * Be very careful using this method.
 *
 * @param path the path to the Rocksdb database.
 * @param options [org.rocksdb.Options] instance.
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 */
expect fun destroyRocksDB(path: String, options: Options)

/**
 * Function to determine all available column families for a
 * RocksDB database identified by path
 *
 * @param options Options for opening the database
 * @param path Absolute path to RocksDB database
 * @return List containing the column family names
 *
 * @throws RocksDBException thrown if error happens in underlying
 * native library.
 */
expect fun listColumnFamilies(
    options: Options,
    path: String
): List<ByteArray>
