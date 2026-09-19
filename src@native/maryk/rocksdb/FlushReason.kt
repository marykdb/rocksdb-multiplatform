package maryk.rocksdb

actual enum class FlushReason(internal val id: Byte) {
    OTHERS(0x00),
    GET_LIVE_FILES(0x01),
    SHUTDOWN(0x02),
    EXTERNAL_FILE_INGESTION(0x03),
    MANUAL_COMPACTION(0x04),
    WRITE_BUFFER_MANAGER(0x05),
    WRITE_BUFFER_FULL(0x06),
    TEST(0x07),
    DELETE_FILES(0x08),
    AUTO_COMPACTION(0x09),
    MANUAL_FLUSH(0x0a),
    ERROR_RECOVERY(0x0b),
    ERROR_RECOVERY_RETRY_FLUSH(0x0c),
    WAL_FULL(0x0d),
    CATCH_UP_AFTER_ERROR_RECOVERY(0x0e);
}

actual fun FlushReason.value(): Byte = id

actual fun flushReasonFromValue(value: Byte): FlushReason =
    FlushReason.entries.firstOrNull { it.id == value }
        ?: error("Unknown FlushReason value: $value")
