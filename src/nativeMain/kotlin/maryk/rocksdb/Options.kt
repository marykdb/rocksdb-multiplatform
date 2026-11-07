@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

import rocksdb.rocksdb_options_get_bottommost_compression
import rocksdb.rocksdb_options_set_bottommost_compression

import rocksdb.rocksdb_options_compaction_readahead_size
import rocksdb.rocksdb_options_get_WAL_ttl_seconds
import rocksdb.rocksdb_options_get_advise_random_on_open
import rocksdb.rocksdb_options_get_allow_2pc
import rocksdb.rocksdb_options_get_allow_concurrent_memtable_write
import rocksdb.rocksdb_options_get_allow_fallocate
import rocksdb.rocksdb_options_get_allow_ingest_behind
import rocksdb.rocksdb_options_get_allow_mmap_reads
import rocksdb.rocksdb_options_get_allow_mmap_writes
import rocksdb.rocksdb_options_get_arena_block_size
import rocksdb.rocksdb_options_get_atomic_flush
import rocksdb.rocksdb_options_get_avoid_flush_during_recovery
import rocksdb.rocksdb_options_get_avoid_flush_during_shutdown
import rocksdb.rocksdb_options_get_avoid_unnecessary_blocking_io
import rocksdb.rocksdb_options_get_best_efforts_recovery
import rocksdb.rocksdb_options_get_bgerror_resume_retry_interval
import rocksdb.rocksdb_options_get_compaction_pri
import rocksdb.rocksdb_options_get_compaction_readahead_size
import rocksdb.rocksdb_options_get_daily_offpeak_time_utc
import rocksdb.rocksdb_options_get_db_log_dir
import rocksdb.rocksdb_options_get_db_write_buffer_size
import rocksdb.rocksdb_options_get_delayed_write_rate
import rocksdb.rocksdb_options_get_delete_obsolete_files_period_micros
import rocksdb.rocksdb_options_get_dump_malloc_stats
import rocksdb.rocksdb_options_get_enable_pipelined_write
import rocksdb.rocksdb_options_get_enable_thread_tracking
import rocksdb.rocksdb_options_get_enable_write_thread_adaptive_yield
import rocksdb.rocksdb_options_get_experimental_mempurge_threshold
import rocksdb.rocksdb_options_get_force_consistency_checks
import rocksdb.rocksdb_options_get_hard_pending_compaction_bytes_limit
import rocksdb.rocksdb_options_get_inplace_update_num_locks
import rocksdb.rocksdb_options_get_inplace_update_support
import rocksdb.rocksdb_options_get_is_fd_close_on_exec
import rocksdb.rocksdb_options_get_level_compaction_dynamic_level_bytes
import rocksdb.rocksdb_options_get_log_readahead_size
import rocksdb.rocksdb_options_get_manifest_preallocation_size
import rocksdb.rocksdb_options_get_manual_wal_flush
import rocksdb.rocksdb_options_get_max_background_compactions
import rocksdb.rocksdb_options_get_max_background_flushes
import rocksdb.rocksdb_options_get_max_background_jobs
import rocksdb.rocksdb_options_get_max_bgerror_resume_count
import rocksdb.rocksdb_options_get_max_compaction_trigger_wakeup_seconds
import rocksdb.rocksdb_options_get_max_file_opening_threads
import rocksdb.rocksdb_options_get_max_manifest_file_size
import rocksdb.rocksdb_options_get_max_sequential_skip_in_iterations
import rocksdb.rocksdb_options_get_max_subcompactions
import rocksdb.rocksdb_options_get_max_successive_merges
import rocksdb.rocksdb_options_get_max_total_wal_size
import rocksdb.rocksdb_options_get_max_write_batch_group_size_bytes
import rocksdb.rocksdb_options_get_memtable_huge_page_size
import rocksdb.rocksdb_options_get_memtable_max_range_deletions
import rocksdb.rocksdb_options_get_memtable_prefix_bloom_size_ratio
import rocksdb.rocksdb_options_get_memtable_whole_key_filtering
import rocksdb.rocksdb_options_get_min_tombstones_for_range_conversion
import rocksdb.rocksdb_options_get_open_files_async
import rocksdb.rocksdb_options_get_optimize_filters_for_hits
import rocksdb.rocksdb_options_get_paranoid_file_checks
import rocksdb.rocksdb_options_get_periodic_compaction_seconds
import rocksdb.rocksdb_options_get_persist_stats_to_disk
import rocksdb.rocksdb_options_get_read_triggered_compaction_threshold
import rocksdb.rocksdb_options_get_recycle_log_file_num
import rocksdb.rocksdb_options_get_report_bg_io_stats
import rocksdb.rocksdb_options_get_skip_stats_update_on_db_open
import rocksdb.rocksdb_options_get_soft_pending_compaction_bytes_limit
import rocksdb.rocksdb_options_get_stats_dump_period_sec
import rocksdb.rocksdb_options_get_stats_history_buffer_size
import rocksdb.rocksdb_options_get_stats_persist_period_sec
import rocksdb.rocksdb_options_get_strict_bytes_per_sync
import rocksdb.rocksdb_options_get_table_cache_numshardbits
import rocksdb.rocksdb_options_get_ttl
import rocksdb.rocksdb_options_get_two_write_queues
import rocksdb.rocksdb_options_get_unordered_write
import rocksdb.rocksdb_options_get_use_adaptive_mutex
import rocksdb.rocksdb_options_get_use_direct_io_for_flush_and_compaction
import rocksdb.rocksdb_options_get_use_direct_reads
import rocksdb.rocksdb_options_get_wal_bytes_per_sync
import rocksdb.rocksdb_options_get_wal_dir
import rocksdb.rocksdb_options_get_writable_file_max_buffer_size
import rocksdb.rocksdb_options_get_write_dbid_to_manifest
import rocksdb.rocksdb_options_get_write_thread_max_yield_usec
import rocksdb.rocksdb_options_get_write_thread_slow_yield_usec
import rocksdb.rocksdb_options_increase_parallelism
import rocksdb.rocksdb_options_optimize_for_point_lookup
import rocksdb.rocksdb_options_optimize_level_style_compaction
import rocksdb.rocksdb_options_optimize_universal_style_compaction
import rocksdb.rocksdb_options_prepare_for_bulk_load
import rocksdb.rocksdb_options_set_WAL_ttl_seconds
import rocksdb.rocksdb_options_set_advise_random_on_open
import rocksdb.rocksdb_options_set_allow_2pc
import rocksdb.rocksdb_options_set_allow_concurrent_memtable_write
import rocksdb.rocksdb_options_set_allow_fallocate
import rocksdb.rocksdb_options_set_allow_ingest_behind
import rocksdb.rocksdb_options_set_allow_mmap_reads
import rocksdb.rocksdb_options_set_allow_mmap_writes
import rocksdb.rocksdb_options_set_arena_block_size
import rocksdb.rocksdb_options_set_atomic_flush
import rocksdb.rocksdb_options_set_avoid_flush_during_recovery
import rocksdb.rocksdb_options_set_avoid_flush_during_shutdown
import rocksdb.rocksdb_options_set_avoid_unnecessary_blocking_io
import rocksdb.rocksdb_options_set_best_efforts_recovery
import rocksdb.rocksdb_options_set_bgerror_resume_retry_interval
import rocksdb.rocksdb_options_set_compaction_pri
import rocksdb.rocksdb_options_set_daily_offpeak_time_utc
import rocksdb.rocksdb_options_set_db_log_dir
import rocksdb.rocksdb_options_set_db_write_buffer_size
import rocksdb.rocksdb_options_set_delayed_write_rate
import rocksdb.rocksdb_options_set_delete_obsolete_files_period_micros
import rocksdb.rocksdb_options_set_dump_malloc_stats
import rocksdb.rocksdb_options_set_enable_pipelined_write
import rocksdb.rocksdb_options_set_enable_thread_tracking
import rocksdb.rocksdb_options_set_enable_write_thread_adaptive_yield
import rocksdb.rocksdb_options_set_experimental_mempurge_threshold
import rocksdb.rocksdb_options_set_force_consistency_checks
import rocksdb.rocksdb_options_set_hard_pending_compaction_bytes_limit
import rocksdb.rocksdb_options_set_inplace_update_num_locks
import rocksdb.rocksdb_options_set_inplace_update_support
import rocksdb.rocksdb_options_set_is_fd_close_on_exec
import rocksdb.rocksdb_options_set_level_compaction_dynamic_level_bytes
import rocksdb.rocksdb_options_set_log_readahead_size
import rocksdb.rocksdb_options_set_manifest_preallocation_size
import rocksdb.rocksdb_options_set_manual_wal_flush
import rocksdb.rocksdb_options_set_max_background_compactions
import rocksdb.rocksdb_options_set_max_background_flushes
import rocksdb.rocksdb_options_set_max_background_jobs
import rocksdb.rocksdb_options_set_max_bgerror_resume_count
import rocksdb.rocksdb_options_set_max_compaction_trigger_wakeup_seconds
import rocksdb.rocksdb_options_set_max_file_opening_threads
import rocksdb.rocksdb_options_set_max_manifest_file_size
import rocksdb.rocksdb_options_set_max_sequential_skip_in_iterations
import rocksdb.rocksdb_options_set_max_subcompactions
import rocksdb.rocksdb_options_set_max_successive_merges
import rocksdb.rocksdb_options_set_max_total_wal_size
import rocksdb.rocksdb_options_set_max_write_batch_group_size_bytes
import rocksdb.rocksdb_options_set_memtable_huge_page_size
import rocksdb.rocksdb_options_set_memtable_max_range_deletions
import rocksdb.rocksdb_options_set_memtable_prefix_bloom_size_ratio
import rocksdb.rocksdb_options_set_memtable_whole_key_filtering
import rocksdb.rocksdb_options_set_min_tombstones_for_range_conversion
import rocksdb.rocksdb_options_set_open_files_async
import rocksdb.rocksdb_options_set_optimize_filters_for_hits
import rocksdb.rocksdb_options_set_paranoid_file_checks
import rocksdb.rocksdb_options_set_periodic_compaction_seconds
import rocksdb.rocksdb_options_set_persist_stats_to_disk
import rocksdb.rocksdb_options_set_read_triggered_compaction_threshold
import rocksdb.rocksdb_options_set_recycle_log_file_num
import rocksdb.rocksdb_options_set_report_bg_io_stats
import rocksdb.rocksdb_options_set_skip_stats_update_on_db_open
import rocksdb.rocksdb_options_set_soft_pending_compaction_bytes_limit
import rocksdb.rocksdb_options_set_stats_dump_period_sec
import rocksdb.rocksdb_options_set_stats_history_buffer_size
import rocksdb.rocksdb_options_set_stats_persist_period_sec
import rocksdb.rocksdb_options_set_strict_bytes_per_sync
import rocksdb.rocksdb_options_set_table_cache_numshardbits
import rocksdb.rocksdb_options_set_ttl
import rocksdb.rocksdb_options_set_two_write_queues
import rocksdb.rocksdb_options_set_unordered_write
import rocksdb.rocksdb_options_set_use_adaptive_mutex
import rocksdb.rocksdb_options_set_use_direct_io_for_flush_and_compaction
import rocksdb.rocksdb_options_set_use_direct_reads
import rocksdb.rocksdb_options_set_wal_bytes_per_sync
import rocksdb.rocksdb_options_set_wal_dir
import rocksdb.rocksdb_options_set_writable_file_max_buffer_size
import rocksdb.rocksdb_options_set_write_dbid_to_manifest
import rocksdb.rocksdb_options_set_write_thread_max_yield_usec
import rocksdb.rocksdb_options_set_write_thread_slow_yield_usec

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
import maryk.asUInt32
import maryk.asUInt64
import maryk.rocksdb.util.BytewiseComparator
import maryk.rocksdb.util.ReverseBytewiseComparator
import maryk.sizeTToLong
import maryk.toBoolean
import maryk.toCheckedInt
import maryk.toCheckedLong
import maryk.toUByte
import maryk.usePointer
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_options_create
import rocksdb.rocksdb_options_destroy
import rocksdb.rocksdb_options_get_WAL_size_limit_MB
import rocksdb.rocksdb_options_get_bloom_locality
import rocksdb.rocksdb_options_get_bytes_per_sync
import rocksdb.rocksdb_options_get_compaction_style
import rocksdb.rocksdb_options_get_compression
import rocksdb.rocksdb_options_get_create_if_missing
import rocksdb.rocksdb_options_get_create_missing_column_families
import rocksdb.rocksdb_options_get_disable_auto_compactions
import rocksdb.rocksdb_options_get_error_if_exists
import rocksdb.rocksdb_options_get_info_log_level
import rocksdb.rocksdb_options_get_keep_log_file_num
import rocksdb.rocksdb_options_get_level0_file_num_compaction_trigger
import rocksdb.rocksdb_options_get_level0_slowdown_writes_trigger
import rocksdb.rocksdb_options_get_level0_stop_writes_trigger
import rocksdb.rocksdb_options_get_log_file_time_to_roll
import rocksdb.rocksdb_options_get_max_bytes_for_level_base
import rocksdb.rocksdb_options_get_max_bytes_for_level_multiplier
import rocksdb.rocksdb_options_get_max_log_file_size
import rocksdb.rocksdb_options_get_max_open_files
import rocksdb.rocksdb_options_get_max_write_buffer_number
import rocksdb.rocksdb_options_get_min_write_buffer_number_to_merge
import rocksdb.rocksdb_options_get_num_levels
import rocksdb.rocksdb_options_get_paranoid_checks
import rocksdb.rocksdb_options_get_target_file_size_base
import rocksdb.rocksdb_options_get_target_file_size_multiplier
import rocksdb.rocksdb_options_get_use_fsync
import rocksdb.rocksdb_options_get_write_buffer_size
import rocksdb.rocksdb_options_set_WAL_size_limit_MB
import rocksdb.rocksdb_options_set_bloom_locality
import rocksdb.rocksdb_options_set_bytes_per_sync
import rocksdb.rocksdb_options_set_compaction_style
import rocksdb.rocksdb_options_set_comparator
import rocksdb.rocksdb_options_set_compression
import rocksdb.rocksdb_options_set_create_if_missing
import rocksdb.rocksdb_options_set_create_missing_column_families
import rocksdb.rocksdb_options_set_disable_auto_compactions
import rocksdb.rocksdb_options_set_error_if_exists
import rocksdb.rocksdb_options_set_info_log_level
import rocksdb.rocksdb_options_set_keep_log_file_num
import rocksdb.rocksdb_options_set_level0_file_num_compaction_trigger
import rocksdb.rocksdb_options_set_level0_slowdown_writes_trigger
import rocksdb.rocksdb_options_set_level0_stop_writes_trigger
import rocksdb.rocksdb_options_set_log_file_time_to_roll
import rocksdb.rocksdb_options_set_max_bytes_for_level_base
import rocksdb.rocksdb_options_set_max_bytes_for_level_multiplier
import rocksdb.rocksdb_options_set_max_log_file_size
import rocksdb.rocksdb_options_set_max_open_files
import rocksdb.rocksdb_options_set_max_write_buffer_number
import rocksdb.rocksdb_options_set_min_write_buffer_number_to_merge
import rocksdb.rocksdb_options_set_num_levels
import rocksdb.rocksdb_options_set_paranoid_checks
import rocksdb.rocksdb_options_set_prefix_extractor
import rocksdb.rocksdb_options_set_sst_file_manager
import rocksdb.rocksdb_options_set_target_file_size_base
import rocksdb.rocksdb_options_set_target_file_size_multiplier
import rocksdb.rocksdb_options_set_use_fsync
import rocksdb.rocksdb_options_set_write_buffer_size
import rocksdb.rocksdb_slicetransform_create_fixed_prefix
import kotlin.experimental.ExperimentalNativeApi

