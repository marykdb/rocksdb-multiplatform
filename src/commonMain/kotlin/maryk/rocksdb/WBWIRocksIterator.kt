package maryk.rocksdb

expect class WBWIRocksIterator : AbstractRocksIterator<WriteBatchWithIndex> {
    /**
     * Get the current entry
     *
     * The WriteEntry is only valid
     * until the iterator is repositioned.
     * If you want to keep the WriteEntry across iterator
     * movements, you must make a copy of its data!
     *
     * Note - This method is not thread-safe with respect to the WriteEntry
     * as it performs a non-atomic update across the fields of the WriteEntry
     *
     * @return The WriteEntry of the current entry
     */
    fun entry(): WriteEntry
}
