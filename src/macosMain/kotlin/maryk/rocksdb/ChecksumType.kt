package maryk.rocksdb

actual enum class ChecksumType(
    private val value: Byte
) {
    kNoChecksum(0),
    kCRC32c(1),
    kxxHash(2);

    actual fun getValue() = value
}
