package maryk.rocksdb

/**
 * DataBlockIndexType used in conjunction with BlockBasedTable.
 */
expect enum class DataBlockIndexType {
    /**
     * traditional block type
     */
    kDataBlockBinarySearch,

    /**
     * additional hash index
     */
    kDataBlockBinaryAndHash;
}
