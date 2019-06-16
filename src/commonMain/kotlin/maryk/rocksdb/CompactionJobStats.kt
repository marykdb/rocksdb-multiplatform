package maryk.rocksdb

expect class CompactionJobStats() : RocksObject {
    /** Reset the stats. */
    fun reset()

    /** Aggregate the [compactionJobStats] from another instance with this one. */
    fun add(compactionJobStats: CompactionJobStats)

    /** Get the elapsed time in micro of this compaction. */
    fun elapsedMicros(): Long

    /** Get the number of compaction input records. */
    fun numInputRecords(): Long

    /** Get the number of compaction input files. */
    fun numInputFiles(): Long

    /** Get the number of compaction input files at the output level. */
    fun numInputFilesAtOutputLevel(): Long

    /** Get the number of compaction output records. */
    fun numOutputRecords(): Long

    /** Get the number of compaction output files. */
    fun numOutputFiles(): Long

    /**
     * Determine if the compaction is a manual compaction.
     * @return true if the compaction is a manual compaction, false otherwise.
     */
    fun isManualCompaction(): Boolean

    /** Get the size of the compaction input in bytes. */
    fun totalInputBytes(): Long

    /** Get the size of the compaction output in bytes. */
    fun totalOutputBytes(): Long

    /**
     * Get the number of records being replaced by newer record associated
     * with same key.
     *
     * This could be a new value or a deletion entry for that key so this field
     * sums up all updated and deleted keys.
     *
     * @return the number of records being replaced by newer record associated
     * with same key.
     */
    fun numRecordsReplaced(): Long

    /** Get the sum of the uncompressed input keys in bytes. */
    fun totalInputRawKeyBytes(): Long

    /** Get the sum of the uncompressed input values in bytes. */
    fun totalInputRawValueBytes(): Long

    /**
     * Get the number of deletion entries before compaction.
     * Deletion entries can disappear after compaction because they expired.
     */
    fun numInputDeletionRecords(): Long

    /**
     * Get the number of deletion records that were found obsolete and discarded
     * because it is not possible to delete any more keys with this entry.
     * (i.e. all possible deletions resulting from it have been completed)
     */
    fun numExpiredDeletionRecords(): Long

    /**
     * Get the number of corrupt keys (ParseInternalKey returned false when
     * applied to the key) encountered and written out.
     */
    fun numCorruptKeys(): Long

    /**
     * Get the Time spent on file's Append() call.
     * Only populated if [ColumnFamilyOptions.reportBgIoStats] is set.
     */
    fun fileWriteNanos(): Long

    /**
     * Get the Time spent on sync file range.
     * Only populated if [ColumnFamilyOptions.reportBgIoStats] is set.
     */
    fun fileRangeSyncNanos(): Long

    /**
     * Get the Time spent on file fsync.
     * Only populated if [ColumnFamilyOptions.reportBgIoStats] is set.
     */
    fun fileFsyncNanos(): Long

    /**
     * Get the Time spent on preparing file write (falocate, etc)
     * Only populated if [ColumnFamilyOptions.reportBgIoStats] is set.
     */
    fun filePrepareWriteNanos(): Long

    /** Get the smallest output key prefix. */
    fun smallestOutputKeyPrefix(): ByteArray

    /** Get the largest output key prefix. */
    fun largestOutputKeyPrefix(): ByteArray

    /** Get the number of single-deletes which do not meet a put. */
    fun numSingleDelFallthru(): Long

    /** Get the number of single-deletes which meet something other than a put. */
    fun numSingleDelMismatch(): Long
}
