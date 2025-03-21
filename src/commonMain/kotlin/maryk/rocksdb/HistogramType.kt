package maryk.rocksdb

expect enum class HistogramType {
    DB_GET,
    DB_WRITE,
    COMPACTION_TIME,
    COMPACTION_CPU_TIME,
    SUBCOMPACTION_SETUP_TIME,
    TABLE_SYNC_MICROS,
    COMPACTION_OUTFILE_SYNC_MICROS,
    WAL_FILE_SYNC_MICROS,
    MANIFEST_FILE_SYNC_MICROS,
    /**
     * TIME SPENT IN IO DURING TABLE OPEN.
     */
    TABLE_OPEN_IO_MICROS,
    DB_MULTIGET,
    READ_BLOCK_COMPACTION_MICROS,
    READ_BLOCK_GET_MICROS,
    WRITE_RAW_BLOCK_MICROS,
    NUM_FILES_IN_SINGLE_COMPACTION,
    DB_SEEK,
    WRITE_STALL,
    SST_READ_MICROS,

    FILE_READ_FLUSH_MICROS,
    FILE_READ_COMPACTION_MICROS,
    FILE_READ_DB_OPEN_MICROS,
    FILE_READ_GET_MICROS,
    FILE_READ_MULTIGET_MICROS,
    FILE_READ_DB_ITERATOR_MICROS,
    FILE_READ_VERIFY_DB_CHECKSUM_MICROS,
    FILE_READ_VERIFY_FILE_CHECKSUMS_MICROS,
    SST_WRITE_MICROS,
    FILE_WRITE_FLUSH_MICROS,
    FILE_WRITE_COMPACTION_MICROS,
    FILE_WRITE_DB_OPEN_MICROS,

    /**
     * The number of subcompactions actually scheduled during a compaction.
     */
    NUM_SUBCOMPACTIONS_SCHEDULED,

    /**
     * Value size distribution in each operation.
     */
    BYTES_PER_READ,
    BYTES_PER_WRITE,
    BYTES_PER_MULTIGET,

    COMPRESSION_TIMES_NANOS,
    DECOMPRESSION_TIMES_NANOS,
    READ_NUM_MERGE_OPERANDS,

    /**
     * Size of keys written to BlobDB.
     */
    BLOB_DB_KEY_SIZE,

    /**
     * Size of values written to BlobDB.
     */
    BLOB_DB_VALUE_SIZE,

    /**
     * BlobDB Put/PutWithTTL/PutUntil/Write latency.
     */
    BLOB_DB_WRITE_MICROS,

    /**
     * BlobDB Get lagency.
     */
    BLOB_DB_GET_MICROS,

    /**
     * BlobDB MultiGet latency.
     */
    BLOB_DB_MULTIGET_MICROS,

    /**
     * BlobDB Seek/SeekToFirst/SeekToLast/SeekForPrev latency.
     */
    BLOB_DB_SEEK_MICROS,

    /**
     * BlobDB Next latency.
     */
    BLOB_DB_NEXT_MICROS,

    /**
     * BlobDB Prev latency.
     */
    BLOB_DB_PREV_MICROS,

    /**
     * Blob file write latency.
     */
    BLOB_DB_BLOB_FILE_WRITE_MICROS,

    /**
     * Blob file read latency.
     */
    BLOB_DB_BLOB_FILE_READ_MICROS,

    /**
     * Blob file sync latency.
     */
    BLOB_DB_BLOB_FILE_SYNC_MICROS,

    /**
     * BlobDB compression time.
     */
    BLOB_DB_COMPRESSION_MICROS,

    /**
     * BlobDB decompression time.
     */
    BLOB_DB_DECOMPRESSION_MICROS,

    /**
     * Time spent flushing memtable to disk.
     */
    FLUSH_TIME,

    /**
     * Number of MultiGet batch keys overlapping a file
     */
    SST_BATCH_SIZE,

    /**
     * Size of a single IO batch issued by MultiGet
     */
    MULTIGET_IO_BATCH_SIZE,

    /**
     * Num of Index and Filter blocks read from file system per level in MultiGet
     * request
     */
    NUM_INDEX_AND_FILTER_BLOCKS_READ_PER_LEVEL,

    /**
     * Num of SST files read from file system per level in MultiGet request.
     */
    NUM_SST_READ_PER_LEVEL,

    /**
     * Num of LSM levels read from file system per MultiGet request.
     */
    NUM_LEVEL_READ_PER_MULTIGET,

    /**
     * The number of retry in auto resume
     */
    ERROR_HANDLER_AUTORESUME_RETRY_COUNT,

    ASYNC_READ_BYTES,

    POLL_WAIT_MICROS,

    /**
     * Number of prefetched bytes discarded by RocksDB.
     */
    PREFETCHED_BYTES_DISCARDED,

    /**
     * Wait time for aborting async read in FilePrefetchBuffer destructor
     */
    ASYNC_PREFETCH_ABORT_MICROS,

    /**
     * Number of bytes read for RocksDB's prefetching contents
     * (as opposed to file system's prefetch)
     * from the end of SST table during block based table open
     */
    TABLE_OPEN_PREFETCH_TAIL_READ_BYTES,

    // 0x3E for backwards compatibility on current minor version.
    HISTOGRAM_ENUM_MAX;
}
