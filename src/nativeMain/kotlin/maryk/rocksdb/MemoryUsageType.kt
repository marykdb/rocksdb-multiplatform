package maryk.rocksdb

actual enum class MemoryUsageType(
    private val value: Byte
) {
    kMemTableTotal(0),
    kMemTableUnFlushed(1),
    kTableReadersTotal(2),
    kCacheTotal(3),
    kNumUsageTypes(4);

    actual fun getValue() = value
}
