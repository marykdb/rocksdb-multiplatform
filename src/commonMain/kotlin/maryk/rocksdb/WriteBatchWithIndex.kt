package maryk.rocksdb

/**
 * Similar to [WriteBatch] but with a binary searchable
 * index built for all the keys inserted.
 *
 * Calling put, merge, remove or putLogData calls the same function
 * as with [WriteBatch] whilst also building an index.
 *
 * A user can call [WriteBatchWithIndex.newIterator] to
 * create an iterator over the write batch or
 * [WriteBatchWithIndex.newIteratorWithBase]
 * to get an iterator for the database with Read-Your-Own-Writes like capability
 */
expect class WriteBatchWithIndex : AbstractWriteBatch {
    /**
     * Creates a WriteBatchWithIndex where no bytes
     * are reserved up-front, bytewise comparison is
     * used for fallback key comparisons,
     * and duplicate keys operations are retained
     */
    constructor()

    /**
     * Creates a WriteBatchWithIndex where no bytes
     * are reserved up-front, bytewise comparison is
     * used for fallback key comparisons, and duplicate key
     * assignment is determined by the constructor argument
     *
     * @param overwriteKey if true, overwrite the key in the index when
     * inserting a duplicate key, in this way an iterator will never
     * show two entries with the same key.
     */
    constructor(overwriteKey: Boolean)

    /**
     * Create an iterator of a column family. User can call
     * [org.rocksdb.RocksIteratorInterface.seek] to
     * search to the next entry of or after a key. Keys will be iterated in the
     * order given by index_comparator. For multiple updates on the same key,
     * each update will be returned as a separate entry, in the order of update
     * time.
     *
     * @param columnFamilyHandle The column family to iterate over
     * @return An iterator for the Write Batch contents, restricted to the column
     * family
     */
    fun newIterator(
        columnFamilyHandle: ColumnFamilyHandle
    ): WBWIRocksIterator

    /**
     * Create an iterator of the default column family. User can call
     * [maryk.rocksdb.RocksIteratorInterface.seek] to
     * search to the next entry of or after a key. Keys will be iterated in the
     * order given by index_comparator. For multiple updates on the same key,
     * each update will be returned as a separate entry, in the order of update
     * time.
     *
     * @return An iterator for the Write Batch contents
     */
    fun newIterator(): WBWIRocksIterator

    /**
     * Provides Read-Your-Own-Writes like functionality by
     * creating a new Iterator that will use [maryk.rocksdb.WBWIRocksIterator]
     * as a delta and baseIterator as a base
     *
     * Updating write batch with the current key of the iterator is not safe.
     * We strongly recommand users not to do it. It will invalidate the current
     * key() and value() of the iterator. This invalidation happens even before
     * the write batch update finishes. The state may recover after Next() is
     * called.
     *
     * @param columnFamilyHandle The column family to iterate over
     * @param baseIterator The base iterator,
     * e.g. [org.rocksdb.RocksDB.newIterator]
     * @return An iterator which shows a view comprised of both the database
     * point-in-time from baseIterator and modifications made in this write batch.
     */
    fun newIteratorWithBase(
        columnFamilyHandle: ColumnFamilyHandle,
        baseIterator: RocksIterator
    ): RocksIterator

    /**
     * Provides Read-Your-Own-Writes like functionality by
     * creating a new Iterator that will use [org.rocksdb.WBWIRocksIterator]
     * as a delta and baseIterator as a base. Operates on the default column
     * family.
     *
     * @param baseIterator The base iterator,
     * e.g. [maryk.rocksdb.RocksDB.newIterator]
     * @return An iterator which shows a view comprised of both the database
     * point-in-timefrom baseIterator and modifications made in this write batch.
     */
    fun newIteratorWithBase(baseIterator: RocksIterator): RocksIterator

    /**
     * Similar to [RocksDB.get] but will only
     * read the key from this batch.
     *
     * @param columnFamilyHandle The column family to retrieve the value from
     * @param options The database options to use
     * @param key The key to read the value for
     *
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException if the batch does not have enough data to resolve
     * Merge operations, MergeInProgress status may be returned.
     */
    fun getFromBatch(
        columnFamilyHandle: ColumnFamilyHandle,
        options: DBOptions, key: ByteArray
    ): ByteArray?

    /**
     * Similar to [RocksDB.get] but will only
     * read the key from this batch.
     *
     * @param options The database options to use
     * @param key The key to read the value for
     *
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException if the batch does not have enough data to resolve
     * Merge operations, MergeInProgress status may be returned.
     */
    fun getFromBatch(options: DBOptions, key: ByteArray): ByteArray?

    /**
     * Similar to [RocksDB.get] but will also
     * read writes from this batch.
     *
     * This function will query both this batch and the DB and then merge
     * the results using the DB's merge operator (if the batch contains any
     * merge requests).
     *
     * Setting [ReadOptions.setSnapshot] will affect what is
     * read from the DB but will NOT change which keys are read from the batch
     * (the keys in this batch do not yet belong to any snapshot and will be
     * fetched regardless).
     *
     * @param db The Rocks database
     * @param columnFamilyHandle The column family to retrieve the value from
     * @param options The read options to use
     * @param key The key to read the value for
     *
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException if the value for the key cannot be read
     */
    fun getFromBatchAndDB(
        db: RocksDB, columnFamilyHandle: ColumnFamilyHandle,
        options: ReadOptions, key: ByteArray
    ): ByteArray?

    /**
     * Similar to [RocksDB.get] but will also
     * read writes from this batch.
     *
     * This function will query both this batch and the DB and then merge
     * the results using the DB's merge operator (if the batch contains any
     * merge requests).
     *
     * Setting [ReadOptions.setSnapshot] will affect what is
     * read from the DB but will NOT change which keys are read from the batch
     * (the keys in this batch do not yet belong to any snapshot and will be
     * fetched regardless).
     *
     * @param db The Rocks database
     * @param options The read options to use
     * @param key The key to read the value for
     *
     * @return a byte array storing the value associated with the input key if
     * any. null if it does not find the specified key.
     *
     * @throws RocksDBException if the value for the key cannot be read
     */
    fun getFromBatchAndDB(
        db: RocksDB, options: ReadOptions,
        key: ByteArray
    ): ByteArray?
}
