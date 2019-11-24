package maryk.rocksdb

import rocksdb.RocksDBTicker
import rocksdb.RocksDBTickerBlobDBBlobFileBytesRead
import rocksdb.RocksDBTickerBlobDBBlobFileBytesWritten
import rocksdb.RocksDBTickerBlobDBBlobFileSynced
import rocksdb.RocksDBTickerBlobDBBlobIndexEvictedCount
import rocksdb.RocksDBTickerBlobDBBlobIndexEvictedSize
import rocksdb.RocksDBTickerBlobDBBlobIndexExpiredCount
import rocksdb.RocksDBTickerBlobDBBlobIndexExpiredSize
import rocksdb.RocksDBTickerBlobDBBytesRead
import rocksdb.RocksDBTickerBlobDBBytesWritten
import rocksdb.RocksDBTickerBlobDBFifoBytesEvicted
import rocksdb.RocksDBTickerBlobDBFifoNumFilesEvicted
import rocksdb.RocksDBTickerBlobDBFifoNumKeysEvicted
import rocksdb.RocksDBTickerBlobDBGCBytesExpired
import rocksdb.RocksDBTickerBlobDBGCBytesOverwritten
import rocksdb.RocksDBTickerBlobDBGCBytesRelocated
import rocksdb.RocksDBTickerBlobDBGCFailures
import rocksdb.RocksDBTickerBlobDBGCNumFiles
import rocksdb.RocksDBTickerBlobDBGCNumKeysExpired
import rocksdb.RocksDBTickerBlobDBGCNumKeysOverwritten
import rocksdb.RocksDBTickerBlobDBGCNumKeysRelocated
import rocksdb.RocksDBTickerBlobDBGCNumNewFiles
import rocksdb.RocksDBTickerBlobDBNumGet
import rocksdb.RocksDBTickerBlobDBNumKeysRead
import rocksdb.RocksDBTickerBlobDBNumKeysWritten
import rocksdb.RocksDBTickerBlobDBNumMultiget
import rocksdb.RocksDBTickerBlobDBNumNext
import rocksdb.RocksDBTickerBlobDBNumPrev
import rocksdb.RocksDBTickerBlobDBNumPut
import rocksdb.RocksDBTickerBlobDBNumSeek
import rocksdb.RocksDBTickerBlobDBNumWrite
import rocksdb.RocksDBTickerBlobDBWriteBlob
import rocksdb.RocksDBTickerBlobDBWriteBlobTTL
import rocksdb.RocksDBTickerBlobDBWriteInlined
import rocksdb.RocksDBTickerBlobDBWriteInlinedTTL
import rocksdb.RocksDBTickerBlockCacheAdd
import rocksdb.RocksDBTickerBlockCacheAddFailures
import rocksdb.RocksDBTickerBlockCacheBytesRead
import rocksdb.RocksDBTickerBlockCacheBytesWrite
import rocksdb.RocksDBTickerBlockCacheCompressedAdd
import rocksdb.RocksDBTickerBlockCacheCompressedHit
import rocksdb.RocksDBTickerBlockCacheCompressedMiss
import rocksdb.RocksDBTickerBlockCacheDataAdd
import rocksdb.RocksDBTickerBlockCacheDataBytesInsert
import rocksdb.RocksDBTickerBlockCacheDataHit
import rocksdb.RocksDBTickerBlockCacheDataMiss
import rocksdb.RocksDBTickerBlockCacheFilterAdd
import rocksdb.RocksDBTickerBlockCacheFilterBytesEvict
import rocksdb.RocksDBTickerBlockCacheFilterBytesInsert
import rocksdb.RocksDBTickerBlockCacheFilterHit
import rocksdb.RocksDBTickerBlockCacheFilterMiss
import rocksdb.RocksDBTickerBlockCacheHit
import rocksdb.RocksDBTickerBlockCacheIndexAdd
import rocksdb.RocksDBTickerBlockCacheIndexBytesEvict
import rocksdb.RocksDBTickerBlockCacheIndexBytesInsert
import rocksdb.RocksDBTickerBlockCacheIndexHit
import rocksdb.RocksDBTickerBlockCacheIndexMiss
import rocksdb.RocksDBTickerBlockCacheMiss
import rocksdb.RocksDBTickerBloomFilterFullPositive
import rocksdb.RocksDBTickerBloomFilterFullTruePositive
import rocksdb.RocksDBTickerBloomFilterMicros
import rocksdb.RocksDBTickerBloomFilterPrefixChecked
import rocksdb.RocksDBTickerBloomFilterUseful
import rocksdb.RocksDBTickerBytesRead
import rocksdb.RocksDBTickerBytesWritten
import rocksdb.RocksDBTickerCompactReadBytes
import rocksdb.RocksDBTickerCompactWriteBytes
import rocksdb.RocksDBTickerCompactionCancelled
import rocksdb.RocksDBTickerCompactionKeyDropNewerEntry
import rocksdb.RocksDBTickerCompactionKeyDropObsolete
import rocksdb.RocksDBTickerCompactionKeyDropUser
import rocksdb.RocksDBTickerCompactionKeyRangeDelete
import rocksdb.RocksDBTickerCompactionOptimizedDelDropObsolete
import rocksdb.RocksDBTickerCompactionRangeDelDropObsolete
import rocksdb.RocksDBTickerDBMutexWaitMicros
import rocksdb.RocksDBTickerEnumMax
import rocksdb.RocksDBTickerFilterOperationTotalTime
import rocksdb.RocksDBTickerFlushWriteBytes
import rocksdb.RocksDBTickerGetHit_L0
import rocksdb.RocksDBTickerGetHit_L1
import rocksdb.RocksDBTickerGetHit_L2_AndUp
import rocksdb.RocksDBTickerGetUpdatesSinceCalls
import rocksdb.RocksDBTickerIterBytesRead
import rocksdb.RocksDBTickerMemtableHit
import rocksdb.RocksDBTickerMemtableMiss
import rocksdb.RocksDBTickerMergeOprationTotalTime
import rocksdb.RocksDBTickerNoFileCloses
import rocksdb.RocksDBTickerNoFileErrors
import rocksdb.RocksDBTickerNoFileOpens
import rocksdb.RocksDBTickerNoIteratorCreated
import rocksdb.RocksDBTickerNoIteratorDeleted
import rocksdb.RocksDBTickerNoIterators
import rocksdb.RocksDBTickerNumberBlockCompressed
import rocksdb.RocksDBTickerNumberBlockDecompressed
import rocksdb.RocksDBTickerNumberBlockNotCompressed
import rocksdb.RocksDBTickerNumberDBNext
import rocksdb.RocksDBTickerNumberDBNextFound
import rocksdb.RocksDBTickerNumberDBPrevious
import rocksdb.RocksDBTickerNumberDBPreviousFound
import rocksdb.RocksDBTickerNumberDBSeek
import rocksdb.RocksDBTickerNumberDBSeekFound
import rocksdb.RocksDBTickerNumberDirectLoadTableProperties
import rocksdb.RocksDBTickerNumberFilteredDeletes
import rocksdb.RocksDBTickerNumberIterSkip
import rocksdb.RocksDBTickerNumberKeysRead
import rocksdb.RocksDBTickerNumberKeysUpdated
import rocksdb.RocksDBTickerNumberKeysWritten
import rocksdb.RocksDBTickerNumberMergeFailures
import rocksdb.RocksDBTickerNumberMultigetBytesRead
import rocksdb.RocksDBTickerNumberMultigetCalls
import rocksdb.RocksDBTickerNumberMultigetKeysRead
import rocksdb.RocksDBTickerNumberOfReseeksInIteration
import rocksdb.RocksDBTickerNumberRateLimiterDrains
import rocksdb.RocksDBTickerNumberSuperversionAcquires
import rocksdb.RocksDBTickerNumberSuperversionCleanups
import rocksdb.RocksDBTickerNumberSuperversionReleases
import rocksdb.RocksDBTickerPersistentCacheHit
import rocksdb.RocksDBTickerPersistentCacheMiss
import rocksdb.RocksDBTickerRateLimitDelayMillis
import rocksdb.RocksDBTickerReadAmpEstimateUsefulBytes
import rocksdb.RocksDBTickerReadAmpTotalReadBytes
import rocksdb.RocksDBTickerRowCacheHit
import rocksdb.RocksDBTickerRowCacheMiss
import rocksdb.RocksDBTickerSimBlockCacheHit
import rocksdb.RocksDBTickerSimBlockCacheMiss
import rocksdb.RocksDBTickerStallMicros
import rocksdb.RocksDBTickerTxnDuplicateKeyOverhead
import rocksdb.RocksDBTickerTxnOldCommitMapMutexOverhead
import rocksdb.RocksDBTickerTxnPrepareMutexOverhead
import rocksdb.RocksDBTickerTxnSnapshotMutexOverhead
import rocksdb.RocksDBTickerWalFileBytes
import rocksdb.RocksDBTickerWalFileSynced
import rocksdb.RocksDBTickerWriteDoneByOther
import rocksdb.RocksDBTickerWriteDoneBySelf
import rocksdb.RocksDBTickerWriteTimedout
import rocksdb.RocksDBTickerWriteWithWal

