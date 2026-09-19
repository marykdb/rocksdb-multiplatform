package maryk.rocksdb

actual enum class ChecksumType(
    internal val value: Byte
) {
    kNoChecksum(0),
    kCRC32c(1),
    kxxHash(2);
}
