@file:Suppress("unused")

package maryk.rocksdb

import rocksdb.RocksDBHistogram

actual enum class HistogramType(
    internal val value: RocksDBHistogram
) {
    DB_GET(0x0u),
    DB_WRITE(0x1u),
    COMPACTION_TIME(0x2u),
    SUBCOMPACTION_SETUP_TIME(0x3u),
    TABLE_SYNC_MICROS(0x4u),
    COMPACTION_OUTFILE_SYNC_MICROS(0x5u),
    WAL_FILE_SYNC_MICROS(0x6u),
    MANIFEST_FILE_SYNC_MICROS(0x7u),
    TABLE_OPEN_IO_MICROS(0x8u),
    DB_MULTIGET(0x9u),
    READ_BLOCK_COMPACTION_MICROS(0xAu),
    READ_BLOCK_GET_MICROS(0xBu),
    WRITE_RAW_BLOCK_MICROS(0xCu),
    NUM_FILES_IN_SINGLE_COMPACTION(0x12u),
    DB_SEEK(0x13u),
    WRITE_STALL(0x14u),
    SST_READ_MICROS(0x15u),
    NUM_SUBCOMPACTIONS_SCHEDULED(0x16u),
    BYTES_PER_READ(0x17u),
    BYTES_PER_WRITE(0x18u),
    BYTES_PER_MULTIGET(0x19u),
    BYTES_COMPRESSED(0x1Au),
    BYTES_DECOMPRESSED(0x1Bu),
    COMPRESSION_TIMES_NANOS(0x1Cu),
    DECOMPRESSION_TIMES_NANOS(0x1Du),
    READ_NUM_MERGE_OPERANDS(0x1Eu),
    FLUSH_TIME(0x20u),
    BLOB_DB_KEY_SIZE(0x21u),
    BLOB_DB_VALUE_SIZE(0x22u),
    BLOB_DB_WRITE_MICROS(0x23u),
    BLOB_DB_GET_MICROS(0x24u),
    BLOB_DB_MULTIGET_MICROS(0x25u),
    BLOB_DB_SEEK_MICROS(0x26u),
    BLOB_DB_NEXT_MICROS(0x27u),
    BLOB_DB_PREV_MICROS(0x28u),
    BLOB_DB_BLOB_FILE_WRITE_MICROS(0x29u),
    BLOB_DB_BLOB_FILE_READ_MICROS(0x2Au),
    BLOB_DB_BLOB_FILE_SYNC_MICROS(0x2Bu),
    BLOB_DB_COMPRESSION_MICROS(0x2Du),
    BLOB_DB_DECOMPRESSION_MICROS(0x2Eu),
    NUM_INDEX_AND_FILTER_BLOCKS_READ_PER_LEVEL(0x2Fu),
    NUM_SST_READ_PER_LEVEL(0x31u),
    ERROR_HANDLER_AUTORESUME_RETRY_COUNT(0x32u),
    ASYNC_READ_BYTES(0x33u),
    HISTOGRAM_ENUM_MAX(0x1Fu)
}
