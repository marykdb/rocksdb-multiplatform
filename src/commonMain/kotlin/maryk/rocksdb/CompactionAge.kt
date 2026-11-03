package maryk.rocksdb

/**
 * Select the RocksDB default for TTL or periodic-compaction age in seconds.
 * The sentinel is -2, rather than a duration. Zero disables the corresponding age trigger.
 */
const val engineDefaultCompactionAgeSeconds: Long = -2L
