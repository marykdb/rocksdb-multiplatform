package maryk.rocksdb

actual enum class DataBlockIndexType(
    private val value: Byte
) {
    kDataBlockBinarySearch(0),
    kDataBlockBinaryAndHash(1);
}
