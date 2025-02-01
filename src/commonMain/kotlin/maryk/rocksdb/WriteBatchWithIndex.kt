@file:Suppress("unused")
package maryk.rocksdb

/**
 * Similar to [WriteBatch] but with a binary searchable
 * index built for all the keys inserted.
 *
 * Calling `put`, `merge`, `remove`, or `putLogData` invokes the same function
 * as with [WriteBatch] while also building an index.
 *
 * Users can call [newIterator] to create an iterator over the write batch or
 * [newIteratorWithBase] to get an iterator for the database with
 * Read-Your-Own-Writes-like capability.
 */
expect class WriteBatchWithIndex : AbstractWriteBatch {

    /**
     * Creates a [WriteBatchWithIndex] where no bytes
     * are reserved up-front, byte-wise comparison is
     * used for fallback key comparisons,
     * and duplicate keys operations are retained.
     */
    constructor()

    /**
     * Creates a [WriteBatchWithIndex] where no bytes
     * are reserved up-front, byte-wise comparison is
     * used for fallback key comparisons, and duplicate key
     * assignment is determined by the constructor argument.
     *
     * @param overwriteKey If `true`, overwrite the key in the index when
     * inserting a duplicate key, ensuring that an iterator will never
     * show two entries with the same key.
     */
    constructor(overwriteKey: Boolean)

    /**
     * Creates a [WriteBatchWithIndex].
     *
     * @param fallbackIndexComparator Fallback comparator to compare keys within a column family
     *                                if the column family and its comparator cannot be determined.
     * @param reservedBytes          Reserved bytes in the underlying [WriteBatch].
     * @param overwriteKey           If `true`, overwrite the key in the index when
     *                                inserting a duplicate key, ensuring that an iterator will never
     *                                show two entries with the same key.
     */
    constructor(
        fallbackIndexComparator: AbstractComparator,
        reservedBytes: Int,
        overwriteKey: Boolean
    )
//
//    /**
//     * Creates an iterator for a specific column family.
//     *
//     * Users can call [RocksIteratorInterface.seek] to search to the next entry of or after a key.
//     * Keys will be iterated in the order given by the index comparator. For multiple updates
//     * on the same key, each update will be returned as a separate entry, in the order of update time.
//     *
//     * @param columnFamilyHandle The column family to iterate over.
//     * @return An iterator for the write batch contents, restricted to the specified column family.
//     */
//    fun newIterator(columnFamilyHandle: ColumnFamilyHandle): WBWIRocksIterator
//
//    /**
//     * Creates an iterator for the default column family.
//     *
//     * Users can call [RocksIteratorInterface.seek] to search to the next entry of or after a key.
//     * Keys will be iterated in the order given by the index comparator. For multiple updates
//     * on the same key, each update will be returned as a separate entry, in the order of update time.
//     *
//     * @return An iterator for the write batch contents.
//     */
//    fun newIterator(): WBWIRocksIterator

    /**
     * Provides Read-Your-Own-Writes-like functionality by
     * creating a new iterator that uses [WBWIRocksIterator]
     * as a delta and [baseIterator] as a base.
     *
     * **Note:** Updating the write batch with the current key of the iterator is not safe.
     * It will invalidate the current `key()` and `value()` of the iterator, even before
     * the write batch update finishes. The state may recover after `next()` is called.
     *
     * @param columnFamilyHandle The column family to iterate over.
     * @param baseIterator       The base iterator, e.g., [RocksDB.newIterator].
     * @return An iterator showing a view comprised of both the database
     * point-in-time from [baseIterator] and modifications made in this write batch.
     */
    fun newIteratorWithBase(
        columnFamilyHandle: ColumnFamilyHandle,
        baseIterator: RocksIterator
    ): RocksIterator

//    /**
//     * Provides Read-Your-Own-Writes-like functionality by
//     * creating a new iterator that uses [WBWIRocksIterator]
//     * as a delta and [baseIterator] as a base, with optional read options.
//     *
//     * **Note:** Updating the write batch with the current key of the iterator is not safe.
//     * It will invalidate the current `key()` and `value()` of the iterator, even before
//     * the write batch update finishes. The state may recover after `next()` is called.
//     *
//     * @param columnFamilyHandle The column family to iterate over.
//     * @param baseIterator       The base iterator, e.g., [RocksDB.newIterator].
//     * @param readOptions        The read options, or `null`.
//     * @return An iterator showing a view comprised of both the database
//     * point-in-time from [baseIterator] and modifications made in this write batch.
//     */
//    fun newIteratorWithBase(
//        columnFamilyHandle: ColumnFamilyHandle,
//        baseIterator: RocksIterator,
//        readOptions: ReadOptions?
//    ): RocksIterator

    /**
     * Provides Read-Your-Own-Writes-like functionality by
     * creating a new iterator that uses [WBWIRocksIterator]
     * as a delta and [baseIterator] as a base, operating on the default column family.
     *
     * @param baseIterator The base iterator, e.g., [RocksDB.newIterator].
     * @return An iterator showing a view comprised of both the database
     * point-in-time from [baseIterator] and modifications made in this write batch.
     */
    fun newIteratorWithBase(baseIterator: RocksIterator): RocksIterator

//    /**
//     * Provides Read-Your-Own-Writes-like functionality by
//     * creating a new iterator that uses [WBWIRocksIterator]
//     * as a delta and [baseIterator] as a base, operating on the default column family,
//     * with optional read options.
//     *
//     * @param baseIterator The base iterator, e.g., [RocksDB.newIterator].
//     * @param readOptions  The read options, or `null`.
//     * @return An iterator showing a view comprised of both the database
//     * point-in-time from [baseIterator] and modifications made in this write batch.
//     */
//    fun newIteratorWithBase(
//        baseIterator: RocksIterator,
//        readOptions: ReadOptions?
//    ): RocksIterator

    /**
     * Similar to [RocksDB.get] but will only read the key from this batch.
     *
     * @param columnFamilyHandle The column family to retrieve the value from.
     * @param options            The database options to use.
     * @param key                The key to read the value for.
     * @return A byte array storing the value associated with the input key if any,
     *         or `null` if the specified key is not found.
     * @throws RocksDBException If the batch does not have enough data to resolve
     *                           merge operations or in the case of an unexpected error.
     */
    fun getFromBatch(
        columnFamilyHandle: ColumnFamilyHandle,
        options: DBOptions,
        key: ByteArray
    ): ByteArray?

    /**
     * Similar to [RocksDB.get] but will only read the key from this batch.
     *
     * @param options The database options to use.
     * @param key     The key to read the value for.
     * @return A byte array storing the value associated with the input key if any,
     *         or `null` if the specified key is not found.
     * @throws RocksDBException If the batch does not have enough data to resolve
     *                           merge operations or in the case of an unexpected error.
     */
    fun getFromBatch(options: DBOptions, key: ByteArray): ByteArray?
//
//    /**
//     * Similar to [RocksDB.get] but will also read writes from this batch.
//     *
//     * This function queries both this batch and the DB, then merges
//     * the results using the DB's merge operator (if the batch contains any
//     * merge requests).
//     *
//     * Setting [ReadOptions.setSnapshot] will affect what is
//     * read from the DB but will **not** change which keys are read from the batch
//     * (the keys in this batch do not yet belong to any snapshot and will be
//     * fetched regardless).
//     *
//     * @param db                 The RocksDB instance.
//     * @param columnFamilyHandle The column family to retrieve the value from.
//     * @param options            The read options to use.
//     * @param key                The key to read the value for.
//     * @return A byte array storing the value associated with the input key if any,
//     *         or `null` if the specified key is not found.
//     * @throws RocksDBException If the value for the key cannot be read.
//     */
//    fun getFromBatchAndDB(
//        db: RocksDB,
//        columnFamilyHandle: ColumnFamilyHandle,
//        options: ReadOptions,
//        key: ByteArray
//    ): ByteArray?
//
//    /**
//     * Similar to [RocksDB.get] but will also read writes from this batch.
//     *
//     * This function queries both this batch and the DB, then merges
//     * the results using the DB's merge operator (if the batch contains any
//     * merge requests).
//     *
//     * Setting [ReadOptions.setSnapshot] will affect what is
//     * read from the DB but will **not** change which keys are read from the batch
//     * (the keys in this batch do not yet belong to any snapshot and will be
//     * fetched regardless).
//     *
//     * @param db      The RocksDB instance.
//     * @param options The read options to use.
//     * @param key     The key to read the value for.
//     * @return A byte array storing the value associated with the input key if any,
//     *         or `null` if the specified key is not found.
//     * @throws RocksDBException If the value for the key cannot be read.
//     */
//    fun getFromBatchAndDB(
//        db: RocksDB,
//        options: ReadOptions,
//        key: ByteArray
//    ): ByteArray?
}
