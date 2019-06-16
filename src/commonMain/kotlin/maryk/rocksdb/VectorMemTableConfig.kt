package maryk.rocksdb

/** The config for vector memtable representation. */
expect class VectorMemTableConfig() : MemTableConfig {
    /**
     * Set the initial size of the vector that will be used
     * by the memtable created based on this config.
     *
     * @param size the initial size of the vector.
     * @return the reference to the current config.
     */
    fun setReservedSize(size: Int): VectorMemTableConfig

    /**
     * Returns the initial size of the vector used by the memtable
     * created based on this config.
     *
     * @return the initial size of the vector.
     */
    fun reservedSize(): Int
}
