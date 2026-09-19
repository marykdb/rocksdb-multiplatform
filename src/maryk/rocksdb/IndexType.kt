package maryk.rocksdb

/**
 * IndexType used in conjunction with BlockBasedTable.
 */
expect enum class IndexType {
    /**
     * A space efficient index block that is optimized for
     * binary-search-based index.
     */
    kBinarySearch,
    /**
     * The hash index, if enabled, will do the hash lookup when
     * `Options.prefix_extractor` is provided.
     */
    kHashSearch,
    /**
     * A two-level index implementation. Both levels are binary search indexes.
     */
    kTwoLevelIndexSearch;
}
