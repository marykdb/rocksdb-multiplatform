package maryk.rocksdb

/**
 * A structure for describing the save point in the Write Batch.
 */
expect class WriteBatchSavePoint(
    size: Long,
    count: Long,
    contentFlags: Long
) {
    /**
     * Get the size of the serialized representation.
     *
     * @return the size of the serialized representation.
     */
    fun getSize(): Long

    /**
     * Get the number of elements.
     *
     * @return the number of elements.
     */
    fun getCount(): Long

    /**
     * Get the content flags.
     *
     * @return the content flags.
     */
    fun getContentFlags(): Long

    fun isCleared(): Boolean

    fun clear()
}
