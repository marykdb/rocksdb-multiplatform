package maryk.rocksdb

actual enum class ReadTier(
    private val value: Byte
) {
    READ_ALL_TIER(0),
    BLOCK_CACHE_TIER(1),
    PERSISTED_TIER(2),
    MEMTABLE_TIER(3);

    actual fun getValue() = value
}
