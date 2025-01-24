package maryk.rocksdb


/**
 * An iterator over a [WriteBatchWithIndex] that allows traversing the entries
 * in the write batch.
 *
 * The iterator provides access to each [WriteEntry] in the write batch,
 * which includes the operation type, key, and value.
 *
 * **Note:** The [WriteEntry] returned by [entry] is only valid until the iterator
 * is repositioned. To retain the data across iterator movements, make a copy of the
 * [WriteEntry]'s data.
 *
 * **Thread Safety:** This iterator is not thread-safe with respect to the [WriteEntry]
 * as it performs a non-atomic update across the fields of the [WriteEntry].
 */
expect class WBWIRocksIterator : AbstractRocksIterator<WriteBatchWithIndex> {
    /**
     * Retrieves the current [WriteEntry] that the iterator is pointing to.
     *
     * The [WriteEntry] includes the type of write operation, the key, and the value.
     *
     * @return The current [WriteEntry].
     */
    fun entry(): WriteEntry

    /**
     * Closes the iterator and releases any associated resources.
     */
    override fun close()
}
