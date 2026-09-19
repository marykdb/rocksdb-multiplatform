package maryk.rocksdb

/** Reasons that can trigger a memtable flush. */
expect enum class FlushReason {
    OTHERS,
    GET_LIVE_FILES,
    SHUTDOWN,
    EXTERNAL_FILE_INGESTION,
    MANUAL_COMPACTION,
    WRITE_BUFFER_MANAGER,
    WRITE_BUFFER_FULL,
    TEST,
    DELETE_FILES,
    AUTO_COMPACTION,
    MANUAL_FLUSH,
    ERROR_RECOVERY,
    ERROR_RECOVERY_RETRY_FLUSH,
    WAL_FULL,
    CATCH_UP_AFTER_ERROR_RECOVERY,
}

/** Encoded value used by RocksDB internals. */
expect fun FlushReason.value(): Byte

/** Resolve a [FlushReason] from its encoded value. */
expect fun flushReasonFromValue(value: Byte): FlushReason
