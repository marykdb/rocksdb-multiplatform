@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_perfcontext_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import rocksdb.rocksdb_free
import rocksdb.rocksdb_perfcontext_create
import rocksdb.rocksdb_perfcontext_destroy
import rocksdb.rocksdb_perfcontext_metric
import rocksdb.rocksdb_perfcontext_report
import rocksdb.rocksdb_perfcontext_reset

actual class PerfContext internal constructor(
    internal val native: CPointer<rocksdb_perfcontext_t>,
) : RocksObject() {
    constructor() : this(rocksdb_perfcontext_create()!!)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_perfcontext_destroy(native)
            super.close()
        }
    }

    actual fun reset() {
        rocksdb_perfcontext_reset(native)
    }

    private fun metric(metric: PerfContextMetric): Long =
        rocksdb_perfcontext_metric(native, metric.id).toLong()

    actual fun getUserKeyComparisonCount(): Long = metric(PerfContextMetric.USER_KEY_COMPARISON_COUNT)

    actual fun getBlockCacheHitCount(): Long = metric(PerfContextMetric.BLOCK_CACHE_HIT_COUNT)

    actual fun getBlockReadCount(): Long = metric(PerfContextMetric.BLOCK_READ_COUNT)

    actual fun getBlockReadByte(): Long = metric(PerfContextMetric.BLOCK_READ_BYTE)

    actual fun getBlockReadTime(): Long = metric(PerfContextMetric.BLOCK_READ_TIME)

    actual fun getBlockReadCpuTime(): Long = metric(PerfContextMetric.BLOCK_READ_CPU_TIME)

    actual fun getBlockCacheIndexHitCount(): Long = metric(PerfContextMetric.BLOCK_CACHE_INDEX_HIT_COUNT)

    actual fun getBlockCacheStandaloneHandleCount(): Long =
        metric(PerfContextMetric.BLOCK_CACHE_STANDALONE_HANDLE_COUNT)

    actual fun getBlockCacheRealHandleCount(): Long =
        metric(PerfContextMetric.BLOCK_CACHE_REAL_HANDLE_COUNT)

    actual fun getIndexBlockReadCount(): Long = metric(PerfContextMetric.INDEX_BLOCK_READ_COUNT)

    actual fun getBlockCacheFilterHitCount(): Long = metric(PerfContextMetric.BLOCK_CACHE_FILTER_HIT_COUNT)

    actual fun getFilterBlockReadCount(): Long = metric(PerfContextMetric.FILTER_BLOCK_READ_COUNT)

    actual fun getCompressionDictBlockReadCount(): Long =
        metric(PerfContextMetric.COMPRESSION_DICT_BLOCK_READ_COUNT)

    actual fun getSecondaryCacheHitCount(): Long = metric(PerfContextMetric.SECONDARY_CACHE_HIT_COUNT)

    actual fun getCompressedSecCacheInsertRealCount(): Long =
        metric(PerfContextMetric.COMPRESSED_SEC_CACHE_INSERT_REAL_COUNT)

    actual fun getCompressedSecCacheInsertDummyCount(): Long =
        metric(PerfContextMetric.COMPRESSED_SEC_CACHE_INSERT_DUMMY_COUNT)

    actual fun getCompressedSecCacheUncompressedBytes(): Long =
        metric(PerfContextMetric.COMPRESSED_SEC_CACHE_UNCOMPRESSED_BYTES)

    actual fun getCompressedSecCacheCompressedBytes(): Long =
        metric(PerfContextMetric.COMPRESSED_SEC_CACHE_COMPRESSED_BYTES)

    actual fun getBlockChecksumTime(): Long = metric(PerfContextMetric.BLOCK_CHECKSUM_TIME)

    actual fun getBlockDecompressTime(): Long = metric(PerfContextMetric.BLOCK_DECOMPRESS_TIME)

    actual fun getReadBytes(): Long = metric(PerfContextMetric.GET_READ_BYTES)

    actual fun getMultigetReadBytes(): Long = metric(PerfContextMetric.MULTIGET_READ_BYTES)

    actual fun getIterReadBytes(): Long = metric(PerfContextMetric.ITER_READ_BYTES)

    actual fun getBlobCacheHitCount(): Long = metric(PerfContextMetric.BLOB_CACHE_HIT_COUNT)

    actual fun getBlobReadCount(): Long = metric(PerfContextMetric.BLOB_READ_COUNT)

    actual fun getBlobReadByte(): Long = metric(PerfContextMetric.BLOB_READ_BYTE)

    actual fun getBlobReadTime(): Long = metric(PerfContextMetric.BLOB_READ_TIME)

    actual fun getBlobChecksumTime(): Long = metric(PerfContextMetric.BLOB_CHECKSUM_TIME)

    actual fun getBlobDecompressTime(): Long = metric(PerfContextMetric.BLOB_DECOMPRESS_TIME)

    actual fun getInternalKeySkippedCount(): Long = metric(PerfContextMetric.INTERNAL_KEY_SKIPPED_COUNT)

    actual fun getInternalDeleteSkippedCount(): Long = metric(PerfContextMetric.INTERNAL_DELETE_SKIPPED_COUNT)

    actual fun getInternalRecentSkippedCount(): Long = metric(PerfContextMetric.INTERNAL_RECENT_SKIPPED_COUNT)

    actual fun getInternalMergeCount(): Long = metric(PerfContextMetric.INTERNAL_MERGE_COUNT)

    actual fun getInternalMergePointLookupCount(): Long =
        metric(PerfContextMetric.INTERNAL_MERGE_POINT_LOOKUP_COUNT)

    actual fun getInternalRangeDelReseekCount(): Long =
        metric(PerfContextMetric.INTERNAL_RANGE_DEL_RESEEK_COUNT)

    actual fun getSnapshotTime(): Long = metric(PerfContextMetric.GET_SNAPSHOT_TIME)

    actual fun getFromMemtableTime(): Long = metric(PerfContextMetric.GET_FROM_MEMTABLE_TIME)

    actual fun getFromMemtableCount(): Long = metric(PerfContextMetric.GET_FROM_MEMTABLE_COUNT)

    actual fun getPostProcessTime(): Long = metric(PerfContextMetric.GET_POST_PROCESS_TIME)

    actual fun getFromOutputFilesTime(): Long = metric(PerfContextMetric.GET_FROM_OUTPUT_FILES_TIME)

    actual fun getSeekOnMemtableTime(): Long = metric(PerfContextMetric.SEEK_ON_MEMTABLE_TIME)

    actual fun getSeekOnMemtableCount(): Long = metric(PerfContextMetric.SEEK_ON_MEMTABLE_COUNT)

    actual fun getNextOnMemtableCount(): Long = metric(PerfContextMetric.NEXT_ON_MEMTABLE_COUNT)

    actual fun getPrevOnMemtableCount(): Long = metric(PerfContextMetric.PREV_ON_MEMTABLE_COUNT)

    actual fun getSeekChildSeekTime(): Long = metric(PerfContextMetric.SEEK_CHILD_SEEK_TIME)

    actual fun getSeekChildSeekCount(): Long = metric(PerfContextMetric.SEEK_CHILD_SEEK_COUNT)

    actual fun getSeekMinHeapTime(): Long = metric(PerfContextMetric.SEEK_MIN_HEAP_TIME)

    actual fun getSeekMaxHeapTime(): Long = metric(PerfContextMetric.SEEK_MAX_HEAP_TIME)

    actual fun getSeekInternalSeekTime(): Long = metric(PerfContextMetric.SEEK_INTERNAL_SEEK_TIME)

    actual fun getFindNextUserEntryTime(): Long = metric(PerfContextMetric.FIND_NEXT_USER_ENTRY_TIME)

    actual fun getWriteWalTime(): Long = metric(PerfContextMetric.WRITE_WAL_TIME)

    actual fun getWriteMemtableTime(): Long = metric(PerfContextMetric.WRITE_MEMTABLE_TIME)

    actual fun getWriteDelayTime(): Long = metric(PerfContextMetric.WRITE_DELAY_TIME)

    actual fun getWriteSchedulingFlushesCompactionsTime(): Long =
        metric(PerfContextMetric.WRITE_SCHEDULING_FLUSHES_COMPACTIONS_TIME)

    actual fun getWritePreAndPostProcessTime(): Long =
        metric(PerfContextMetric.WRITE_PRE_AND_POST_PROCESS_TIME)

    actual fun getWriteThreadWaitNanos(): Long = metric(PerfContextMetric.WRITE_THREAD_WAIT_NANOS)

    actual fun getDbMutexLockNanos(): Long = metric(PerfContextMetric.DB_MUTEX_LOCK_NANOS)

    actual fun getDbConditionWaitNanos(): Long = metric(PerfContextMetric.DB_CONDITION_WAIT_NANOS)

    actual fun getMergeOperatorTimeNanos(): Long = metric(PerfContextMetric.MERGE_OPERATOR_TIME_NANOS)

    actual fun getReadIndexBlockNanos(): Long = metric(PerfContextMetric.READ_INDEX_BLOCK_NANOS)

    actual fun getReadFilterBlockNanos(): Long = metric(PerfContextMetric.READ_FILTER_BLOCK_NANOS)

    actual fun getNewTableBlockIterNanos(): Long = metric(PerfContextMetric.NEW_TABLE_BLOCK_ITER_NANOS)

    actual fun getNewTableIteratorNanos(): Long = metric(PerfContextMetric.NEW_TABLE_ITERATOR_NANOS)

    actual fun getBlockSeekNanos(): Long = metric(PerfContextMetric.BLOCK_SEEK_NANOS)

    actual fun getFindTableNanos(): Long = metric(PerfContextMetric.FIND_TABLE_NANOS)

    actual fun getBloomMemtableHitCount(): Long = metric(PerfContextMetric.BLOOM_MEMTABLE_HIT_COUNT)

    actual fun getBloomMemtableMissCount(): Long = metric(PerfContextMetric.BLOOM_MEMTABLE_MISS_COUNT)

    actual fun getBloomSstHitCount(): Long = metric(PerfContextMetric.BLOOM_SST_HIT_COUNT)

    actual fun getBloomSstMissCount(): Long = metric(PerfContextMetric.BLOOM_SST_MISS_COUNT)

    actual fun getKeyLockWaitTime(): Long = metric(PerfContextMetric.KEY_LOCK_WAIT_TIME)

    actual fun getKeyLockWaitCount(): Long = metric(PerfContextMetric.KEY_LOCK_WAIT_COUNT)

    actual fun getEnvNewSequentialFileNanos(): Long = metric(PerfContextMetric.ENV_NEW_SEQUENTIAL_FILE_NANOS)

    actual fun getEnvNewRandomAccessFileNanos(): Long =
        metric(PerfContextMetric.ENV_NEW_RANDOM_ACCESS_FILE_NANOS)

    actual fun getEnvNewWritableFileNanos(): Long = metric(PerfContextMetric.ENV_NEW_WRITABLE_FILE_NANOS)

    actual fun getEnvReuseWritableFileNanos(): Long = metric(PerfContextMetric.ENV_REUSE_WRITABLE_FILE_NANOS)

    actual fun getEnvNewRandomRwFileNanos(): Long = metric(PerfContextMetric.ENV_NEW_RANDOM_RW_FILE_NANOS)

    actual fun getEnvNewDirectoryNanos(): Long = metric(PerfContextMetric.ENV_NEW_DIRECTORY_NANOS)

    actual fun getEnvFileExistsNanos(): Long = metric(PerfContextMetric.ENV_FILE_EXISTS_NANOS)

    actual fun getEnvGetChildrenNanos(): Long = metric(PerfContextMetric.ENV_GET_CHILDREN_NANOS)

    actual fun getEnvGetChildrenFileAttributesNanos(): Long =
        metric(PerfContextMetric.ENV_GET_CHILDREN_FILE_ATTRIBUTES_NANOS)

    actual fun getEnvDeleteFileNanos(): Long = metric(PerfContextMetric.ENV_DELETE_FILE_NANOS)

    actual fun getEnvCreateDirNanos(): Long = metric(PerfContextMetric.ENV_CREATE_DIR_NANOS)

    actual fun getEnvCreateDirIfMissingNanos(): Long = metric(PerfContextMetric.ENV_CREATE_DIR_IF_MISSING_NANOS)

    actual fun getEnvDeleteDirNanos(): Long = metric(PerfContextMetric.ENV_DELETE_DIR_NANOS)

    actual fun getEnvGetFileSizeNanos(): Long = metric(PerfContextMetric.ENV_GET_FILE_SIZE_NANOS)

    actual fun getEnvGetFileModificationTimeNanos(): Long =
        metric(PerfContextMetric.ENV_GET_FILE_MODIFICATION_TIME_NANOS)

    actual fun getEnvRenameFileNanos(): Long = metric(PerfContextMetric.ENV_RENAME_FILE_NANOS)

    actual fun getEnvLinkFileNanos(): Long = metric(PerfContextMetric.ENV_LINK_FILE_NANOS)

    actual fun getEnvLockFileNanos(): Long = metric(PerfContextMetric.ENV_LOCK_FILE_NANOS)

    actual fun getEnvUnlockFileNanos(): Long = metric(PerfContextMetric.ENV_UNLOCK_FILE_NANOS)

    actual fun getEnvNewLoggerNanos(): Long = metric(PerfContextMetric.ENV_NEW_LOGGER_NANOS)

    actual fun getGetCpuNanos(): Long = metric(PerfContextMetric.GET_CPU_NANOS)

    actual fun getIterNextCpuNanos(): Long = metric(PerfContextMetric.ITER_NEXT_CPU_NANOS)

    actual fun getIterPrevCpuNanos(): Long = metric(PerfContextMetric.ITER_PREV_CPU_NANOS)

    actual fun getIterSeekCpuNanos(): Long = metric(PerfContextMetric.ITER_SEEK_CPU_NANOS)

    actual fun getEncryptDataNanos(): Long = metric(PerfContextMetric.ENCRYPT_DATA_NANOS)

    actual fun getDecryptDataNanos(): Long = metric(PerfContextMetric.DECRYPT_DATA_NANOS)

    actual fun getNumberAsyncSeek(): Long = metric(PerfContextMetric.NUMBER_ASYNC_SEEK)

    actual fun toString(excludeZeroCounters: Boolean): String {
        val raw = rocksdb_perfcontext_report(native, if (excludeZeroCounters) 1u else 0u)
        return raw?.toKString().also { rocksdb_free(raw) } ?: ""
    }

    private enum class PerfContextMetric(val id: Int) {
        USER_KEY_COMPARISON_COUNT(0),
        BLOCK_CACHE_HIT_COUNT(1),
        BLOCK_READ_COUNT(2),
        BLOCK_READ_BYTE(3),
        BLOCK_READ_TIME(4),
        BLOCK_READ_CPU_TIME(5),
        BLOCK_CACHE_INDEX_HIT_COUNT(6),
        BLOCK_CACHE_STANDALONE_HANDLE_COUNT(7),
        BLOCK_CACHE_REAL_HANDLE_COUNT(8),
        INDEX_BLOCK_READ_COUNT(9),
        BLOCK_CACHE_FILTER_HIT_COUNT(10),
        FILTER_BLOCK_READ_COUNT(11),
        COMPRESSION_DICT_BLOCK_READ_COUNT(12),
        SECONDARY_CACHE_HIT_COUNT(17),
        COMPRESSED_SEC_CACHE_INSERT_REAL_COUNT(18),
        COMPRESSED_SEC_CACHE_INSERT_DUMMY_COUNT(19),
        COMPRESSED_SEC_CACHE_UNCOMPRESSED_BYTES(20),
        COMPRESSED_SEC_CACHE_COMPRESSED_BYTES(21),
        BLOCK_CHECKSUM_TIME(22),
        BLOCK_DECOMPRESS_TIME(23),
        GET_READ_BYTES(24),
        MULTIGET_READ_BYTES(25),
        ITER_READ_BYTES(26),
        BLOB_CACHE_HIT_COUNT(27),
        BLOB_READ_COUNT(28),
        BLOB_READ_BYTE(29),
        BLOB_READ_TIME(30),
        BLOB_CHECKSUM_TIME(31),
        BLOB_DECOMPRESS_TIME(32),
        INTERNAL_KEY_SKIPPED_COUNT(33),
        INTERNAL_DELETE_SKIPPED_COUNT(34),
        INTERNAL_RECENT_SKIPPED_COUNT(35),
        INTERNAL_MERGE_COUNT(36),
        INTERNAL_MERGE_POINT_LOOKUP_COUNT(37),
        INTERNAL_RANGE_DEL_RESEEK_COUNT(38),
        GET_SNAPSHOT_TIME(39),
        GET_FROM_MEMTABLE_TIME(40),
        GET_FROM_MEMTABLE_COUNT(41),
        GET_POST_PROCESS_TIME(42),
        GET_FROM_OUTPUT_FILES_TIME(43),
        SEEK_ON_MEMTABLE_TIME(44),
        SEEK_ON_MEMTABLE_COUNT(45),
        NEXT_ON_MEMTABLE_COUNT(46),
        PREV_ON_MEMTABLE_COUNT(47),
        SEEK_CHILD_SEEK_TIME(48),
        SEEK_CHILD_SEEK_COUNT(49),
        SEEK_MIN_HEAP_TIME(50),
        SEEK_MAX_HEAP_TIME(51),
        SEEK_INTERNAL_SEEK_TIME(52),
        FIND_NEXT_USER_ENTRY_TIME(53),
        WRITE_WAL_TIME(54),
        WRITE_MEMTABLE_TIME(55),
        WRITE_DELAY_TIME(56),
        WRITE_SCHEDULING_FLUSHES_COMPACTIONS_TIME(57),
        WRITE_PRE_AND_POST_PROCESS_TIME(58),
        WRITE_THREAD_WAIT_NANOS(59),
        DB_MUTEX_LOCK_NANOS(60),
        DB_CONDITION_WAIT_NANOS(61),
        MERGE_OPERATOR_TIME_NANOS(62),
        READ_INDEX_BLOCK_NANOS(63),
        READ_FILTER_BLOCK_NANOS(64),
        NEW_TABLE_BLOCK_ITER_NANOS(65),
        NEW_TABLE_ITERATOR_NANOS(66),
        BLOCK_SEEK_NANOS(67),
        FIND_TABLE_NANOS(68),
        BLOOM_MEMTABLE_HIT_COUNT(69),
        BLOOM_MEMTABLE_MISS_COUNT(70),
        BLOOM_SST_HIT_COUNT(71),
        BLOOM_SST_MISS_COUNT(72),
        KEY_LOCK_WAIT_TIME(73),
        KEY_LOCK_WAIT_COUNT(74),
        ENV_NEW_SEQUENTIAL_FILE_NANOS(75),
        ENV_NEW_RANDOM_ACCESS_FILE_NANOS(76),
        ENV_NEW_WRITABLE_FILE_NANOS(77),
        ENV_REUSE_WRITABLE_FILE_NANOS(78),
        ENV_NEW_RANDOM_RW_FILE_NANOS(79),
        ENV_NEW_DIRECTORY_NANOS(80),
        ENV_FILE_EXISTS_NANOS(81),
        ENV_GET_CHILDREN_NANOS(82),
        ENV_GET_CHILDREN_FILE_ATTRIBUTES_NANOS(83),
        ENV_DELETE_FILE_NANOS(84),
        ENV_CREATE_DIR_NANOS(85),
        ENV_CREATE_DIR_IF_MISSING_NANOS(86),
        ENV_DELETE_DIR_NANOS(87),
        ENV_GET_FILE_SIZE_NANOS(88),
        ENV_GET_FILE_MODIFICATION_TIME_NANOS(89),
        ENV_RENAME_FILE_NANOS(90),
        ENV_LINK_FILE_NANOS(91),
        ENV_LOCK_FILE_NANOS(92),
        ENV_UNLOCK_FILE_NANOS(93),
        ENV_NEW_LOGGER_NANOS(94),
        GET_CPU_NANOS(95),
        ITER_NEXT_CPU_NANOS(96),
        ITER_PREV_CPU_NANOS(97),
        ITER_SEEK_CPU_NANOS(98),
        ENCRYPT_DATA_NANOS(102),
        DECRYPT_DATA_NANOS(103),
        NUMBER_ASYNC_SEEK(104);

        companion object {
            init {
                require(values().distinctBy { it.id }.size == values().size)
            }
        }
    }
}
