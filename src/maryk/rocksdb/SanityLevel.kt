package maryk.rocksdb

/**
 * Sanity checks applied when loading options files.
 */
expect enum class SanityLevel {
    NONE,
    LOOSELY_COMPATIBLE,
    EXACT_MATCH,
}
