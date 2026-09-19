package maryk.rocksdb

/** Possible stall states reported by the RocksDB write controller. */
expect enum class WriteStallCondition {
    /** Writes are slowed down but still admitted. */
    DELAYED,

    /** Writes are rejected until backpressure clears. */
    STOPPED,

    /** Writes are flowing normally without throttling. */
    NORMAL,
}
