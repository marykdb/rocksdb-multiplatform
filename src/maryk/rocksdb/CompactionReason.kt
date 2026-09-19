package maryk.rocksdb

expect enum class CompactionReason {
    kUnknown,

    /** [Level] number of L0 files > level0_file_num_compaction_trigger */
    kLevelL0FilesNum,

    /** [Level] total size of level > MaxBytesForLevel() */
    kLevelMaxLevelSize,

    /** [Universal] Compacting for size amplification */
    kUniversalSizeAmplification,

    /** [Universal] Compacting for size ratio */
    kUniversalSizeRatio,

    /** [Universal] number of sorted runs > level0_file_num_compaction_trigger */
    kUniversalSortedRunNum,

    /** [FIFO] total size > max_table_files_size */
    kFIFOMaxSize,

    /** [FIFO] reduce number of files. */
    kFIFOReduceNumFiles,

    /** [FIFO] files with creation time < (current_time - interval) */
    kFIFOTtl,

    /** Manual compaction */
    kManualCompaction,

    /** DB::SuggestCompactRange() marked files for compaction */
    kFilesMarkedForCompaction,

    /**
     * [Level] Automatic compaction within bottommost level to cleanup duplicate
     * versions of same user key, usually due to a released snapshot.
     */
    kBottommostFiles,

    /** Compaction based on TTL */
    kTtl,

    /**
     * According to the comments in flush_job.cc, RocksDB treats flush as
     * a level 0 compaction in internal stats.
     */
    kFlush,

    /** Compaction caused by external sst file ingestion */
    kExternalSstIngestion;
}
