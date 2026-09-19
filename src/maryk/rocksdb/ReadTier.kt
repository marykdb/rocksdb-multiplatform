package maryk.rocksdb

expect enum class ReadTier {
    READ_ALL_TIER,
    BLOCK_CACHE_TIER,
    PERSISTED_TIER,
    MEMTABLE_TIER;

    /**
     * Returns the byte value of the enumerations value
     * @return byte representation
     */
    fun getValue(): Byte
}
