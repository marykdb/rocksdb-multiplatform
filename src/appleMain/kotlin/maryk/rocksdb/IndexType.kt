package maryk.rocksdb

actual enum class IndexType(
    internal val value: Byte
) {
    kBinarySearch(0),
    kHashSearch(1),
    kTwoLevelIndexSearch(2);
}
