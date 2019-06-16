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
     * # of failures when adding blocks to block cache.
     */
    BLOCK_CACHE_ADD_FAILURES,

    /**
     * # of times cache miss when accessing index block from block cache.
     */
    BLOCK_CACHE_INDEX_MISS,

    /**
     * # of times cache hit when accessing index block from block cache.
     */
    BLOCK_CACHE_INDEX_HIT,

    /**
     * # of index blocks added to block cache.
     */
    BLOCK_CACHE_INDEX_ADD,

    /**
     * # of bytes of index blocks inserted into cache
     */
    BLOCK_CACHE_INDEX_BYTES_INSERT,

    /**
     * # of bytes of index block erased from cache
     */
    BLOCK_CACHE_INDEX_BYTES_EVICT,

    /**
     * # of times cache miss when accessing filter block from block cache.
     */
    BLOCK_CACHE_FILTER_MISS,

    /**
     * # of times cache hit when accessing filter block from block cache.
     */
    BLOCK_CACHE_FILTER_HIT,

    /**
     * # of filter blocks added to block cache.
     */
    BLOCK_CACHE_FILTER_ADD,

    /**
     * # of bytes of bloom filter blocks inserted into cache
     */
    BLOCK_CACHE_FILTER_BYTES_INSERT,

    /**
     * # of bytes of bloom filter block erased from cache
     */
    BLOCK_CACHE_FILTER_BYTES_EVICT,

    /**
     * # of times cache miss when accessing data block from block cache.
     */
    BLOCK_CACHE_DATA_MISS,

    /**
     * # of times cache hit when accessing data block from block cache.
     */
    BLOCK_CACHE_DATA_HIT,

    /**
     * # of data blocks added to block cache.
     */
    BLOCK_CACHE_DATA_ADD,

    /**
     * # of bytes of data blocks inserted into cache
     */
    BLOCK_CACHE_DATA_BYTES_INSERT,

    /**
     * # of bytes read from cache.
     */
    BLOCK_CACHE_BYTES_READ,

    /**
     * # of bytes written into cache.
     */
    BLOCK_CACHE_BYTES_WRITE,

    /**
     * # of times bloom filter has avoided file reads.
     */
    BLOOM_FILTER_USEFUL,

    /**
     * # persistent cache hit
     */
    PERSISTENT_CACHE_HIT,

    /**
     * # persistent cache miss
     */
    PERSISTENT_CACHE_MISS,

    /**
     * # total simulation block cache hits
     */
    SIM_BLOCK_CACHE_HIT,

    /**
     * # total simulation block cache misses
     */
    SIM_BLOCK_CACHE_MISS,

    /**
     * # of memtable hits.
     */
    MEMTABLE_HIT,

    /**
     * # of memtable misses.
     */
    MEMTABLE_MISS,

    /**
     * # of Get() queries served by L0
     */
    GET_HIT_L0,

    /**
     * # of Get() queries served by L1
     */
    GET_HIT_L1,

    /**
     * # of Get() queries served by L2 and up
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

    NO_FILE_CLOSES,

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

    RATE_LIMIT_DELAY_MILLIS,

    /**
     * Number of iterators created.
     *
     */
    NO_ITERATORS,

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

    /**
     * Number of deletes records that were not required to be
     * written to storage because key does not exist.
     */
    NUMBER_FILTERED_DELETES,
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
     * Record the number of calls to [RocksDB.getUpdatesSince]. Useful to keep track of
     * transaction log iterator refreshes.
     */
    GET_UPDATES_SINCE_CALLS,

    /**
     * Miss in the compressed block cache.
     */
    BLOCK_CACHE_COMPRESSED_MISS,

    /**
     * Hit in the compressed block cache.
     */
    BLOCK_CACHE_COMPRESSED_HIT,

    /**
     * Number of blocks added to compressed block cache.
     */
    BLOCK_CACHE_COMPRESSED_ADD,

    /**
     * Number of failures when adding blocks to compressed block cache.
     */
    BLOCK_CACHE_COMPRESSED_ADD_FAILURES,

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
     * Number of writes ending up with timed-out.
     */
    WRITE_TIMEDOUT,

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
     * # of compressions/decompressions executed
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
     * # of times bloom FullFilter has not avoided the reads.
     */
    BLOOM_FILTER_FULL_POSITIVE,

    /**
     * # of times bloom FullFilter has not avoided the reads and data actually
     * exist.
     */
    BLOOM_FILTER_FULL_TRUE_POSITIVE,

    /**
     * BlobDB specific stats
     * # of Put/PutTTL/PutUntil to BlobDB.
     */
    BLOB_DB_NUM_PUT,

    /**
     * # of Write to BlobDB.
     */
    BLOB_DB_NUM_WRITE,

    /**
     * # of Get to BlobDB.
     */
    BLOB_DB_NUM_GET,

    /**
     * # of MultiGet to BlobDB.
     */
    BLOB_DB_NUM_MULTIGET,

    /**
     * # of Seek/SeekToFirst/SeekToLast/SeekForPrev to BlobDB iterator.
     */
    BLOB_DB_NUM_SEEK,

    /**
     * # of Next to BlobDB iterator.
     */
    BLOB_DB_NUM_NEXT,

    /**
     * # of Prev to BlobDB iterator.
     */
    BLOB_DB_NUM_PREV,

    /**
     * # of keys written to BlobDB.
     */
    BLOB_DB_NUM_KEYS_WRITTEN,

    /**
     * # of keys read from BlobDB.
     */
    BLOB_DB_NUM_KEYS_READ,

    /**
     * # of bytes (key + value) written to BlobDB.
     */
    BLOB_DB_BYTES_WRITTEN,

    /**
     * # of bytes (keys + value) read from BlobDB.
     */
    BLOB_DB_BYTES_READ,

    /**
     * # of keys written by BlobDB as non-TTL inlined value.
     */
    BLOB_DB_WRITE_INLINED,

    /**
     * # of keys written by BlobDB as TTL inlined value.
     */
    BLOB_DB_WRITE_INLINED_TTL,

    /**
     * # of keys written by BlobDB as non-TTL blob value.
     */
    BLOB_DB_WRITE_BLOB,

    /**
     * # of keys written by BlobDB as TTL blob value.
     */
    BLOB_DB_WRITE_BLOB_TTL,

    /**
     * # of bytes written to blob file.
     */
    BLOB_DB_BLOB_FILE_BYTES_WRITTEN,

    /**
     * # of bytes read from blob file.
     */
    BLOB_DB_BLOB_FILE_BYTES_READ,

    /**
     * # of times a blob files being synced.
     */
    BLOB_DB_BLOB_FILE_SYNCED,

    /**
     * # of blob index evicted from base DB by BlobDB compaction filter because
     * of expiration.
     */
    BLOB_DB_BLOB_INDEX_EXPIRED_COUNT,

    /**
     * Size of blob index evicted from base DB by BlobDB compaction filter
     * because of expiration.
     */
    BLOB_DB_BLOB_INDEX_EXPIRED_SIZE,

    /**
     * # of blob index evicted from base DB by BlobDB compaction filter because
     * of corresponding file deleted.
     */
    BLOB_DB_BLOB_INDEX_EVICTED_COUNT,

    /**
     * Size of blob index evicted from base DB by BlobDB compaction filter
     * because of corresponding file deleted.
     */
    BLOB_DB_BLOB_INDEX_EVICTED_SIZE,

    /**
     * # of blob files being garbage collected.
     */
    BLOB_DB_GC_NUM_FILES,

    /**
     * # of blob files generated by garbage collection.
     */
    BLOB_DB_GC_NUM_NEW_FILES,

    /**
     * # of BlobDB garbage collection failures.
     */
    BLOB_DB_GC_FAILURES,

    /**
     * # of keys drop by BlobDB garbage collection because they had been
     * overwritten.
     */
    BLOB_DB_GC_NUM_KEYS_OVERWRITTEN,

    /**
     * # of keys drop by BlobDB garbage collection because of expiration.
     */
    BLOB_DB_GC_NUM_KEYS_EXPIRED,

    /**
     * # of keys relocated to new blob file by garbage collection.
     */
    BLOB_DB_GC_NUM_KEYS_RELOCATED,

    /**
     * # of bytes drop by BlobDB garbage collection because they had been
     * overwritten.
     */
    BLOB_DB_GC_BYTES_OVERWRITTEN,

    /**
     * # of bytes drop by BlobDB garbage collection because of expiration.
     */
    BLOB_DB_GC_BYTES_EXPIRED,

    /**
     * # of bytes relocated to new blob file by garbage collection.
     */
    BLOB_DB_GC_BYTES_RELOCATED,

    /**
     * # of blob files evicted because of BlobDB is full.
     */
    BLOB_DB_FIFO_NUM_FILES_EVICTED,

    /**
     * # of keys in the blob files evicted because of BlobDB is full.
     */
    BLOB_DB_FIFO_NUM_KEYS_EVICTED,

    /**
     * # of bytes in the blob files evicted because of BlobDB is full.
     */
    BLOB_DB_FIFO_BYTES_EVICTED,

    /**
     * These counters indicate a performance issue in WritePrepared transactions.
     * We should not seem them ticking them much.
     * # of times prepare_mutex_ is acquired in the fast path.
     */
    TXN_PREPARE_MUTEX_OVERHEAD,

    /**
     * # of times old_commit_map_mutex_ is acquired in the fast path.
     */
    TXN_OLD_COMMIT_MAP_MUTEX_OVERHEAD,

    /**
     * # of times we checked a batch for duplicate keys.
     */
    TXN_DUPLICATE_KEY_OVERHEAD,

    /**
     * # of times snapshot_mutex_ is acquired in the fast path.
     */
    TXN_SNAPSHOT_MUTEX_OVERHEAD,

    TICKER_ENUM_MAX
}
