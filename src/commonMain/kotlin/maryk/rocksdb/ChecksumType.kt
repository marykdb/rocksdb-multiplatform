package maryk.rocksdb

/**
 * Checksum types used in conjunction with BlockBasedTable.
 */
expect enum class ChecksumType {
    /** Not implemented yet. */
    kNoChecksum,
    /** CRC32 Checksum */
    kCRC32c,
    /** XX Hash */
    kxxHash;
}
