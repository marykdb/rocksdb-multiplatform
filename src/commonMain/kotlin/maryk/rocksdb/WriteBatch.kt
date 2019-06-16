package maryk.rocksdb

/**
 * WriteBatch holds a collection of updates to apply atomically to a DB.
 *
 * The updates are applied in the order in which they are added
 * to the WriteBatch.  For example, the value of "key" will be "v3"
 * after the following batch is written:
 *
 * batch.put("key", "v1");
 * batch.remove("key");
 * batch.put("key", "v2");
 * batch.put("key", "v3");
 *
 * Multiple threads can invoke const methods on a WriteBatch without
 * external synchronization, but if any of the threads may call a
 * non-const method, all threads accessing the same WriteBatch must use
 * external synchronization.
 */
expect class WriteBatch : AbstractWriteBatch {
    /**
     * Retrieve data size of the batch.
     * @return the serialized data size of the batch.
     */
    fun getDataSize(): Long

    /**
     * Gets the WAL termination point.
     * See [.markWalTerminationPoint]
     * @return the WAL termination point
     */
    fun getWalTerminationPoint(): WriteBatchSavePoint

    /**
     * Constructs a WriteBatch instance
     */
    constructor()

    /**
     * Constructs a WriteBatch instance with a given size.
     *
     * @param reserved_bytes reserved size for WriteBatch
     */
    constructor(reserved_bytes: Int = 0)

    /**
     * Constructs a WriteBatch instance from a serialized representation
     * as returned by [.data].
     *
     * @param serialized the serialized representation.
     */
    constructor(serialized: ByteArray)

    /**
     * Support for iterating over the contents of a batch.
     *
     * @param handler A handler that is called back for each
     * update present in the batch
     *
     * @throws RocksDBException If we cannot iterate over the batch
     */
    fun iterate(handler: WriteBatchHandler)

    /**
     * Retrieve the serialized version of this batch.
     *
     * @return the serialized representation of this write batch.
     *
     * @throws RocksDBException if an error occurs whilst retrieving
     * the serialized batch data.
     */
    fun data(): ByteArray

    /** Returns true if Put will be called during Iterate. */
    fun hasPut(): Boolean

    /** Returns true if Delete will be called during Iterate. */
    fun hasDelete(): Boolean

    /** Returns true if SingleDelete will be called during Iterate. */
    fun hasSingleDelete(): Boolean

    /** Returns true if DeleteRange will be called during Iterate. */
    fun hasDeleteRange(): Boolean

    /** Returns true if Merge will be called during Iterate. */
    fun hasMerge(): Boolean

    /** Returns true if MarkBeginPrepare will be called during Iterate. */
    fun hasBeginPrepare(): Boolean

    /** Returns true if MarkEndPrepare will be called during Iterate. */
    fun hasEndPrepare(): Boolean

    /** Returns true if MarkCommit will be called during Iterate. */
    fun hasCommit(): Boolean

    /** @return true if MarkRollback will be called during Iterate. */
    fun hasRollback(): Boolean

    /**
     * Marks this point in the WriteBatch as the last record to
     * be inserted into the WAL, provided the WAL is enabled.
     */
    fun markWalTerminationPoint()
}

