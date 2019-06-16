package maryk.rocksdb

/**
 * The config for skip-list memtable representation.
 */
expect class SkipListMemTableConfig() : MemTableConfig {
    /**
     * Sets lookahead for SkipList
     *
     * @param lookahead If non-zero, each iterator's seek operation
     * will start the search from the previously visited record
     * (doing at most 'lookahead' steps). This is an
     * optimization for the access pattern including many
     * seeks with consecutive keys.
     * @return the current instance of SkipListMemTableConfig
     */
    fun setLookahead(lookahead: Long): SkipListMemTableConfig

    /**
     * Returns the currently set lookahead value.
     *
     * @return lookahead value
     */
    fun lookahead(): Long
}
