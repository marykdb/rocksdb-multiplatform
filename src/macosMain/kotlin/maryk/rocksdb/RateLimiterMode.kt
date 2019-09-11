package maryk.rocksdb

actual enum class RateLimiterMode(
    internal val value: Byte
) {
    READS_ONLY(0x0),
    WRITES_ONLY(0x1),
    ALL_IO(0x2);
}
