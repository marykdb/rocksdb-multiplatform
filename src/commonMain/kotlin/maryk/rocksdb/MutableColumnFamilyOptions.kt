package maryk.rocksdb

expect class MutableColumnFamilyOptions : AbstractMutableOptions

expect enum class MemtableOption {
    write_buffer_size,
    arena_block_size,
    memtable_prefix_bloom_size_ratio,
    memtable_huge_page_size,
    max_successive_merges,
    max_write_buffer_number,
    inplace_update_num_locks;

    fun getValueType(): MutableOptionKeyValueType
}

expect enum class CompactionOption {
    disable_auto_compactions,
    soft_pending_compaction_bytes_limit,
    hard_pending_compaction_bytes_limit,
    level0_file_num_compaction_trigger,
    level0_slowdown_writes_trigger,
    level0_stop_writes_trigger,
    max_compaction_bytes,
    target_file_size_base,
    target_file_size_multiplier,
    max_bytes_for_level_base,
    max_bytes_for_level_multiplier,
    max_bytes_for_level_multiplier_additional,
    ttl;

    fun getValueType(): MutableOptionKeyValueType
}

expect enum class MiscOption {
    max_sequential_skip_in_iterations,
    paranoid_file_checks,
    report_bg_io_stats,
    compression_type;

    fun getValueType(): MutableOptionKeyValueType
}

