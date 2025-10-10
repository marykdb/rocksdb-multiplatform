package maryk.rocksdb

/** Reason reported when RocksDB encounters a background error. */
expect enum class BackgroundErrorReason {
    /** A flush job failed. */
    FLUSH,

    /** A compaction job failed. */
    COMPACTION,

    /** A write callback failed. */
    WRITE_CALLBACK,

    /** An error occurred while writing to the memtable. */
    MEMTABLE,
}
