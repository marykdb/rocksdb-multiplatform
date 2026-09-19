package maryk.rocksdb

/**
 * Flags for [RocksDB.getApproximateSizes]
 * that specify whether memtable stats should be included,
 * or file stats approximation or both.
 */
expect enum class SizeApproximationFlag {
    NONE,
    INCLUDE_MEMTABLES,
    INCLUDE_FILES
}
