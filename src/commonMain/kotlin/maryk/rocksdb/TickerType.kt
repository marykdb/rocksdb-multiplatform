package maryk.rocksdb


/**
 * The logical mapping of tickers defined in rocksdb::Tickers.
 *
 * Java byte value mappings don't align 1:1 to the c++ values. c++ rocksdb::Tickers enumeration type
 * is uint32_t and java org.rocksdb.TickerType is byte, this causes mapping issues when
 * rocksdb::Tickers value is greater then 127 (0x7F) for jbyte jni interface as range greater is not
 * available. Without breaking interface in minor versions, value mappings for
 * org.rocksdb.TickerType leverage full byte range [-128 (-0x80), (0x7F)]. Newer tickers added
 * should descend into negative values until TICKER_ENUM_MAX reaches -128 (-0x80).
 */
expect enum class TickerType {

    /**
     * total block cache misses
     *
     * REQUIRES: BLOCK_CACHE_MISS == BLOCK_CACHE_INDEX_MISS +
     * BLOCK_CACHE_FILTER_MISS +
     * BLOCK_CACHE_DATA_MISS;
     */
    BLOCK_CACHE_MISS,

    /**
     * total block cache hit
     *
     * REQUIRES: BLOCK_CACHE_HIT == BLOCK_CACHE_INDEX_HIT +
     * BLOCK_CACHE_FILTER_HIT +
     * BLOCK_CACHE_DATA_HIT;
     */
    BLOCK_CACHE_HIT,

    BLOCK_CACHE_ADD,

    /**
     * Number of failures when adding blocks to block cache.
     */
    BLOCK_CACHE_ADD_FAILURES,

    /**
     * Number of times cache miss when accessing index block from block cache.
     */
    BLOCK_CACHE_INDEX_MISS,

    /**
     * Number of times cache hit when accessing index block from block cache.
     */
    BLOCK_CACHE_INDEX_HIT,

    /**
     * Number of index blocks added to block cache.
     */
    BLOCK_CACHE_INDEX_ADD,

    /**
     * Number of bytes of index blocks inserted into cache
     */
    BLOCK_CACHE_INDEX_BYTES_INSERT,

    /**
     * Number of times cache miss when accessing filter block from block cache.
     */
    BLOCK_CACHE_FILTER_MISS,

    /**
     * Number of times cache hit when accessing filter block from block cache.
     */
    BLOCK_CACHE_FILTER_HIT,

    /**
     * Number of filter blocks added to block cache.
     */
    BLOCK_CACHE_FILTER_ADD,

    /**
     * Number of bytes of bloom filter blocks inserted into cache
     */
    BLOCK_CACHE_FILTER_BYTES_INSERT,

    /**
     * Number of times cache miss when accessing data block from block cache.
     */
    BLOCK_CACHE_DATA_MISS,

    /**
     * Number of times cache hit when accessing data block from block cache.
     */
    BLOCK_CACHE_DATA_HIT,

    /**
     * Number of data blocks added to block cache.
     */
    BLOCK_CACHE_DATA_ADD,

    /**
     * Number of bytes of data blocks inserted into cache
     */
    BLOCK_CACHE_DATA_BYTES_INSERT,

    /**
     * Number of bytes read from cache.
     */
    BLOCK_CACHE_BYTES_READ,

    /**
     * Number of bytes written into cache.
     */
    BLOCK_CACHE_BYTES_WRITE,

    /**
     * Number of times bloom filter has avoided file reads.
     */
    BLOOM_FILTER_USEFUL,

    /**
     * Number persistent cache hit
     */
    PERSISTENT_CACHE_HIT,

    /**
     * Number persistent cache miss
     */
    PERSISTENT_CACHE_MISS,

    /**
     * Number total simulation block cache hits
     */
    SIM_BLOCK_CACHE_HIT,

    /**
     * Number total simulation block cache misses
     */
    SIM_BLOCK_CACHE_MISS,

    /**
     * Number of memtable hits.
     */
    MEMTABLE_HIT,

    /**
     * Number of memtable misses.
     */
    MEMTABLE_MISS,

    /**
     * Number of Get() queries served by L0
     */
    GET_HIT_L0,

    /**
     * Number of Get() queries served by L1
     */
    GET_HIT_L1,

    /**
     * Number of Get() queries served by L2 and up
     */
    GET_HIT_L2_AND_UP,

    /**
     * COMPACTION_KEY_DROP_* count the reasons for key drop during compaction
     * There are 4 reasons currently.
     */

    /**
     * key was written with a newer value.
     */
    COMPACTION_KEY_DROP_NEWER_ENTRY,

    /**
     * Also includes keys dropped for range del.
     * The key is obsolete.
     */
    COMPACTION_KEY_DROP_OBSOLETE,

    /**
     * key was covered by a range tombstone.
     */
    COMPACTION_KEY_DROP_RANGE_DEL,

    /**
     * User compaction function has dropped the key.
     */
    COMPACTION_KEY_DROP_USER,

    /**
     * all keys in range were deleted.
     */
    COMPACTION_RANGE_DEL_DROP_OBSOLETE,

    /**
     * Number of keys written to the database via the Put and Write call's.
     */
    NUMBER_KEYS_WRITTEN,

    /**
     * Number of Keys read.
     */
    NUMBER_KEYS_READ,

    /**
     * Number keys updated, if inplace update is enabled
     */
    NUMBER_KEYS_UPDATED,

    /**
     * The number of uncompressed bytes issued by DB::Put(), DB::Delete(),\
     * DB::Merge.
     */
    BYTES_WRITTEN,

    /**
     * The number of uncompressed bytes read from DB::Get().  It could be
     * either from memtables, cache, or table files.
     *
     * For the number of logical bytes read from DB::MultiGet(),
     * please use [.NUMBER_MULTIGET_BYTES_READ].
     */
    BYTES_READ,

    /**
     * The number of calls to seek.
     */
    NUMBER_DB_SEEK,

    /**
     * The number of calls to next.
     */
    NUMBER_DB_NEXT,

    /**
     * The number of calls to prev.
     */
    NUMBER_DB_PREV,

    /**
     * The number of calls to seek that returned data.
     */
    NUMBER_DB_SEEK_FOUND,

    /**
     * The number of calls to next that returned data.
     */
    NUMBER_DB_NEXT_FOUND,

    /**
     * The number of calls to prev that returned data.
     */
    NUMBER_DB_PREV_FOUND,

    /**
     * The number of uncompressed bytes read from an iterator.
     * Includes size of key and value.
     */
    ITER_BYTES_READ,

    NO_FILE_OPENS,

    NO_FILE_ERRORS,

    /**
     * Writer has to wait for compaction or flush to finish.
     */
    STALL_MICROS,

    /**
     * The wait time for db mutex.
     *
     * Disabled by default. To enable it set stats level to [StatsLevel.ALL]
     */
    DB_MUTEX_WAIT_MICROS,

    /**
     * Number of MultiGet calls.
     */
    NUMBER_MULTIGET_CALLS,

    /**
     * Number of MultiGet keys read.
     */
    NUMBER_MULTIGET_KEYS_READ,

    /**
     * Number of MultiGet bytes read.
     */
    NUMBER_MULTIGET_BYTES_READ,

    NUMBER_MERGE_FAILURES,

    /**
     * Number of times bloom was checked before creating iterator on a
     * file, and the number of times the check was useful in avoiding
     * iterator creation (and thus likely IOPs).
     */
    BLOOM_FILTER_PREFIX_CHECKED,
    BLOOM_FILTER_PREFIX_USEFUL,

    /**
     * Number of times we had to reseek inside an iteration to skip
     * over large number of keys with same userkey.
     */
    NUMBER_OF_RESEEKS_IN_ITERATION,

    /**
     * Record the number of calls to `RocksDB.getUpdatesSince`. Useful to keep track of
     * transaction log iterator refreshes.
     */
    GET_UPDATES_SINCE_CALLS,

    /**
     * Number of times WAL sync is done.
     */
    WAL_FILE_SYNCED,

    /**
     * Number of bytes written to WAL.
     */
    WAL_FILE_BYTES,

    /**
     * Writes can be processed by requesting thread or by the thread at the
     * head of the writers queue.
     */
    WRITE_DONE_BY_SELF,

    /**
     * Equivalent to writes done for others.
     */
    WRITE_DONE_BY_OTHER,

    /**
     * Number of Write calls that request WAL.
     */
    WRITE_WITH_WAL,

    /**
     * Bytes read during compaction.
     */
    COMPACT_READ_BYTES,

    /**
     * Bytes written during compaction.
     */
    COMPACT_WRITE_BYTES,

    /**
     * Bytes written during flush.
     */
    FLUSH_WRITE_BYTES,

    /**
     * Number of table's properties loaded directly from file, without creating
     * table reader object.
     */
    NUMBER_DIRECT_LOAD_TABLE_PROPERTIES,
    NUMBER_SUPERVERSION_ACQUIRES,
    NUMBER_SUPERVERSION_RELEASES,
    NUMBER_SUPERVERSION_CLEANUPS,

    /**
     * Number of compressions/decompressions executed
     */
    NUMBER_BLOCK_COMPRESSED,
    NUMBER_BLOCK_DECOMPRESSED,

    NUMBER_BLOCK_NOT_COMPRESSED,
    MERGE_OPERATION_TOTAL_TIME,
    FILTER_OPERATION_TOTAL_TIME,

    /**
     * Row cache.
     */
    ROW_CACHE_HIT,
    ROW_CACHE_MISS,

    /**
     * Read amplification statistics.
     *
     * Read amplification can be calculated using this formula
     * (READ_AMP_TOTAL_READ_BYTES / READ_AMP_ESTIMATE_USEFUL_BYTES)
     *
     * REQUIRES: ReadOptions::read_amp_bytes_per_bit to be enabled
     */

    /**
     * Estimate of total bytes actually used.
     */
    READ_AMP_ESTIMATE_USEFUL_BYTES,

    /**
     * Total size of loaded data blocks.
     */
    READ_AMP_TOTAL_READ_BYTES,

    /**
     * Number of refill intervals where rate limiter's bytes are fully consumed.
     */
    NUMBER_RATE_LIMITER_DRAINS,

    /**
     * Number of internal skipped during iteration
     */
    NUMBER_ITER_SKIP,

    /**
     * Number of MultiGet keys found (vs number requested)
     */
    NUMBER_MULTIGET_KEYS_FOUND,

    // -0x01 to fixate the new value that incorrectly changed TICKER_ENUM_MAX
    /**
     * Number of iterators created.
     */
    NO_ITERATOR_CREATED,

    /**
     * Number of iterators deleted.
     */
    NO_ITERATOR_DELETED,

    /**
     * Deletions obsoleted before bottom level due to file gap optimization.
     */
    COMPACTION_OPTIMIZED_DEL_DROP_OBSOLETE,

    /**
     * If a compaction was cancelled in sfm to prevent ENOSPC
     */
    COMPACTION_CANCELLED,

    /**
     * Number of times bloom FullFilter has not avoided the reads.
     */
    BLOOM_FILTER_FULL_POSITIVE,

    /**
     * Number of times bloom FullFilter has not avoided the reads and data actually
     * exist.
     */
    BLOOM_FILTER_FULL_TRUE_POSITIVE,

    /**
     * BlobDB specific stats
     * Number of Put/PutTTL/PutUntil to BlobDB.
     */
    BLOB_DB_NUM_PUT,

    /**
     * Number of Write to BlobDB.
     */
    BLOB_DB_NUM_WRITE,

    /**
     * Number of Get to BlobDB.
     */
    BLOB_DB_NUM_GET,

    /**
     * Number of MultiGet to BlobDB.
     */
    BLOB_DB_NUM_MULTIGET,

    /**
     * Number of Seek/SeekToFirst/SeekToLast/SeekForPrev to BlobDB iterator.
     */
    BLOB_DB_NUM_SEEK,

    /**
     * Number of Next to BlobDB iterator.
     */
    BLOB_DB_NUM_NEXT,

    /**
     * Number of Prev to BlobDB iterator.
     */
    BLOB_DB_NUM_PREV,

    /**
     * Number of keys written to BlobDB.
     */
    BLOB_DB_NUM_KEYS_WRITTEN,

    /**
     * Number of keys read from BlobDB.
     */
    BLOB_DB_NUM_KEYS_READ,

    /**
     * Number of bytes (key + value) written to BlobDB.
     */
    BLOB_DB_BYTES_WRITTEN,

    /**
     * Number of bytes (keys + value) read from BlobDB.
     */
    BLOB_DB_BYTES_READ,

    /**
     * Number of keys written by BlobDB as non-TTL inlined value.
     */
    BLOB_DB_WRITE_INLINED,

    /**
     * Number of keys written by BlobDB as TTL inlined value.
     */
    BLOB_DB_WRITE_INLINED_TTL,

    /**
     * Number of keys written by BlobDB as non-TTL blob value.
     */
    BLOB_DB_WRITE_BLOB,

    /**
     * Number of keys written by BlobDB as TTL blob value.
     */
    BLOB_DB_WRITE_BLOB_TTL,

    /**
     * Number of bytes written to blob file.
     */
    BLOB_DB_BLOB_FILE_BYTES_WRITTEN,

    /**
     * Number of bytes read from blob file.
     */
    BLOB_DB_BLOB_FILE_BYTES_READ,

    /**
     * Number of times a blob files being synced.
     */
    BLOB_DB_BLOB_FILE_SYNCED,

    /**
     * Number of blob index evicted from base DB by BlobDB compaction filter because
     * of expiration.
     */
    BLOB_DB_BLOB_INDEX_EXPIRED_COUNT,

    /**
     * Size of blob index evicted from base DB by BlobDB compaction filter
     * because of expiration.
     */
    BLOB_DB_BLOB_INDEX_EXPIRED_SIZE,

    /**
     * Number of blob index evicted from base DB by BlobDB compaction filter because
     * of corresponding file deleted.
     */
    BLOB_DB_BLOB_INDEX_EVICTED_COUNT,

    /**
     * Size of blob index evicted from base DB by BlobDB compaction filter
     * because of corresponding file deleted.
     */
    BLOB_DB_BLOB_INDEX_EVICTED_SIZE,

    /**
     * Number of blob files being garbage collected.
     */
    BLOB_DB_GC_NUM_FILES,

    /**
     * Number of blob files generated by garbage collection.
     */
    BLOB_DB_GC_NUM_NEW_FILES,

    /**
     * Number of BlobDB garbage collection failures.
     */
    BLOB_DB_GC_FAILURES,

    /**
     * Number of keys relocated to new blob file by garbage collection.
     */
    BLOB_DB_GC_NUM_KEYS_RELOCATED,

    /**
     * Number of bytes relocated to new blob file by garbage collection.
     */
    BLOB_DB_GC_BYTES_RELOCATED,

    /**
     * Number of blob files evicted because of BlobDB is full.
     */
    BLOB_DB_FIFO_NUM_FILES_EVICTED,

    /**
     * Number of keys in the blob files evicted because of BlobDB is full.
     */
    BLOB_DB_FIFO_NUM_KEYS_EVICTED,

    /**
     * Number of bytes in the blob files evicted because of BlobDB is full.
     */
    BLOB_DB_FIFO_BYTES_EVICTED,

    /**
     * These counters indicate a performance issue in WritePrepared transactions.
     * We should not seem them ticking them much.
     * Number of times prepare_mutex_ is acquired in the fast path.
     */
    TXN_PREPARE_MUTEX_OVERHEAD,

    /**
     * Number of times old_commit_map_mutex_ is acquired in the fast path.
     */
    TXN_OLD_COMMIT_MAP_MUTEX_OVERHEAD,

    /**
     * Number of times we checked a batch for duplicate keys.
     */
    TXN_DUPLICATE_KEY_OVERHEAD,

    /**
     * Number of times snapshot_mutex_ is acquired in the fast path.
     */
    TXN_SNAPSHOT_MUTEX_OVERHEAD,

    /**
     * # of times ::Get returned TryAgain due to expired snapshot seq
     */
    TXN_GET_TRY_AGAIN,

    /**
     * # of files marked as trash by delete scheduler
     */
    FILES_MARKED_TRASH,

    /**
     * # of files deleted immediately by delete scheduler
     */
    FILES_DELETED_IMMEDIATELY,

    /**
     * Compaction read and write statistics broken down by CompactionReason
     */
    COMPACT_READ_BYTES_MARKED,
    COMPACT_READ_BYTES_PERIODIC,
    COMPACT_READ_BYTES_TTL,
    COMPACT_WRITE_BYTES_MARKED,
    COMPACT_WRITE_BYTES_PERIODIC,
    COMPACT_WRITE_BYTES_TTL,

    /**
     * DB error handler statistics
     */
    ERROR_HANDLER_BG_ERROR_COUNT,
    ERROR_HANDLER_BG_IO_ERROR_COUNT,
    ERROR_HANDLER_BG_RETRYABLE_IO_ERROR_COUNT,
    ERROR_HANDLER_AUTORESUME_COUNT,
    ERROR_HANDLER_AUTORESUME_RETRY_TOTAL_COUNT,
    ERROR_HANDLER_AUTORESUME_SUCCESS_COUNT,

    /**
     * Bytes of raw data (payload) found on memtable at flush time.
     * Contains the sum of garbage payload (bytes that are discarded
     * at flush time) and useful payload (bytes of data that will
     * eventually be written to SSTable).
     */
    MEMTABLE_PAYLOAD_BYTES_AT_FLUSH,

    /**
     * Outdated bytes of data present on memtable at flush time.
     */
    MEMTABLE_GARBAGE_BYTES_AT_FLUSH,

    /**
     * Number of secondary cache hits
     */
    SECONDARY_CACHE_HITS,

    /**
     * Bytes read by `VerifyChecksum()` and `VerifyFileChecksums()` APIs.
     */
    VERIFY_CHECKSUM_READ_BYTES,

    /**
     * Bytes read/written while creating backups
     */
    BACKUP_READ_BYTES,
    BACKUP_WRITE_BYTES,

    /**
     * Remote compaction read/write statistics
     */
    REMOTE_COMPACT_READ_BYTES,
    REMOTE_COMPACT_WRITE_BYTES,

    /**
     * Tiered storage related statistics
     */
    HOT_FILE_READ_BYTES,
    WARM_FILE_READ_BYTES,
    COLD_FILE_READ_BYTES,
    HOT_FILE_READ_COUNT,
    WARM_FILE_READ_COUNT,
    COLD_FILE_READ_COUNT,

    /**
     * (non-)last level read statistics
     */
    LAST_LEVEL_READ_BYTES,
    LAST_LEVEL_READ_COUNT,
    NON_LAST_LEVEL_READ_BYTES,
    NON_LAST_LEVEL_READ_COUNT,

    BLOCK_CHECKSUM_COMPUTE_COUNT,

    /**
     * # of times cache miss when accessing blob from blob cache.
     */
    BLOB_DB_CACHE_MISS,

    /**
     * # of times cache hit when accessing blob from blob cache.
     */
    BLOB_DB_CACHE_HIT,

    /**
     * # of data blocks added to blob cache.
     */
    BLOB_DB_CACHE_ADD,

    /**
     * # # of failures when adding blobs to blob cache.
     */
    BLOB_DB_CACHE_ADD_FAILURES,

    /**
     * # of bytes read from blob cache.
     */
    BLOB_DB_CACHE_BYTES_READ,

    /**
     * # of bytes written into blob cache.
     */
    BLOB_DB_CACHE_BYTES_WRITE,

    TICKER_ENUM_MAX
}