actual enum class TickerType(
    internal val value: RocksDBTicker
) {
    BLOCK_CACHE_MISS(RocksDBTickerBlockCacheMiss),
    BLOCK_CACHE_HIT(RocksDBTickerBlockCacheHit),
    BLOCK_CACHE_ADD(RocksDBTickerBlockCacheAdd),
    BLOCK_CACHE_ADD_FAILURES(RocksDBTickerBlockCacheAddFailures),
    BLOCK_CACHE_INDEX_MISS(RocksDBTickerBlockCacheIndexMiss),
    BLOCK_CACHE_INDEX_HIT(RocksDBTickerBlockCacheIndexHit),
    BLOCK_CACHE_INDEX_ADD(RocksDBTickerBlockCacheIndexAdd),
    BLOCK_CACHE_INDEX_BYTES_INSERT(RocksDBTickerBlockCacheIndexBytesInsert),
    BLOCK_CACHE_INDEX_BYTES_EVICT(RocksDBTickerBlockCacheIndexBytesEvict),
    BLOCK_CACHE_FILTER_MISS(RocksDBTickerBlockCacheFilterMiss),
    BLOCK_CACHE_FILTER_HIT(RocksDBTickerBlockCacheFilterHit),
    BLOCK_CACHE_FILTER_ADD(RocksDBTickerBlockCacheFilterAdd),
    BLOCK_CACHE_FILTER_BYTES_INSERT(RocksDBTickerBlockCacheFilterBytesInsert),
    BLOCK_CACHE_FILTER_BYTES_EVICT(RocksDBTickerBlockCacheFilterBytesEvict),
    BLOCK_CACHE_DATA_MISS(RocksDBTickerBlockCacheDataMiss),
    BLOCK_CACHE_DATA_HIT(RocksDBTickerBlockCacheDataHit),
    BLOCK_CACHE_DATA_ADD(RocksDBTickerBlockCacheDataAdd),
    BLOCK_CACHE_DATA_BYTES_INSERT(RocksDBTickerBlockCacheDataBytesInsert),
    BLOCK_CACHE_BYTES_READ(RocksDBTickerBlockCacheBytesRead),
    BLOCK_CACHE_BYTES_WRITE(RocksDBTickerBlockCacheBytesWrite),
    BLOOM_FILTER_USEFUL(RocksDBTickerBloomFilterUseful),
    BLOOM_FILTER_FULL_POSITIVE(RocksDBTickerBloomFilterFullPositive),
    BLOOM_FILTER_FULL_TRUE_POSITIVE(RocksDBTickerBloomFilterFullTruePositive),
    BLOOM_FILTER_MICROS(RocksDBTickerBloomFilterMicros),
    PERSISTENT_CACHE_HIT(RocksDBTickerPersistentCacheHit),
    PERSISTENT_CACHE_MISS(RocksDBTickerPersistentCacheMiss),
    SIM_BLOCK_CACHE_HIT(RocksDBTickerSimBlockCacheHit),
    SIM_BLOCK_CACHE_MISS(RocksDBTickerSimBlockCacheMiss),
    MEMTABLE_HIT(RocksDBTickerMemtableHit),
    MEMTABLE_MISS(RocksDBTickerMemtableMiss),
    GET_HIT_L0(RocksDBTickerGetHit_L0),
    GET_HIT_L1(RocksDBTickerGetHit_L1),
    GET_HIT_L2_AND_UP(RocksDBTickerGetHit_L2_AndUp),
    COMPACTION_KEY_DROP_NEWER_ENTRY(RocksDBTickerCompactionKeyDropNewerEntry),
    COMPACTION_KEY_DROP_OBSOLETE(RocksDBTickerCompactionKeyDropObsolete),
    COMPACTION_KEY_DROP_RANGE_DEL(RocksDBTickerCompactionKeyRangeDelete),
    COMPACTION_KEY_DROP_USER(RocksDBTickerCompactionKeyDropUser),
    COMPACTION_RANGE_DEL_DROP_OBSOLETE(RocksDBTickerCompactionRangeDelDropObsolete),
    COMPACTION_OPTIMIZED_DEL_DROP_OBSOLETE(RocksDBTickerCompactionOptimizedDelDropObsolete),
    COMPACTION_CANCELLED(RocksDBTickerCompactionCancelled),
    NUMBER_KEYS_WRITTEN(RocksDBTickerNumberKeysWritten),
    NUMBER_KEYS_READ(RocksDBTickerNumberKeysRead),
    NUMBER_KEYS_UPDATED(RocksDBTickerNumberKeysUpdated),
    BYTES_WRITTEN(RocksDBTickerBytesWritten),
    BYTES_READ(RocksDBTickerBytesRead),
    NUMBER_DB_SEEK(RocksDBTickerNumberDBSeek),
    NUMBER_DB_NEXT(RocksDBTickerNumberDBNext),
    NUMBER_DB_PREV(RocksDBTickerNumberDBPrevious),
    NUMBER_DB_SEEK_FOUND(RocksDBTickerNumberDBSeekFound),
    NUMBER_DB_NEXT_FOUND(RocksDBTickerNumberDBNextFound),
    NUMBER_DB_PREV_FOUND(RocksDBTickerNumberDBPreviousFound),
    ITER_BYTES_READ(RocksDBTickerIterBytesRead),
    NO_FILE_CLOSES(RocksDBTickerNoFileCloses),
    NO_FILE_OPENS(RocksDBTickerNoFileOpens),
    NO_FILE_ERRORS(RocksDBTickerNoFileErrors),
    STALL_MICROS(RocksDBTickerStallMicros),
    DB_MUTEX_WAIT_MICROS(RocksDBTickerDBMutexWaitMicros),
    RATE_LIMIT_DELAY_MILLIS(RocksDBTickerRateLimitDelayMillis),
    NO_ITERATORS(RocksDBTickerNoIterators),
    NUMBER_MULTIGET_CALLS(RocksDBTickerNumberMultigetCalls),
    NUMBER_MULTIGET_KEYS_READ(RocksDBTickerNumberMultigetKeysRead),
    NUMBER_MULTIGET_BYTES_READ(RocksDBTickerNumberMultigetBytesRead),
    NUMBER_FILTERED_DELETES(RocksDBTickerNumberFilteredDeletes),
    NUMBER_MERGE_FAILURES(RocksDBTickerNumberMergeFailures),
    BLOOM_FILTER_PREFIX_CHECKED(RocksDBTickerBloomFilterPrefixChecked),
    BLOOM_FILTER_PREFIX_USEFUL(RocksDBTickerBloomFilterUseful),
    NUMBER_OF_RESEEKS_IN_ITERATION(RocksDBTickerNumberOfReseeksInIteration),
    GET_UPDATES_SINCE_CALLS(RocksDBTickerGetUpdatesSinceCalls),
    BLOCK_CACHE_COMPRESSED_MISS(RocksDBTickerBlockCacheCompressedMiss),
    BLOCK_CACHE_COMPRESSED_HIT(RocksDBTickerBlockCacheCompressedHit),
    BLOCK_CACHE_COMPRESSED_ADD(RocksDBTickerBlockCacheCompressedAdd),
    BLOCK_CACHE_COMPRESSED_ADD_FAILURES(RocksDBTickerBlockCacheAddFailures),
    WAL_FILE_SYNCED(RocksDBTickerWalFileSynced),
    WAL_FILE_BYTES(RocksDBTickerWalFileBytes),
    WRITE_DONE_BY_SELF(RocksDBTickerWriteDoneBySelf),
    WRITE_DONE_BY_OTHER(RocksDBTickerWriteDoneByOther),
    WRITE_TIMEDOUT(RocksDBTickerWriteTimedout),
    WRITE_WITH_WAL(RocksDBTickerWriteWithWal),
    COMPACT_READ_BYTES(RocksDBTickerCompactReadBytes),
    COMPACT_WRITE_BYTES(RocksDBTickerCompactWriteBytes),
    FLUSH_WRITE_BYTES(RocksDBTickerFlushWriteBytes),
    NUMBER_DIRECT_LOAD_TABLE_PROPERTIES(RocksDBTickerNumberDirectLoadTableProperties),
    NUMBER_SUPERVERSION_ACQUIRES(RocksDBTickerNumberSuperversionAcquires),
    NUMBER_SUPERVERSION_RELEASES(RocksDBTickerNumberSuperversionReleases),
    NUMBER_SUPERVERSION_CLEANUPS(RocksDBTickerNumberSuperversionCleanups),
    NUMBER_BLOCK_COMPRESSED(RocksDBTickerNumberBlockCompressed),
    NUMBER_BLOCK_DECOMPRESSED(RocksDBTickerNumberBlockDecompressed),
    NUMBER_BLOCK_NOT_COMPRESSED(RocksDBTickerNumberBlockNotCompressed),
    MERGE_OPERATION_TOTAL_TIME(RocksDBTickerMergeOprationTotalTime),
    FILTER_OPERATION_TOTAL_TIME(RocksDBTickerFilterOperationTotalTime),
    ROW_CACHE_HIT(RocksDBTickerRowCacheHit),
    ROW_CACHE_MISS(RocksDBTickerRowCacheMiss),
    READ_AMP_ESTIMATE_USEFUL_BYTES(RocksDBTickerReadAmpEstimateUsefulBytes),
    READ_AMP_TOTAL_READ_BYTES(RocksDBTickerReadAmpTotalReadBytes),
    NUMBER_RATE_LIMITER_DRAINS(RocksDBTickerNumberRateLimiterDrains),
    NUMBER_ITER_SKIP(RocksDBTickerNumberIterSkip),
    NUMBER_MULTIGET_KEYS_FOUND(RocksDBTickerNumberMultigetKeysRead),
    // -0x01 to fixate the new value that incorrectly changed TICKER_ENUM_MAX
    NO_ITERATOR_CREATED(RocksDBTickerNoIteratorCreated),
    NO_ITERATOR_DELETED(RocksDBTickerNoIteratorDeleted),
    BLOB_DB_NUM_PUT(RocksDBTickerBlobDBNumPut),
    BLOB_DB_NUM_WRITE(RocksDBTickerBlobDBNumWrite),
    BLOB_DB_NUM_GET(RocksDBTickerBlobDBNumGet),
    BLOB_DB_NUM_MULTIGET(RocksDBTickerBlobDBNumMultiget),
    BLOB_DB_NUM_SEEK(RocksDBTickerBlobDBNumSeek),
    BLOB_DB_NUM_NEXT(RocksDBTickerBlobDBNumNext),
    BLOB_DB_NUM_PREV(RocksDBTickerBlobDBNumPrev),
    BLOB_DB_NUM_KEYS_WRITTEN(RocksDBTickerBlobDBNumKeysWritten),
    BLOB_DB_NUM_KEYS_READ(RocksDBTickerBlobDBNumKeysRead),
    BLOB_DB_BYTES_WRITTEN(RocksDBTickerBlobDBBytesWritten),
    BLOB_DB_BYTES_READ(RocksDBTickerBlobDBBytesRead),
    BLOB_DB_WRITE_INLINED(RocksDBTickerBlobDBWriteInlined),
    BLOB_DB_WRITE_INLINED_TTL(RocksDBTickerBlobDBWriteInlinedTTL),
    BLOB_DB_WRITE_BLOB(RocksDBTickerBlobDBWriteBlob),
    BLOB_DB_WRITE_BLOB_TTL(RocksDBTickerBlobDBWriteBlobTTL),
    BLOB_DB_BLOB_FILE_BYTES_WRITTEN(RocksDBTickerBlobDBBlobFileBytesWritten),
    BLOB_DB_BLOB_FILE_BYTES_READ(RocksDBTickerBlobDBBlobFileBytesRead),
    BLOB_DB_BLOB_FILE_SYNCED(RocksDBTickerBlobDBBlobFileSynced),
    BLOB_DB_BLOB_INDEX_EXPIRED_COUNT(RocksDBTickerBlobDBBlobIndexExpiredCount),
    BLOB_DB_BLOB_INDEX_EXPIRED_SIZE(RocksDBTickerBlobDBBlobIndexExpiredSize),
    BLOB_DB_BLOB_INDEX_EVICTED_COUNT(RocksDBTickerBlobDBBlobIndexEvictedCount),
    BLOB_DB_BLOB_INDEX_EVICTED_SIZE(RocksDBTickerBlobDBBlobIndexEvictedSize),
    BLOB_DB_GC_NUM_FILES(RocksDBTickerBlobDBGCNumFiles),
    BLOB_DB_GC_NUM_NEW_FILES(RocksDBTickerBlobDBGCNumNewFiles),
    BLOB_DB_GC_FAILURES(RocksDBTickerBlobDBGCFailures),
    BLOB_DB_GC_NUM_KEYS_OVERWRITTEN(RocksDBTickerBlobDBGCNumKeysOverwritten),
    BLOB_DB_GC_NUM_KEYS_EXPIRED(RocksDBTickerBlobDBGCNumKeysExpired),
    BLOB_DB_GC_NUM_KEYS_RELOCATED(RocksDBTickerBlobDBGCNumKeysRelocated),
    BLOB_DB_GC_BYTES_OVERWRITTEN(RocksDBTickerBlobDBGCBytesOverwritten),
    BLOB_DB_GC_BYTES_EXPIRED(RocksDBTickerBlobDBGCBytesExpired),
    BLOB_DB_GC_BYTES_RELOCATED(RocksDBTickerBlobDBGCBytesRelocated),
    BLOB_DB_FIFO_NUM_FILES_EVICTED(RocksDBTickerBlobDBFifoNumFilesEvicted),
    BLOB_DB_FIFO_NUM_KEYS_EVICTED(RocksDBTickerBlobDBFifoNumKeysEvicted),
    BLOB_DB_FIFO_BYTES_EVICTED(RocksDBTickerBlobDBFifoBytesEvicted),
    TXN_PREPARE_MUTEX_OVERHEAD(RocksDBTickerTxnPrepareMutexOverhead),
    TXN_OLD_COMMIT_MAP_MUTEX_OVERHEAD(RocksDBTickerTxnOldCommitMapMutexOverhead),
    TXN_DUPLICATE_KEY_OVERHEAD(RocksDBTickerTxnDuplicateKeyOverhead),
    TXN_SNAPSHOT_MUTEX_OVERHEAD(RocksDBTickerTxnSnapshotMutexOverhead),
    TICKER_ENUM_MAX(RocksDBTickerEnumMax)
}
