package maryk.rocksdb

/**
 * Checksum types used in conjunction with BlockBasedTable.
 */
expect enum class ChecksumType {
    /** Disable block checksum. */
    kNoChecksum,
    /** CRC32 Checksum */
    kCRC32c,
    /** XX Hash */
    kxxHash;
}