actual class Options private constructor(val native: CPointer<rocksdb_options_t>) :
    RocksObject(),
    DBOptionsInterface<Options> {
    private var statistics: Statistics? = null
    private var writeBufferManager: WriteBufferManager? = null
    private var env: Env? = null
    private var fifoCompactionOptions: CompactionOptionsFIFO? = null
    private var universalCompactionOptions: CompactionOptionsUniversal? = null
    internal var hasConfiguredCompressionOptions: Boolean = false
    internal var hasConfiguredBottommostCompressionOptions: Boolean = false
    private var tableFormatConfig: TableFormatConfig? = null
    private var ownedComparator: AbstractComparator? = null
    private var sstFileManager: SstFileManager? = null

    actual constructor() : this(requireNotNull(rocksdb_options_create()) { "Unable to allocate RocksDB options" })

    actual fun setMergeOperator(mergeOperator: MergeOperator): Options {
        checkOwningHandle()
        mergeOperator.transferOwnershipToNative()
        rocksdb.rocksdb_options_set_merge_operator(native, mergeOperator.native)
        return this
    }

    actual fun setMergeOperatorName(name: String): Options {
        checkOwningHandle()
        val bytes = name.encodeToByteArray()
        wrapWithErrorThrower { error ->
            bytes.usePointer { pointer ->
                rocksdb.rocksdb_options_set_merge_operator_name(native, pointer, bytes.size.asSizeT(), error)
            }
        }
        return this
    }

    actual fun setCompressionPerLevel(compressionLevels: List<CompressionType>): Options {
        checkOwningHandle()
        setNativeCompressionPerLevel(native, compressionLevels)
        return this
    }

    actual fun setRowCache(rowCache: Cache): Options {
        checkOwningHandle()
        rowCache.checkOwningHandle()
        rocksdb.rocksdb_options_set_row_cache(native, rowCache.native)
        return this
    }

    override fun close() {
        if (tryClose()) {
            val comparator = ownedComparator
            val attachedStatistics = statistics
            ownedComparator = null
            if (comparator != null) {
                rocksdb_options_set_comparator(native, null)
            }
            attachedStatistics?.disconnectFromNative(native)
            rocksdb_options_destroy(native)
            comparator?.closeFromOptions()
            statistics = null
            writeBufferManager = null
            env = null
            tableFormatConfig = null
            sstFileManager = null
            super.close()
        }
    }

    internal fun releaseOwnedComparator(): AbstractComparator? {
        checkOwningHandle()
        val comparator = ownedComparator
        if (comparator != null) {
            rocksdb_options_set_comparator(native, null)
        }
        return comparator.also {
            ownedComparator = null
        }
    }

    actual fun setTableFormatConfig(tableFormatConfig: TableFormatConfig): Options {
        checkOwningHandle()
        when (tableFormatConfig) {
            is BlockBasedTableConfig -> tableFormatConfig.applyToOptions(native)
            is PlainTableConfig -> tableFormatConfig.applyToOptions(native)
            else -> error("Unsupported table format config: ${tableFormatConfig::class.simpleName}")
        }
        this.tableFormatConfig = tableFormatConfig
        return this
    }

    actual fun setSstFileManager(sstFileManager: SstFileManager): Options {
        checkOwningHandle()
        sstFileManager.checkOwningHandle()
        rocksdb_options_set_sst_file_manager(native, sstFileManager.native)
        this.sstFileManager = sstFileManager
        return this
    }

    internal fun attachWriteBufferManager(manager: WriteBufferManager): Options {
        checkOwningHandle()
        manager.checkOwningHandle()
        rocksdb.rocksdb_options_set_write_buffer_manager(native, manager.native)
        writeBufferManager = manager
        return this
    }

    internal fun retainedNativeReferences(): List<Any> =
        listOfNotNull(env, statistics, sstFileManager, writeBufferManager)

    actual fun setMaxOpenFiles(maxOpenFiles: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_open_files(native, maxOpenFiles)
        return this
    }

    actual fun maxOpenFiles(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_open_files(native)
    }

    actual fun setBytesPerSync(bytesPerSync: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_bytes_per_sync(native, bytesPerSync.asUInt64())
        return this
    }

    actual fun bytesPerSync(): Long {
        checkOwningHandle()
        return rocksdb_options_get_bytes_per_sync(native).toCheckedLong("options bytes per sync")
    }

    actual fun setCreateIfMissing(flag: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_create_if_missing(native, flag.toUByte())
        return this
    }

    actual fun maxWriteBufferNumber(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_write_buffer_number(native)
    }

    actual fun minWriteBufferNumberToMerge(): Int {
        checkOwningHandle()
        return rocksdb_options_get_min_write_buffer_number_to_merge(native)
    }

    actual fun setBloomLocality(bloomLocality: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_bloom_locality(native, bloomLocality.asUInt32())
        return this
    }

    actual fun bloomLocality(): Int {
        checkOwningHandle()
        return rocksdb_options_get_bloom_locality(native).toCheckedInt("options bloom locality")
    }

    actual fun setNumLevels(numLevels: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_num_levels(native, numLevels)
        return this
    }

    actual fun numLevels(): Int {
        checkOwningHandle()
        return rocksdb_options_get_num_levels(native)
    }

    actual fun setMaxCompactionBytes(maxCompactionBytes: Long): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_max_compaction_bytes(native, maxCompactionBytes.asUInt64())
        return this
    }

    actual fun maxCompactionBytes(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_max_compaction_bytes(native).toCheckedLong("options max compaction bytes")
    }

    actual fun setCompactionStyle(compactionStyle: CompactionStyle): Options {
        checkOwningHandle()
        rocksdb_options_set_compaction_style(native, compactionStyle.value.toInt())
        return this
    }

    actual fun compactionStyle(): CompactionStyle {
        checkOwningHandle()
        return getCompactionStyle(
            rocksdb_options_get_compaction_style(native).toByte()
        )
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_write_buffer_size(native, writeBufferSize.asSizeT())
        return this
    }

    actual fun writeBufferSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_write_buffer_size(native), "options write buffer size")
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_disable_auto_compactions(native, if (disableAutoCompactions) 1 else 0)
        return this
    }

    actual fun disableAutoCompactions(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_disable_auto_compactions(native).toBoolean()
    }

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_level0_file_num_compaction_trigger(native, level0FileNumCompactionTrigger)
        return this
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        checkOwningHandle()
        return rocksdb_options_get_level0_file_num_compaction_trigger(native)
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_bytes_for_level_base(native, maxBytesForLevelBase.asUInt64())
        return this
    }

    actual fun maxBytesForLevelBase(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_bytes_for_level_base(native).toCheckedLong("options max bytes for level base")
    }

    actual fun setCompressionType(compressionType: CompressionType): Options {
        checkOwningHandle()
        rocksdb_options_set_compression(native, compressionType.value.toInt())
        return this
    }

    actual fun compressionType(): CompressionType {
        checkOwningHandle()
        return getCompressionType(
            rocksdb_options_get_compression(native).toByte()
        )
    }

    actual fun setComparator(builtinComparator: BuiltinComparator): Options {
        checkOwningHandle()
        val comparator = when (builtinComparator) {
            BuiltinComparator.BYTEWISE_COMPARATOR -> BytewiseComparator(null)
            BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR -> ReverseBytewiseComparator(null)
        }
        val previous = ownedComparator
        val comparatorNative = comparator.transferOwnershipToOptions()
        rocksdb_options_set_comparator(native, comparatorNative)
        ownedComparator = comparator
        previous?.closeFromOptions()
        return this
    }

    actual fun setComparator(comparator: AbstractComparator): Options {
        checkOwningHandle()
        if (ownedComparator === comparator) {
            return this
        }
        val previous = ownedComparator
        val comparatorNative = comparator.transferOwnershipToOptions()
        rocksdb_options_set_comparator(native, comparatorNative)
        ownedComparator = comparator
        previous?.closeFromOptions()
        return this
    }

    actual fun useFixedLengthPrefixExtractor(n: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_prefix_extractor(native, rocksdb_slicetransform_create_fixed_prefix(n.asSizeT()))
        return this
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): Options {
        checkOwningHandle()
        rocksdb_options_set_max_bytes_for_level_multiplier(native, multiplier)
        return this
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        checkOwningHandle()
        return rocksdb_options_get_max_bytes_for_level_multiplier(native)
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_level0_slowdown_writes_trigger(native, level0SlowdownWritesTrigger)
        return this
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        checkOwningHandle()
        return rocksdb_options_get_level0_slowdown_writes_trigger(native)
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_level0_stop_writes_trigger(native, level0StopWritesTrigger)
        return this
    }

    actual fun level0StopWritesTrigger(): Int {
        checkOwningHandle()
        return rocksdb_options_get_level0_stop_writes_trigger(native)
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_target_file_size_base(native, targetFileSizeBase.asUInt64())
        return this
    }

    actual fun targetFileSizeBase(): Long {
        checkOwningHandle()
        return rocksdb_options_get_target_file_size_base(native).toCheckedLong("options target file size base")
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_target_file_size_multiplier(native, multiplier)
        return this
    }

    actual fun targetFileSizeMultiplier(): Int {
        checkOwningHandle()
        return rocksdb_options_get_target_file_size_multiplier(native)
    }

    actual fun createIfMissing(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_create_if_missing(native).toBoolean()
    }

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_write_buffer_number(native, maxWriteBufferNumber)
        return this
    }

    actual fun setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_min_write_buffer_number_to_merge(native, minWriteBufferNumberToMerge)
        return this
    }

    actual fun setCreateMissingColumnFamilies(flag: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_create_missing_column_families(native, flag.toUByte())
        return this
    }

    actual fun createMissingColumnFamilies(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_create_missing_column_families(native).toBoolean()
    }

    actual fun setErrorIfExists(errorIfExists: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_error_if_exists(native, errorIfExists.toUByte())
        return this
    }

    actual fun errorIfExists(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_error_if_exists(native).toBoolean()
    }

    actual fun setParanoidChecks(paranoidChecks: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_paranoid_checks(native, paranoidChecks.toUByte())
        return this
    }

    actual fun paranoidChecks(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_paranoid_checks(native).toBoolean()
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): Options {
        checkOwningHandle()
        rocksdb_options_set_info_log_level(native, infoLogLevel.value.toInt())
        return this
    }

    actual fun infoLogLevel(): InfoLogLevel {
        checkOwningHandle()
        return getInfoLogLevel(rocksdb_options_get_info_log_level(native).toUByte())
    }

    actual fun setStatistics(statistics: Statistics): Options {
        checkOwningHandle()
        this.statistics?.disconnectFromNative(native)
        this.statistics = statistics
        statistics.connectWithNative(native)
        return this
    }

    actual fun statistics(): Statistics? {
        checkOwningHandle()
        return this.statistics
    }

    actual fun setUseFsync(useFsync: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_use_fsync(native, if (useFsync) 1 else 0)
        return this
    }

    actual fun useFsync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_fsync(native) == 1
    }

    actual fun setMaxLogFileSize(maxLogFileSize: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_log_file_size(native, maxLogFileSize.asSizeT())
        return this
    }

    actual fun maxLogFileSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_log_file_size(native), "options max log file size")
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_log_file_time_to_roll(native, logFileTimeToRoll.asSizeT())
        return this
    }

    actual fun logFileTimeToRoll(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_log_file_time_to_roll(native), "options log file time to roll")
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_keep_log_file_num(native, keepLogFileNum.asSizeT())
        return this
    }

    actual fun keepLogFileNum(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_keep_log_file_num(native), "options keep log file count")
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_WAL_size_limit_MB(native, sizeLimitMB.asUInt64())
        return this
    }

    actual fun walSizeLimitMB(): Long {
        checkOwningHandle()
        return rocksdb_options_get_WAL_size_limit_MB(native).toCheckedLong("options WAL size limit MB")
    }

    actual override fun setEnv(env: Env): Options {
        checkOwningHandle()
        env.checkOwningHandle()
        rocksdb.rocksdb_options_set_env(native, env.native)
        this.env = env
        return this
    }

    actual override fun getEnv(): Env {
        checkOwningHandle()
        if (this.env == null) {
            this.env = getDefaultEnv()
        }
        return requireNotNull(this.env)
    }

    actual fun setCompactionOptionsFIFO(compactionOptionsFIFO: CompactionOptionsFIFO): Options {
        checkOwningHandle()
        compactionOptionsFIFO.checkOwningHandle()
        rocksdb.rocksdb_options_set_compaction_options_fifo(native, compactionOptionsFIFO.native)
        fifoCompactionOptions = compactionOptionsFIFO
        return this
    }

    actual fun compactionOptionsFIFO(): CompactionOptionsFIFO? {
        checkOwningHandle()
        return fifoCompactionOptions
    }

    actual fun setCompactionOptionsUniversal(compactionOptionsUniversal: CompactionOptionsUniversal): Options {
        checkOwningHandle()
        compactionOptionsUniversal.checkOwningHandle()
        rocksdb.rocksdb_options_set_compaction_options_universal(native, compactionOptionsUniversal.native)
        universalCompactionOptions = compactionOptionsUniversal
        return this
    }

    actual fun compactionOptionsUniversal(): CompactionOptionsUniversal? {
        checkOwningHandle()
        return universalCompactionOptions
    }

    actual fun setEnableBlobFiles(value: Boolean): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_enable_blob_files(native, value.toUByte())
        return this
    }

    actual fun enableBlobFiles(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_enable_blob_files(native).toBoolean()
    }

    actual fun setMinBlobSize(value: Long): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_min_blob_size(native, value.asUInt64())
        return this
    }

    actual fun minBlobSize(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_min_blob_size(native).toCheckedLong("minBlobSize")
    }

    actual fun setBlobFileSize(value: Long): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_blob_file_size(native, value.asUInt64())
        return this
    }

    actual fun blobFileSize(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_blob_file_size(native).toCheckedLong("blobFileSize")
    }

    actual fun setBlobCompressionType(value: CompressionType): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_blob_compression_type(native, value.value.toInt())
        return this
    }

    actual fun blobCompressionType(): CompressionType {
        checkOwningHandle()
        return getCompressionType(rocksdb.rocksdb_options_get_blob_compression_type(native).toByte())
    }

    actual fun setEnableBlobGarbageCollection(value: Boolean): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_enable_blob_gc(native, value.toUByte())
        return this
    }

    actual fun enableBlobGarbageCollection(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_enable_blob_gc(native).toBoolean()
    }

    actual fun setBlobGarbageCollectionAgeCutoff(value: Double): Options {
        checkOwningHandle()
        require(value.isFinite() && value in 0.0..1.0) { "ratio must be between zero and one" }
        rocksdb.rocksdb_options_set_blob_gc_age_cutoff(native, value)
        return this
    }

    actual fun blobGarbageCollectionAgeCutoff(): Double {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_blob_gc_age_cutoff(native)
    }

    actual fun setBlobGarbageCollectionForceThreshold(value: Double): Options {
        checkOwningHandle()
        require(value.isFinite() && value in 0.0..1.0) { "ratio must be between zero and one" }
        rocksdb.rocksdb_options_set_blob_gc_force_threshold(native, value)
        return this
    }

    actual fun blobGarbageCollectionForceThreshold(): Double {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_blob_gc_force_threshold(native)
    }

    actual fun setBlobCompactionReadaheadSize(value: Long): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_blob_compaction_readahead_size(native, value.asUInt64())
        return this
    }

    actual fun blobCompactionReadaheadSize(): Long {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_blob_compaction_readahead_size(native).toCheckedLong("blobCompactionReadaheadSize")
    }

    actual fun setBlobFileStartingLevel(value: Int): Options {
        checkOwningHandle()
        require(value >= 0) { "starting level must be non-negative" }
        rocksdb.rocksdb_options_set_blob_file_starting_level(native, value)
        return this
    }

    actual fun blobFileStartingLevel(): Int {
        checkOwningHandle()
        return rocksdb.rocksdb_options_get_blob_file_starting_level(native)
    }

    actual fun setPrepopulateBlobCache(value: PrepopulateBlobCache): Options {
        checkOwningHandle()
        rocksdb.rocksdb_options_set_prepopulate_blob_cache(native, value.ordinal)
        return this
    }

    actual fun prepopulateBlobCache(): PrepopulateBlobCache {
        checkOwningHandle()
        return PrepopulateBlobCache.entries.first { it.ordinal == rocksdb.rocksdb_options_get_prepopulate_blob_cache(native) }
    }


    actual fun setOpenFilesAsync(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_open_files_async(native, value.toUByte())
        return this
    }

    actual fun openFilesAsync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_open_files_async(native).toBoolean()
    }

    actual fun setMaxFileOpeningThreads(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_file_opening_threads(native, value)
        return this
    }

    actual fun maxFileOpeningThreads(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_file_opening_threads(native)
    }

    actual fun setMaxTotalWalSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_total_wal_size(native, value.asUInt64())
        return this
    }

    actual fun maxTotalWalSize(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_total_wal_size(native).toCheckedLong("max_total_wal_size")
    }

    actual fun setDbLogDir(value: String): Options {
        checkOwningHandle()
        require('\u0000' !in value) { "Option strings must not contain NUL" }
        rocksdb_options_set_db_log_dir(native, value.checkedNativePath())
        return this
    }

    actual fun dbLogDir(): String {
        checkOwningHandle()
        return readOptionsString { size -> rocksdb_options_get_db_log_dir(native, size) }
    }

    actual fun setWalDir(value: String): Options {
        checkOwningHandle()
        require('\u0000' !in value) { "Option strings must not contain NUL" }
        rocksdb_options_set_wal_dir(native, value.checkedNativePath())
        return this
    }

    actual fun walDir(): String {
        checkOwningHandle()
        return readOptionsString { size -> rocksdb_options_get_wal_dir(native, size) }
    }

    actual fun setDeleteObsoleteFilesPeriodMicros(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_delete_obsolete_files_period_micros(native, value.asUInt64())
        return this
    }

    actual fun deleteObsoleteFilesPeriodMicros(): Long {
        checkOwningHandle()
        return rocksdb_options_get_delete_obsolete_files_period_micros(native).toCheckedLong("delete_obsolete_files_period_micros")
    }

    actual fun setMaxBackgroundCompactions(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_background_compactions(native, value)
        return this
    }

    actual fun maxBackgroundCompactions(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_background_compactions(native)
    }

    actual fun setMaxSubcompactions(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_subcompactions(native, value.asUInt32())
        return this
    }

    actual fun maxSubcompactions(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_subcompactions(native).toCheckedInt("max_subcompactions")
    }

    actual fun setMaxBackgroundFlushes(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_background_flushes(native, value)
        return this
    }

    actual fun maxBackgroundFlushes(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_background_flushes(native)
    }

    actual fun setMaxBackgroundJobs(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_background_jobs(native, value)
        return this
    }

    actual fun maxBackgroundJobs(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_background_jobs(native)
    }

    actual fun setRecycleLogFileNum(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_recycle_log_file_num(native, value.asSizeT())
        return this
    }

    actual fun recycleLogFileNum(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_recycle_log_file_num(native), "recycle_log_file_num")
    }

    actual fun setMaxManifestFileSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_manifest_file_size(native, value.asSizeT())
        return this
    }

    actual fun maxManifestFileSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_manifest_file_size(native), "max_manifest_file_size")
    }

    actual fun setTableCacheNumshardbits(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_table_cache_numshardbits(native, value)
        return this
    }

    actual fun tableCacheNumshardbits(): Int {
        checkOwningHandle()
        return rocksdb_options_get_table_cache_numshardbits(native)
    }

    actual fun setWalTtlSeconds(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_WAL_ttl_seconds(native, value.asUInt64())
        return this
    }

    actual fun walTtlSeconds(): Long {
        checkOwningHandle()
        return rocksdb_options_get_WAL_ttl_seconds(native).toCheckedLong("WAL_ttl_seconds")
    }

    actual fun setMaxWriteBatchGroupSizeBytes(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_write_batch_group_size_bytes(native, value.asUInt64())
        return this
    }

    actual fun maxWriteBatchGroupSizeBytes(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_write_batch_group_size_bytes(native).toCheckedLong("max_write_batch_group_size_bytes")
    }

    actual fun setManifestPreallocationSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_manifest_preallocation_size(native, value.asSizeT())
        return this
    }

    actual fun manifestPreallocationSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_manifest_preallocation_size(native), "manifest_preallocation_size")
    }

    actual fun setUseDirectReads(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_use_direct_reads(native, value.toUByte())
        return this
    }

    actual fun useDirectReads(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_direct_reads(native).toBoolean()
    }

    actual fun setUseDirectIoForFlushAndCompaction(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_use_direct_io_for_flush_and_compaction(native, value.toUByte())
        return this
    }

    actual fun useDirectIoForFlushAndCompaction(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_direct_io_for_flush_and_compaction(native).toBoolean()
    }

    actual fun setAllowFAllocate(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_allow_fallocate(native, value.toUByte())
        return this
    }

    actual fun allowFAllocate(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_fallocate(native).toBoolean()
    }

    actual fun setAllowMmapReads(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_allow_mmap_reads(native, value.toUByte())
        return this
    }

    actual fun allowMmapReads(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_mmap_reads(native).toBoolean()
    }

    actual fun setAllowMmapWrites(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_allow_mmap_writes(native, value.toUByte())
        return this
    }

    actual fun allowMmapWrites(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_mmap_writes(native).toBoolean()
    }

    actual fun setIsFdCloseOnExec(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_is_fd_close_on_exec(native, value.toUByte())
        return this
    }

    actual fun isFdCloseOnExec(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_is_fd_close_on_exec(native).toBoolean()
    }

    actual fun setStatsDumpPeriodSec(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_stats_dump_period_sec(native, value.asUInt32())
        return this
    }

    actual fun statsDumpPeriodSec(): Int {
        checkOwningHandle()
        return rocksdb_options_get_stats_dump_period_sec(native).toCheckedInt("stats_dump_period_sec")
    }

    actual fun setMaxCompactionTriggerWakeupSeconds(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_compaction_trigger_wakeup_seconds(native, value.asUInt64())
        return this
    }

    actual fun maxCompactionTriggerWakeupSeconds(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_compaction_trigger_wakeup_seconds(native).toCheckedLong("max_compaction_trigger_wakeup_seconds")
    }

    actual fun setStatsPersistPeriodSec(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_stats_persist_period_sec(native, value.asUInt32())
        return this
    }

    actual fun statsPersistPeriodSec(): Int {
        checkOwningHandle()
        return rocksdb_options_get_stats_persist_period_sec(native).toCheckedInt("stats_persist_period_sec")
    }

    actual fun setStatsHistoryBufferSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_stats_history_buffer_size(native, value.asSizeT())
        return this
    }

    actual fun statsHistoryBufferSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_stats_history_buffer_size(native), "stats_history_buffer_size")
    }

    actual fun setAdviseRandomOnOpen(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_advise_random_on_open(native, value.toUByte())
        return this
    }

    actual fun adviseRandomOnOpen(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_advise_random_on_open(native).toBoolean()
    }

    actual fun setDbWriteBufferSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_db_write_buffer_size(native, value.asSizeT())
        return this
    }

    actual fun dbWriteBufferSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_db_write_buffer_size(native), "db_write_buffer_size")
    }

    actual fun setCompactionReadaheadSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_compaction_readahead_size(native, value.asSizeT())
        return this
    }

    actual fun compactionReadaheadSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_compaction_readahead_size(native), "compaction_readahead_size")
    }

    actual fun setDailyOffpeakTimeUTC(value: String): Options {
        checkOwningHandle()
        require('\u0000' !in value) { "Option strings must not contain NUL" }
        rocksdb_options_set_daily_offpeak_time_utc(native, value)
        return this
    }

    actual fun dailyOffpeakTimeUTC(): String {
        checkOwningHandle()
        return readOptionsString { size -> rocksdb_options_get_daily_offpeak_time_utc(native, size) }
    }

    actual fun setWritableFileMaxBufferSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_writable_file_max_buffer_size(native, value.asUInt64())
        return this
    }

    actual fun writableFileMaxBufferSize(): Long {
        checkOwningHandle()
        return rocksdb_options_get_writable_file_max_buffer_size(native).toCheckedLong("writable_file_max_buffer_size")
    }

    actual fun setUseAdaptiveMutex(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_use_adaptive_mutex(native, value.toUByte())
        return this
    }

    actual fun useAdaptiveMutex(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_adaptive_mutex(native).toBoolean()
    }

    actual fun setWalBytesPerSync(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_wal_bytes_per_sync(native, value.asUInt64())
        return this
    }

    actual fun walBytesPerSync(): Long {
        checkOwningHandle()
        return rocksdb_options_get_wal_bytes_per_sync(native).toCheckedLong("wal_bytes_per_sync")
    }

    actual fun setStrictBytesPerSync(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_strict_bytes_per_sync(native, value.toUByte())
        return this
    }

    actual fun strictBytesPerSync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_strict_bytes_per_sync(native).toBoolean()
    }

    actual fun setEnableThreadTracking(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_enable_thread_tracking(native, value.toUByte())
        return this
    }

    actual fun enableThreadTracking(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_enable_thread_tracking(native).toBoolean()
    }

    actual fun setDelayedWriteRate(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_delayed_write_rate(native, value.asUInt64())
        return this
    }

    actual fun delayedWriteRate(): Long {
        checkOwningHandle()
        return rocksdb_options_get_delayed_write_rate(native).toCheckedLong("delayed_write_rate")
    }

    actual fun setEnablePipelinedWrite(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_enable_pipelined_write(native, value.toUByte())
        return this
    }

    actual fun enablePipelinedWrite(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_enable_pipelined_write(native).toBoolean()
    }

    actual fun setUnorderedWrite(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_unordered_write(native, value.toUByte())
        return this
    }

    actual fun unorderedWrite(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_unordered_write(native).toBoolean()
    }

    actual fun setAllowConcurrentMemtableWrite(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_allow_concurrent_memtable_write(native, value.toUByte())
        return this
    }

    actual fun allowConcurrentMemtableWrite(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_concurrent_memtable_write(native).toBoolean()
    }

    actual fun setEnableWriteThreadAdaptiveYield(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_enable_write_thread_adaptive_yield(native, value.toUByte())
        return this
    }

    actual fun enableWriteThreadAdaptiveYield(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_enable_write_thread_adaptive_yield(native).toBoolean()
    }

    actual fun setWriteThreadMaxYieldUsec(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_write_thread_max_yield_usec(native, value.asUInt64())
        return this
    }

    actual fun writeThreadMaxYieldUsec(): Long {
        checkOwningHandle()
        return rocksdb_options_get_write_thread_max_yield_usec(native).toCheckedLong("write_thread_max_yield_usec")
    }

    actual fun setWriteThreadSlowYieldUsec(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_write_thread_slow_yield_usec(native, value.asUInt64())
        return this
    }

    actual fun writeThreadSlowYieldUsec(): Long {
        checkOwningHandle()
        return rocksdb_options_get_write_thread_slow_yield_usec(native).toCheckedLong("write_thread_slow_yield_usec")
    }

    actual fun setSkipStatsUpdateOnDbOpen(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_skip_stats_update_on_db_open(native, value.toUByte())
        return this
    }

    actual fun skipStatsUpdateOnDbOpen(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_skip_stats_update_on_db_open(native).toBoolean()
    }

    actual fun setAllow2pc(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_allow_2pc(native, value.toUByte())
        return this
    }

    actual fun allow2pc(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_2pc(native).toBoolean()
    }

    actual fun setDumpMallocStats(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_dump_malloc_stats(native, value.toUByte())
        return this
    }

    actual fun dumpMallocStats(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_dump_malloc_stats(native).toBoolean()
    }

    actual fun setAvoidFlushDuringRecovery(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_avoid_flush_during_recovery(native, value.toUByte())
        return this
    }

    actual fun avoidFlushDuringRecovery(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_avoid_flush_during_recovery(native).toBoolean()
    }

    actual fun setAvoidFlushDuringShutdown(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_avoid_flush_during_shutdown(native, value.toUByte())
        return this
    }

    actual fun avoidFlushDuringShutdown(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_avoid_flush_during_shutdown(native).toBoolean()
    }

    actual fun setAllowIngestBehind(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_allow_ingest_behind(native, value.toUByte())
        return this
    }

    actual fun allowIngestBehind(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_ingest_behind(native).toBoolean()
    }

    actual fun setTwoWriteQueues(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_two_write_queues(native, value.toUByte())
        return this
    }

    actual fun twoWriteQueues(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_two_write_queues(native).toBoolean()
    }

    actual fun setManualWalFlush(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_manual_wal_flush(native, value.toUByte())
        return this
    }

    actual fun manualWalFlush(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_manual_wal_flush(native).toBoolean()
    }

    actual fun setLevelCompactionDynamicLevelBytes(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_level_compaction_dynamic_level_bytes(native, value.toUByte())
        return this
    }

    actual fun levelCompactionDynamicLevelBytes(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_level_compaction_dynamic_level_bytes(native).toBoolean()
    }

    actual fun setArenaBlockSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_arena_block_size(native, value.asSizeT())
        return this
    }

    actual fun arenaBlockSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_arena_block_size(native), "arena_block_size")
    }

    actual fun setMaxSequentialSkipInIterations(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_sequential_skip_in_iterations(native, value.asUInt64())
        return this
    }

    actual fun maxSequentialSkipInIterations(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_sequential_skip_in_iterations(native).toCheckedLong("max_sequential_skip_in_iterations")
    }

    actual fun setInplaceUpdateSupport(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_inplace_update_support(native, value.toUByte())
        return this
    }

    actual fun inplaceUpdateSupport(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_inplace_update_support(native).toBoolean()
    }

    actual fun setInplaceUpdateNumLocks(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_inplace_update_num_locks(native, value.asSizeT())
        return this
    }

    actual fun inplaceUpdateNumLocks(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_inplace_update_num_locks(native), "inplace_update_num_locks")
    }

    actual fun setMemtablePrefixBloomSizeRatio(value: Double): Options {
        checkOwningHandle()
        rocksdb_options_set_memtable_prefix_bloom_size_ratio(native, value)
        return this
    }

    actual fun memtablePrefixBloomSizeRatio(): Double {
        checkOwningHandle()
        return rocksdb_options_get_memtable_prefix_bloom_size_ratio(native)
    }

    actual fun setExperimentalMempurgeThreshold(value: Double): Options {
        checkOwningHandle()
        rocksdb_options_set_experimental_mempurge_threshold(native, value)
        return this
    }

    actual fun experimentalMempurgeThreshold(): Double {
        checkOwningHandle()
        return rocksdb_options_get_experimental_mempurge_threshold(native)
    }

    actual fun setMemtableWholeKeyFiltering(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_memtable_whole_key_filtering(native, value.toUByte())
        return this
    }

    actual fun memtableWholeKeyFiltering(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_memtable_whole_key_filtering(native).toBoolean()
    }

    actual fun setMaxSuccessiveMerges(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_max_successive_merges(native, value.asSizeT())
        return this
    }

    actual fun maxSuccessiveMerges(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_successive_merges(native), "max_successive_merges")
    }

    actual fun setOptimizeFiltersForHits(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_optimize_filters_for_hits(native, if (value) 1 else 0)
        return this
    }

    actual fun optimizeFiltersForHits(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_optimize_filters_for_hits(native).toBoolean()
    }

    actual fun setMemtableHugePageSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_memtable_huge_page_size(native, value.asSizeT())
        return this
    }

    actual fun memtableHugePageSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_memtable_huge_page_size(native), "memtable_huge_page_size")
    }

    actual fun setSoftPendingCompactionBytesLimit(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_soft_pending_compaction_bytes_limit(native, value.asSizeT())
        return this
    }

    actual fun softPendingCompactionBytesLimit(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_soft_pending_compaction_bytes_limit(native), "soft_pending_compaction_bytes_limit")
    }

    actual fun setHardPendingCompactionBytesLimit(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_hard_pending_compaction_bytes_limit(native, value.asSizeT())
        return this
    }

    actual fun hardPendingCompactionBytesLimit(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_hard_pending_compaction_bytes_limit(native), "hard_pending_compaction_bytes_limit")
    }

    actual fun setParanoidFileChecks(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_paranoid_file_checks(native, value.toUByte())
        return this
    }

    actual fun paranoidFileChecks(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_paranoid_file_checks(native).toBoolean()
    }

    actual fun setReportBgIoStats(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_report_bg_io_stats(native, if (value) 1 else 0)
        return this
    }

    actual fun reportBgIoStats(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_report_bg_io_stats(native).toBoolean()
    }

    actual fun setTtl(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_ttl(native, value.asCompactionAgeSeconds())
        return this
    }

    actual fun ttl(): Long {
        checkOwningHandle()
        return rocksdb_options_get_ttl(native).compactionAgeSecondsToLong()
    }

    actual fun setPeriodicCompactionSeconds(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_periodic_compaction_seconds(native, value.asCompactionAgeSeconds())
        return this
    }

    actual fun periodicCompactionSeconds(): Long {
        checkOwningHandle()
        return rocksdb_options_get_periodic_compaction_seconds(native).compactionAgeSecondsToLong()
    }

    actual fun setForceConsistencyChecks(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_force_consistency_checks(native, value.toUByte())
        return this
    }

    actual fun forceConsistencyChecks(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_force_consistency_checks(native).toBoolean()
    }

    actual fun setAtomicFlush(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_atomic_flush(native, value.toUByte())
        return this
    }

    actual fun atomicFlush(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_atomic_flush(native).toBoolean()
    }

    actual fun setAvoidUnnecessaryBlockingIO(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_avoid_unnecessary_blocking_io(native, value.toUByte())
        return this
    }

    actual fun avoidUnnecessaryBlockingIO(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_avoid_unnecessary_blocking_io(native).toBoolean()
    }

    actual fun setPersistStatsToDisk(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_persist_stats_to_disk(native, value.toUByte())
        return this
    }

    actual fun persistStatsToDisk(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_persist_stats_to_disk(native).toBoolean()
    }

    actual fun setWriteDbidToManifest(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_write_dbid_to_manifest(native, value.toUByte())
        return this
    }

    actual fun writeDbidToManifest(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_write_dbid_to_manifest(native).toBoolean()
    }

    actual fun setLogReadaheadSize(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_log_readahead_size(native, value.asSizeT())
        return this
    }

    actual fun logReadaheadSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_log_readahead_size(native), "log_readahead_size")
    }

    actual fun setBestEffortsRecovery(value: Boolean): Options {
        checkOwningHandle()
        rocksdb_options_set_best_efforts_recovery(native, value.toUByte())
        return this
    }

    actual fun bestEffortsRecovery(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_best_efforts_recovery(native).toBoolean()
    }

    actual fun setMaxBgErrorResumeCount(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_max_bgerror_resume_count(native, value)
        return this
    }

    actual fun maxBgerrorResumeCount(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_bgerror_resume_count(native)
    }

    actual fun setBgerrorResumeRetryInterval(value: Long): Options {
        checkOwningHandle()
        rocksdb_options_set_bgerror_resume_retry_interval(native, value.asUInt64())
        return this
    }

    actual fun bgerrorResumeRetryInterval(): Long {
        checkOwningHandle()
        return rocksdb_options_get_bgerror_resume_retry_interval(native).toCheckedLong("bgerror_resume_retry_interval")
    }

    actual fun setMemtableMaxRangeDeletions(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_memtable_max_range_deletions(native, value.asUInt32())
        return this
    }

    actual fun memtableMaxRangeDeletions(): Int {
        checkOwningHandle()
        return rocksdb_options_get_memtable_max_range_deletions(native).toCheckedInt("memtable_max_range_deletions")
    }

    actual fun setMinTombstonesForRangeConversion(value: Int): Options {
        checkOwningHandle()
        rocksdb_options_set_min_tombstones_for_range_conversion(native, value.asUInt32())
        return this
    }

    actual fun minTombstonesForRangeConversion(): Int {
        checkOwningHandle()
        return rocksdb_options_get_min_tombstones_for_range_conversion(native).toCheckedInt("min_tombstones_for_range_conversion")
    }

    actual fun setReadTriggeredCompactionThreshold(value: Double): Options {
        checkOwningHandle()
        rocksdb_options_set_read_triggered_compaction_threshold(native, value)
        return this
    }

    actual fun readTriggeredCompactionThreshold(): Double {
        checkOwningHandle()
        return rocksdb_options_get_read_triggered_compaction_threshold(native)
    }

    actual fun setCompactionPriority(value: CompactionPriority): Options {
        checkOwningHandle()
        rocksdb_options_set_compaction_pri(native, value.value.toInt())
        return this
    }

    actual fun compactionPriority(): CompactionPriority {
        checkOwningHandle()
        return getCompactionPriority(rocksdb_options_get_compaction_pri(native).toByte())
    }

    actual fun optimizeForPointLookup(budget: Long): Options {
        checkOwningHandle()
        rocksdb_options_optimize_for_point_lookup(native, budget.asUInt64())
        return this
    }

    actual fun optimizeLevelStyleCompaction(budget: Long): Options {
        checkOwningHandle()
        rocksdb_options_optimize_level_style_compaction(native, budget.asUInt64())
        return this
    }

    actual fun optimizeLevelStyleCompaction(): Options = optimizeLevelStyleCompaction(536870912L)

    actual fun optimizeUniversalStyleCompaction(budget: Long): Options {
        checkOwningHandle()
        rocksdb_options_optimize_universal_style_compaction(native, budget.asUInt64())
        return this
    }

    actual fun optimizeUniversalStyleCompaction(): Options = optimizeUniversalStyleCompaction(536870912L)

    actual fun prepareForBulkLoad(): Options {
        checkOwningHandle()
        rocksdb_options_prepare_for_bulk_load(native)
        return this
    }

    actual fun setIncreaseParallelism(totalThreads: Int): Options {
        checkOwningHandle()
        rocksdb_options_increase_parallelism(native, totalThreads)
        return this
    }


    actual fun setBottommostCompressionType(compressionType: CompressionType): Options {
        checkOwningHandle()
        rocksdb_options_set_bottommost_compression(native, compressionType.value.toInt())
        return this
    }

    actual fun bottommostCompressionType(): CompressionType {
        checkOwningHandle()
        return getCompressionType(rocksdb_options_get_bottommost_compression(native).toByte())
    }

}