expect class MutableColumnFamilyOptionsBuilder {
    fun build() : MutableColumnFamilyOptions

    /**
     * Amount of data to build up in memory (backed by an unsorted log
     * on disk) before converting to a sorted on-disk file.
     *
     * Larger values increase performance, especially during bulk loads.
     * Up to `max_write_buffer_number` write buffers may be held in memory
     * at the same time, so you may wish to adjust this parameter
     * to control memory usage.
     *
     * Also, a larger write buffer will result in a longer recovery time
     * the next time the database is opened.
     *
     * Default: 4MB
     * @param writeBufferSize the size of write buffer.
     * @return the instance of the current object.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setWriteBufferSize(writeBufferSize: Long): MutableColumnFamilyOptionsBuilder

    /**
     * Return size of write buffer size.
     *
     * @return size of write buffer.
     * @see .setWriteBufferSize
     */
    fun writeBufferSize(): Long

    /**
     * Disable automatic compactions. Manual compactions can still
     * be issued on this column family
     *
     * @param disableAutoCompactions true if auto-compactions are disabled.
     * @return the reference to the current option.
     */
    fun setDisableAutoCompactions(
        disableAutoCompactions: Boolean
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Disable automatic compactions. Manual compactions can still
     * be issued on this column family
     *
     * @return true if auto-compactions are disabled.
     */
    fun disableAutoCompactions(): Boolean

    /**
     * Number of files to trigger level-0 compaction. A value &lt; 0 means that
     * level-0 compaction will not be triggered by number of files at all.
     *
     * Default: 4
     *
     * @param level0FileNumCompactionTrigger The number of files to trigger
     * level-0 compaction
     * @return the reference to the current option.
     */
    fun setLevel0FileNumCompactionTrigger(
        level0FileNumCompactionTrigger: Int
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Number of files to trigger level-0 compaction. A value &lt; 0 means that
     * level-0 compaction will not be triggered by number of files at all.
     *
     * Default: 4
     *
     * @return The number of files to trigger
     */
    fun level0FileNumCompactionTrigger(): Int

    /**
     * We try to limit number of bytes in one compaction to be lower than this
     * threshold. But it's not guaranteed.
     * Value 0 will be sanitized.
     *
     * @param maxCompactionBytes max bytes in a compaction
     * @return the reference to the current option.
     * @see .maxCompactionBytes
     */
    fun setMaxCompactionBytes(maxCompactionBytes: Long): MutableColumnFamilyOptionsBuilder

    /**
     * We try to limit number of bytes in one compaction to be lower than this
     * threshold. But it's not guaranteed.
     * Value 0 will be sanitized.
     *
     * @return the maximum number of bytes in for a compaction.
     * @see .setMaxCompactionBytes
     */
    fun maxCompactionBytes(): Long

    /**
     * The upper-bound of the total size of level-1 files in bytes.
     * Maximum number of bytes for level L can be calculated as
     * (maxBytesForLevelBase) * (maxBytesForLevelMultiplier ^ (L-1))
     * For example, if maxBytesForLevelBase is 20MB, and if
     * max_bytes_for_level_multiplier is 10, total data size for level-1
     * will be 20MB, total file size for level-2 will be 200MB,
     * and total file size for level-3 will be 2GB.
     * by default 'maxBytesForLevelBase' is 10MB.
     *
     * @param maxBytesForLevelBase maximum bytes for level base.
     *
     * @return the reference to the current option.
     *
     * See [AdvancedMutableColumnFamilyOptionsInterface.setMaxBytesForLevelMultiplier]
     */
    fun setMaxBytesForLevelBase(
        maxBytesForLevelBase: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * The upper-bound of the total size of level-1 files in bytes.
     * Maximum number of bytes for level L can be calculated as
     * (maxBytesForLevelBase) * (maxBytesForLevelMultiplier ^ (L-1))
     * For example, if maxBytesForLevelBase is 20MB, and if
     * max_bytes_for_level_multiplier is 10, total data size for level-1
     * will be 20MB, total file size for level-2 will be 200MB,
     * and total file size for level-3 will be 2GB.
     * by default 'maxBytesForLevelBase' is 10MB.
     *
     * @return the upper-bound of the total size of level-1 files
     * in bytes.
     *
     * See [AdvancedMutableColumnFamilyOptionsInterface.maxBytesForLevelMultiplier]
     */
    fun maxBytesForLevelBase(): Long

    /**
     * Compress blocks using the specified compression algorithm.  This
     * parameter can be changed dynamically.
     *
     * Default: SNAPPY_COMPRESSION, which gives lightweight but fast compression.
     *
     * @param compressionType Compression Type.
     * @return the reference to the current option.
     */
    fun setCompressionType(
        compressionType: CompressionType
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Compress blocks using the specified compression algorithm.  This
     * parameter can be changed dynamically.
     *
     * Default: SNAPPY_COMPRESSION, which gives lightweight but fast compression.
     *
     * @return Compression type.
     */
    fun compressionType(): CompressionType

    /**
     * The maximum number of write buffers that are built up in memory.
     * The default is 2, so that when 1 write buffer is being flushed to
     * storage, new writes can continue to the other write buffer.
     * Default: 2
     *
     * @param maxWriteBufferNumber maximum number of write buffers.
     * @return the instance of the current options.
     */
    fun setMaxWriteBufferNumber(
        maxWriteBufferNumber: Int
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Returns maximum number of write buffers.
     *
     * @return maximum number of write buffers.
     * @see .setMaxWriteBufferNumber
     */
    fun maxWriteBufferNumber(): Int

    /**
     * Number of locks used for inplace update
     * Default: 10000, if inplace_update_support = true, else 0.
     *
     * @param inplaceUpdateNumLocks the number of locks used for
     * inplace updates.
     * @return the reference to the current options.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setInplaceUpdateNumLocks(
        inplaceUpdateNumLocks: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Number of locks used for inplace update
     * Default: 10000, if inplace_update_support = true, else 0.
     *
     * @return the number of locks used for inplace update.
     */
    fun inplaceUpdateNumLocks(): Long

    /**
     * if prefix_extractor is set and memtable_prefix_bloom_size_ratio is not 0,
     * create prefix bloom for memtable with the size of
     * write_buffer_size * memtable_prefix_bloom_size_ratio.
     * If it is larger than 0.25, it is santinized to 0.25.
     *
     * Default: 0 (disable)
     *
     * @param memtablePrefixBloomSizeRatio The ratio
     * @return the reference to the current options.
     */
    fun setMemtablePrefixBloomSizeRatio(
        memtablePrefixBloomSizeRatio: Double
    ): MutableColumnFamilyOptionsBuilder

    /**
     * if prefix_extractor is set and memtable_prefix_bloom_size_ratio is not 0,
     * create prefix bloom for memtable with the size of
     * write_buffer_size * memtable_prefix_bloom_size_ratio.
     * If it is larger than 0.25, it is santinized to 0.25.
     *
     * Default: 0 (disable)
     *
     * @return the ratio
     */
    fun memtablePrefixBloomSizeRatio(): Double

    /**
     * Page size for huge page TLB for bloom in memtable. If  0, not allocate
     * from huge page TLB but from malloc.
     * Need to reserve huge pages for it to be allocated. For example:
     * sysctl -w vm.nr_hugepages=20
     * See linux doc Documentation/vm/hugetlbpage.txt
     *
     * @param memtableHugePageSize The page size of the huge
     * page tlb
     * @return the reference to the current options.
     */
    fun setMemtableHugePageSize(
        memtableHugePageSize: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Page size for huge page TLB for bloom in memtable. If  0, not allocate
     * from huge page TLB but from malloc.
     * Need to reserve huge pages for it to be allocated. For example:
     * sysctl -w vm.nr_hugepages=20
     * See linux doc Documentation/vm/hugetlbpage.txt
     *
     * @return The page size of the huge page tlb
     */
    fun memtableHugePageSize(): Long

    /**
     * The size of one block in arena memory allocation.
     * If  0, a proper value is automatically calculated (usually 1/10 of
     * writer_buffer_size).
     *
     * There are two additional restriction of the specified size:
     * (1) size should be in the range of [4096, 2 &lt;&lt; 30] and
     * (2) be the multiple of the CPU word (which helps with the memory
     * alignment).
     *
     * We'll automatically check and adjust the size number to make sure it
     * conforms to the restrictions.
     * Default: 0
     *
     * @param arenaBlockSize the size of an arena block
     * @return the reference to the current options.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setArenaBlockSize(arenaBlockSize: Long): MutableColumnFamilyOptionsBuilder

    /**
     * The size of one block in arena memory allocation.
     * If  0, a proper value is automatically calculated (usually 1/10 of
     * writer_buffer_size).
     *
     * There are two additional restriction of the specified size:
     * (1) size should be in the range of [4096, 2 &lt;&lt; 30] and
     * (2) be the multiple of the CPU word (which helps with the memory
     * alignment).
     *
     * We'll automatically check and adjust the size number to make sure it
     * conforms to the restrictions.
     * Default: 0
     *
     * @return the size of an arena block
     */
    fun arenaBlockSize(): Long

    /**
     * Soft limit on number of level-0 files. We start slowing down writes at this
     * point. A value &lt; 0 means that no writing slow down will be triggered by
     * number of files in level-0.
     *
     * @param level0SlowdownWritesTrigger The soft limit on the number of
     * level-0 files
     * @return the reference to the current options.
     */
    fun setLevel0SlowdownWritesTrigger(
        level0SlowdownWritesTrigger: Int
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Soft limit on number of level-0 files. We start slowing down writes at this
     * point. A value &lt; 0 means that no writing slow down will be triggered by
     * number of files in level-0.
     *
     * @return The soft limit on the number of
     * level-0 files
     */
    fun level0SlowdownWritesTrigger(): Int

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @param level0StopWritesTrigger The maximum number of level-0 files
     * @return the reference to the current options.
     */
    fun setLevel0StopWritesTrigger(
        level0StopWritesTrigger: Int
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Maximum number of level-0 files.  We stop writes at this point.
     *
     * @return The maximum number of level-0 files
     */
    fun level0StopWritesTrigger(): Int

    /**
     * The target file size for compaction.
     * This targetFileSizeBase determines a level-1 file size.
     * Target file size for level L can be calculated by
     * targetFileSizeBase * (targetFileSizeMultiplier ^ (L-1))
     * For example, if targetFileSizeBase is 2MB and
     * target_file_size_multiplier is 10, then each file on level-1 will
     * be 2MB, and each file on level 2 will be 20MB,
     * and each file on level-3 will be 200MB.
     * by default targetFileSizeBase is 2MB.
     *
     * @param targetFileSizeBase the target size of a level-0 file.
     * @return the reference to the current options.
     *
     * @see .setTargetFileSizeMultiplier
     */
    fun setTargetFileSizeBase(
        targetFileSizeBase: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * The target file size for compaction.
     * This targetFileSizeBase determines a level-1 file size.
     * Target file size for level L can be calculated by
     * targetFileSizeBase * (targetFileSizeMultiplier ^ (L-1))
     * For example, if targetFileSizeBase is 2MB and
     * target_file_size_multiplier is 10, then each file on level-1 will
     * be 2MB, and each file on level 2 will be 20MB,
     * and each file on level-3 will be 200MB.
     * by default targetFileSizeBase is 2MB.
     *
     * @return the target size of a level-0 file.
     *
     * @see .targetFileSizeMultiplier
     */
    fun targetFileSizeBase(): Long

    /**
     * targetFileSizeMultiplier defines the size ratio between a
     * level-L file and level-(L+1) file.
     * By default target_file_size_multiplier is 1, meaning
     * files in different levels have the same target.
     *
     * @param multiplier the size ratio between a level-(L+1) file
     * and level-L file.
     * @return the reference to the current options.
     */
    fun setTargetFileSizeMultiplier(
        multiplier: Int
    ): MutableColumnFamilyOptionsBuilder

    /**
     * targetFileSizeMultiplier defines the size ratio between a
     * level-(L+1) file and level-L file.
     * By default targetFileSizeMultiplier is 1, meaning
     * files in different levels have the same target.
     *
     * @return the size ratio between a level-(L+1) file and level-L file.
     */
    fun targetFileSizeMultiplier(): Int

    /**
     * The ratio between the total size of level-(L+1) files and the total
     * size of level-L files for all L.
     * DEFAULT: 10
     *
     * @param multiplier the ratio between the total size of level-(L+1)
     * files and the total size of level-L files for all L.
     * @return the reference to the current options.
     *
     * See [MutableColumnFamilyOptionsInterface.setMaxBytesForLevelBase]
     */
    fun setMaxBytesForLevelMultiplier(multiplier: Double): MutableColumnFamilyOptionsBuilder

    /**
     * The ratio between the total size of level-(L+1) files and the total
     * size of level-L files for all L.
     * DEFAULT: 10
     *
     * @return the ratio between the total size of level-(L+1) files and
     * the total size of level-L files for all L.
     *
     * See [MutableColumnFamilyOptionsInterface.maxBytesForLevelBase]
     */
    fun maxBytesForLevelMultiplier(): Double

    /**
     * Different max-size multipliers for different levels.
     * These are multiplied by max_bytes_for_level_multiplier to arrive
     * at the max-size of each level.
     *
     * Default: 1
     *
     * @param maxBytesForLevelMultiplierAdditional The max-size multipliers
     * for each level
     * @return the reference to the current options.
     */
    fun setMaxBytesForLevelMultiplierAdditional(
        maxBytesForLevelMultiplierAdditional: IntArray
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Different max-size multipliers for different levels.
     * These are multiplied by max_bytes_for_level_multiplier to arrive
     * at the max-size of each level.
     *
     * Default: 1
     *
     * @return The max-size multipliers for each level
     */
    fun maxBytesForLevelMultiplierAdditional(): IntArray

    /**
     * All writes will be slowed down to at least delayed_write_rate if estimated
     * bytes needed to be compaction exceed this threshold.
     *
     * Default: 64GB
     *
     * @param softPendingCompactionBytesLimit The soft limit to impose on
     * compaction
     * @return the reference to the current options.
     */
    fun setSoftPendingCompactionBytesLimit(
        softPendingCompactionBytesLimit: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * All writes will be slowed down to at least delayed_write_rate if estimated
     * bytes needed to be compaction exceed this threshold.
     *
     * Default: 64GB
     *
     * @return The soft limit to impose on compaction
     */
    fun softPendingCompactionBytesLimit(): Long

    /**
     * All writes are stopped if estimated bytes needed to be compaction exceed
     * this threshold.
     *
     * Default: 256GB
     *
     * @param hardPendingCompactionBytesLimit The hard limit to impose on
     * compaction
     * @return the reference to the current options.
     */
    fun setHardPendingCompactionBytesLimit(
        hardPendingCompactionBytesLimit: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * All writes are stopped if estimated bytes needed to be compaction exceed
     * this threshold.
     *
     * Default: 256GB
     *
     * @return The hard limit to impose on compaction
     */
    fun hardPendingCompactionBytesLimit(): Long

    /**
     * An iteration-&gt;Next() sequentially skips over keys with the same
     * user-key unless this option is set. This number specifies the number
     * of keys (with the same userkey) that will be sequentially
     * skipped before a reseek is issued.
     * Default: 8
     *
     * @param maxSequentialSkipInIterations the number of keys could
     * be skipped in a iteration.
     * @return the reference to the current options.
     */
    fun setMaxSequentialSkipInIterations(
        maxSequentialSkipInIterations: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * An iteration-&gt;Next() sequentially skips over keys with the same
     * user-key unless this option is set. This number specifies the number
     * of keys (with the same userkey) that will be sequentially
     * skipped before a reseek is issued.
     * Default: 8
     *
     * @return the number of keys could be skipped in a iteration.
     */
    fun maxSequentialSkipInIterations(): Long

    /**
     * Maximum number of successive merge operations on a key in the memtable.
     *
     * When a merge operation is added to the memtable and the maximum number of
     * successive merges is reached, the value of the key will be calculated and
     * inserted into the memtable instead of the merge operation. This will
     * ensure that there are never more than max_successive_merges merge
     * operations in the memtable.
     *
     * Default: 0 (disabled)
     *
     * @param maxSuccessiveMerges the maximum number of successive merges.
     * @return the reference to the current options.
     * @throws java.lang.IllegalArgumentException thrown on 32-Bit platforms
     * while overflowing the underlying platform specific value.
     */
    fun setMaxSuccessiveMerges(
        maxSuccessiveMerges: Long
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Maximum number of successive merge operations on a key in the memtable.
     *
     * When a merge operation is added to the memtable and the maximum number of
     * successive merges is reached, the value of the key will be calculated and
     * inserted into the memtable instead of the merge operation. This will
     * ensure that there are never more than max_successive_merges merge
     * operations in the memtable.
     *
     * Default: 0 (disabled)
     *
     * @return the maximum number of successive merges.
     */
    fun maxSuccessiveMerges(): Long

    /**
     * After writing every SST file, reopen it and read all the keys.
     *
     * Default: false
     *
     * @param paranoidFileChecks true to enable paranoid file checks
     * @return the reference to the current options.
     */
    fun setParanoidFileChecks(
        paranoidFileChecks: Boolean
    ): MutableColumnFamilyOptionsBuilder

    /**
     * After writing every SST file, reopen it and read all the keys.
     *
     * Default: false
     *
     * @return true if paranoid file checks are enabled
     */
    fun paranoidFileChecks(): Boolean

    /**
     * Measure IO stats in compactions and flushes, if true.
     *
     * Default: false
     *
     * @param reportBgIoStats true to enable reporting
     * @return the reference to the current options.
     */
    fun setReportBgIoStats(
        reportBgIoStats: Boolean
    ): MutableColumnFamilyOptionsBuilder

    /**
     * Determine whether IO stats in compactions and flushes are being measured
     *
     * @return true if reporting is enabled
     */
    fun reportBgIoStats(): Boolean

    /**
     * Non-bottom-level files older than TTL will go through the compaction
     * process. This needs [MutableDBOptionsInterface.maxOpenFiles] to be
     * set to -1.
     *
     * Enabled only for level compaction for now.
     *
     * Default: 0 (disabled)
     *
     * Dynamically changeable through
     * [RocksDB.setOptions].
     *
     * @param ttl the time-to-live.
     *
     * @return the reference to the current options.
     */
    fun setTtl(ttl: Long): MutableColumnFamilyOptionsBuilder

    /**
     * Get the TTL for Non-bottom-level files that will go through the compaction
     * process.
     *
     * See [.setTtl].
     *
     * @return the time-to-live.
     */
    fun ttl(): Long
}

/**
 * Creates a builder which allows you
 * to set MutableColumnFamilyOptions in a fluent
 * manner
 *
 * @return A builder for MutableColumnFamilyOptions
 */
expect fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder

/**
 * Parses a String representation of MutableColumnFamilyOptions
 *
 * The format is: key1=value1;key2=value2;key3=value3 etc
 *
 * For int[] values, each int should be separated by a comma, e.g.
 *
 * key1=value1;intArrayKey1=1,2,3
 *
 * @param str The string representation of the mutable column family options
 *
 * @return A builder for the mutable column family options
 */
expect fun mutableColumnFamilyOptionsParse(str: String): MutableColumnFamilyOptionsBuilder
