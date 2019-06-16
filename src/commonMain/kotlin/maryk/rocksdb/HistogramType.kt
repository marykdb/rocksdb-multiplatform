package maryk.rocksdb

expect enum class HistogramType {
    DB_GET,
    DB_WRITE,
    COMPACTION_TIME,
    SUBCOMPACTION_SETUP_TIME,
    TABLE_SYNC_MICROS,
    COMPACTION_OUTFILE_SYNC_MICROS,
    WAL_FILE_SYNC_MICROS,
    MANIFEST_FILE_SYNC_MICROS,

    /** TIME SPENT IN IO DURING TABLE OPEN. */
    TABLE_OPEN_IO_MICROS,

    DB_MULTIGET,
    READ_BLOCK_COMPACTION_MICROS,
    READ_BLOCK_GET_MICROS,
    WRITE_RAW_BLOCK_MICROS,
    STALL_L0_SLOWDOWN_COUNT,
    STALL_MEMTABLE_COMPACTION_COUNT,
    STALL_L0_NUM_FILES_COUNT,
    HARD_RATE_LIMIT_DELAY_COUNT,
    SOFT_RATE_LIMIT_DELAY_COUNT,
    NUM_FILES_IN_SINGLE_COMPACTION,
    DB_SEEK,
    WRITE_STALL,
    SST_READ_MICROS,

    /** The number of subcompactions actually scheduled during a compaction. */
    NUM_SUBCOMPACTIONS_SCHEDULED,

    /** Value size distribution in each operation. */
    BYTES_PER_READ,
    BYTES_PER_WRITE,
    BYTES_PER_MULTIGET,

    /** number of bytes compressed.*/
    BYTES_COMPRESSED,

    /**
     * number of bytes decompressed.
     * number of bytes is when uncompressed; i.e. before/after respectively
     */
    BYTES_DECOMPRESSED,
    COMPRESSION_TIMES_NANOS,
    DECOMPRESSION_TIMES_NANOS,
    READ_NUM_MERGE_OPERANDS,

    /** Time spent flushing memtable to disk. */
    FLUSH_TIME,

    /** Size of keys written to BlobDB. */
    BLOB_DB_KEY_SIZE,

    /** Size of values written to BlobDB. */
    BLOB_DB_VALUE_SIZE,

    /** BlobDB Put/PutWithTTL/PutUntil/Write latency. */
    BLOB_DB_WRITE_MICROS,

    /** BlobDB Get latency. */
    BLOB_DB_GET_MICROS,

    /** BlobDB MultiGet latency. */
    BLOB_DB_MULTIGET_MICROS,

    /** BlobDB Seek/SeekToFirst/SeekToLast/SeekForPrev latency. */
    BLOB_DB_SEEK_MICROS,

    /** BlobDB Next latency. */
    BLOB_DB_NEXT_MICROS,

    /** BlobDB Prev latency. */
    BLOB_DB_PREV_MICROS,

    /** Blob file write latency. */
    BLOB_DB_BLOB_FILE_WRITE_MICROS,

    /** Blob file read latency. */
    BLOB_DB_BLOB_FILE_READ_MICROS,

    /** Blob file sync latency. */
    BLOB_DB_BLOB_FILE_SYNC_MICROS,

    /** BlobDB garbage collection time. */
    BLOB_DB_GC_MICROS,

    /** BlobDB compression time. */
    BLOB_DB_COMPRESSION_MICROS,

    /** BlobDB decompression time. */
    BLOB_DB_DECOMPRESSION_MICROS,

    // 0x1F for backwards compatibility on current minor version.
    HISTOGRAM_ENUM_MAX
}
