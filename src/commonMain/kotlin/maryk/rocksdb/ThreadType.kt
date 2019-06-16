package maryk.rocksdb

/** The type of a thread. */
expect enum class ThreadType {
    /** RocksDB BG thread in high-pri thread pool. */
    HIGH_PRIORITY,

    /** RocksDB BG thread in low-pri thread pool. */
    LOW_PRIORITY,

    /** User thread (Non-RocksDB BG thread). */
    USER,

    /** RocksDB BG thread in bottom-pri thread pool */
    BOTTOM_PRIORITY;
}
