package maryk.rocksdb

/**
 * Snapshot of RocksDB perf context counters.
 */
expect class PerfContext : RocksObject {
    fun reset()

    fun getUserKeyComparisonCount(): Long
    fun getBlockCacheHitCount(): Long
    fun getBlockReadCount(): Long
    fun getBlockReadByte(): Long
    fun getBlockReadTime(): Long
    fun getBlockReadCpuTime(): Long
    fun getBlockCacheIndexHitCount(): Long
    fun getBlockCacheStandaloneHandleCount(): Long
    fun getBlockCacheRealHandleCount(): Long
    fun getIndexBlockReadCount(): Long
    fun getBlockCacheFilterHitCount(): Long
    fun getFilterBlockReadCount(): Long
    fun getCompressionDictBlockReadCount(): Long
    fun getSecondaryCacheHitCount(): Long
    fun getCompressedSecCacheInsertRealCount(): Long
    fun getCompressedSecCacheInsertDummyCount(): Long
    fun getCompressedSecCacheUncompressedBytes(): Long
    fun getCompressedSecCacheCompressedBytes(): Long
    fun getBlockChecksumTime(): Long
    fun getBlockDecompressTime(): Long
    fun getReadBytes(): Long
    fun getMultigetReadBytes(): Long
    fun getIterReadBytes(): Long
    fun getBlobCacheHitCount(): Long
    fun getBlobReadCount(): Long
    fun getBlobReadByte(): Long
    fun getBlobReadTime(): Long
    fun getBlobChecksumTime(): Long
    fun getBlobDecompressTime(): Long
    fun getInternalKeySkippedCount(): Long
    fun getInternalDeleteSkippedCount(): Long
    fun getInternalRecentSkippedCount(): Long
    fun getInternalMergeCount(): Long
    fun getInternalMergePointLookupCount(): Long
    fun getInternalRangeDelReseekCount(): Long
    fun getSnapshotTime(): Long
    fun getFromMemtableTime(): Long
    fun getFromMemtableCount(): Long
    fun getPostProcessTime(): Long
    fun getFromOutputFilesTime(): Long
    fun getSeekOnMemtableTime(): Long
    fun getSeekOnMemtableCount(): Long
    fun getNextOnMemtableCount(): Long
    fun getPrevOnMemtableCount(): Long
    fun getSeekChildSeekTime(): Long
    fun getSeekChildSeekCount(): Long
    fun getSeekMinHeapTime(): Long
    fun getSeekMaxHeapTime(): Long
    fun getSeekInternalSeekTime(): Long
    fun getFindNextUserEntryTime(): Long
    fun getWriteWalTime(): Long
    fun getWriteMemtableTime(): Long
    fun getWriteDelayTime(): Long
    fun getWriteSchedulingFlushesCompactionsTime(): Long
    fun getWritePreAndPostProcessTime(): Long
    fun getWriteThreadWaitNanos(): Long
    fun getDbMutexLockNanos(): Long
    fun getDbConditionWaitNanos(): Long
    fun getMergeOperatorTimeNanos(): Long
    fun getReadIndexBlockNanos(): Long
    fun getReadFilterBlockNanos(): Long
    fun getNewTableBlockIterNanos(): Long
    fun getNewTableIteratorNanos(): Long
    fun getBlockSeekNanos(): Long
    fun getFindTableNanos(): Long
    fun getBloomMemtableHitCount(): Long
    fun getBloomMemtableMissCount(): Long
    fun getBloomSstHitCount(): Long
    fun getBloomSstMissCount(): Long
    fun getKeyLockWaitTime(): Long
    fun getKeyLockWaitCount(): Long
    fun getEnvNewSequentialFileNanos(): Long
    fun getEnvNewRandomAccessFileNanos(): Long
    fun getEnvNewWritableFileNanos(): Long
    fun getEnvReuseWritableFileNanos(): Long
    fun getEnvNewRandomRwFileNanos(): Long
    fun getEnvNewDirectoryNanos(): Long
    fun getEnvFileExistsNanos(): Long
    fun getEnvGetChildrenNanos(): Long
    fun getEnvGetChildrenFileAttributesNanos(): Long
    fun getEnvDeleteFileNanos(): Long
    fun getEnvCreateDirNanos(): Long
    fun getEnvCreateDirIfMissingNanos(): Long
    fun getEnvDeleteDirNanos(): Long
    fun getEnvGetFileSizeNanos(): Long
    fun getEnvGetFileModificationTimeNanos(): Long
    fun getEnvRenameFileNanos(): Long
    fun getEnvLinkFileNanos(): Long
    fun getEnvLockFileNanos(): Long
    fun getEnvUnlockFileNanos(): Long
    fun getEnvNewLoggerNanos(): Long
    fun getGetCpuNanos(): Long
    fun getIterNextCpuNanos(): Long
    fun getIterPrevCpuNanos(): Long
    fun getIterSeekCpuNanos(): Long
    fun getEncryptDataNanos(): Long
    fun getDecryptDataNanos(): Long
    fun getNumberAsyncSeek(): Long

    fun toString(excludeZeroCounters: Boolean): String
}
