package maryk.rocksdb

actual enum class IndexType(
    private val value: Byte
) {
    kBinarySearch(0),
    kHashSearch(1),
    kTwoLevelIndexSearch(2);

    actual fun getValue() = value
}
