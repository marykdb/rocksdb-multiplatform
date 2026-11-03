package maryk.rocksdb

/**
 * Request RocksDB's startup default for TTL or periodic-compaction age in seconds.
 * The sentinel is -2, rather than a duration. Zero disables the corresponding age trigger.
 * Live updates retain this raw marker in 11.8.1; use a concrete duration for live policy changes.
 */
const val engineDefaultCompactionAgeSeconds: Long = -2L
