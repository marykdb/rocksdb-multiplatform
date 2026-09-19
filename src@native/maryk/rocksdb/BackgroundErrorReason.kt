package maryk.rocksdb

actual enum class BackgroundErrorReason(internal val value: UInt) {
    FLUSH(0u),
    COMPACTION(1u),
    WRITE_CALLBACK(2u),
    MEMTABLE(3u);
}

internal fun backgroundErrorReasonFromValue(value: UInt): BackgroundErrorReason =
    BackgroundErrorReason.entries.firstOrNull { it.value == value }
        ?: BackgroundErrorReason.MEMTABLE
