package maryk.rocksdb

/**
 * Builtin RocksDB comparators
 *
 *  1. BYTEWISE_COMPARATOR - Sorts all keys in ascending bytewise order.
 *  2. REVERSE_BYTEWISE_COMPARATOR - Sorts all keys in descending bytewise order
 */
expect enum class BuiltinComparator {
    BYTEWISE_COMPARATOR,
    REVERSE_BYTEWISE_COMPARATOR
}
