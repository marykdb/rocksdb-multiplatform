@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import maryk.asUInt32
import maryk.toCheckedInt
import rocksdb.rocksdb_options_compaction_readahead_size
import rocksdb.rocksdb_options_get_WAL_ttl_seconds
import rocksdb.rocksdb_options_get_advise_random_on_open
import rocksdb.rocksdb_options_get_allow_2pc
import rocksdb.rocksdb_options_get_allow_concurrent_memtable_write
import rocksdb.rocksdb_options_get_allow_fallocate
import rocksdb.rocksdb_options_get_allow_ingest_behind
import rocksdb.rocksdb_options_get_allow_mmap_reads
import rocksdb.rocksdb_options_get_allow_mmap_writes
import rocksdb.rocksdb_options_get_atomic_flush
import rocksdb.rocksdb_options_get_avoid_flush_during_recovery
import rocksdb.rocksdb_options_get_avoid_flush_during_shutdown
import rocksdb.rocksdb_options_get_avoid_unnecessary_blocking_io
import rocksdb.rocksdb_options_get_best_efforts_recovery
import rocksdb.rocksdb_options_get_bgerror_resume_retry_interval
import rocksdb.rocksdb_options_get_bytes_per_sync
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
import rocksdb.rocksdb_options_get_is_fd_close_on_exec
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
import rocksdb.rocksdb_options_get_max_open_files
import rocksdb.rocksdb_options_get_max_subcompactions
import rocksdb.rocksdb_options_get_max_total_wal_size
import rocksdb.rocksdb_options_get_max_write_batch_group_size_bytes
import rocksdb.rocksdb_options_get_open_files_async
import rocksdb.rocksdb_options_get_persist_stats_to_disk
import rocksdb.rocksdb_options_get_recycle_log_file_num
import rocksdb.rocksdb_options_get_skip_stats_update_on_db_open
import rocksdb.rocksdb_options_get_stats_dump_period_sec
import rocksdb.rocksdb_options_get_stats_history_buffer_size
import rocksdb.rocksdb_options_get_stats_persist_period_sec
import rocksdb.rocksdb_options_get_strict_bytes_per_sync
import rocksdb.rocksdb_options_get_table_cache_numshardbits
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
import rocksdb.rocksdb_options_set_WAL_ttl_seconds
import rocksdb.rocksdb_options_set_advise_random_on_open
import rocksdb.rocksdb_options_set_allow_2pc
import rocksdb.rocksdb_options_set_allow_concurrent_memtable_write
import rocksdb.rocksdb_options_set_allow_fallocate
import rocksdb.rocksdb_options_set_allow_ingest_behind
import rocksdb.rocksdb_options_set_allow_mmap_reads
import rocksdb.rocksdb_options_set_allow_mmap_writes
import rocksdb.rocksdb_options_set_atomic_flush
import rocksdb.rocksdb_options_set_avoid_flush_during_recovery
import rocksdb.rocksdb_options_set_avoid_flush_during_shutdown
import rocksdb.rocksdb_options_set_avoid_unnecessary_blocking_io
import rocksdb.rocksdb_options_set_best_efforts_recovery
import rocksdb.rocksdb_options_set_bgerror_resume_retry_interval
import rocksdb.rocksdb_options_set_bytes_per_sync
import rocksdb.rocksdb_options_set_daily_offpeak_time_utc
import rocksdb.rocksdb_options_set_db_log_dir
import rocksdb.rocksdb_options_set_db_write_buffer_size
import rocksdb.rocksdb_options_set_delayed_write_rate
import rocksdb.rocksdb_options_set_delete_obsolete_files_period_micros
import rocksdb.rocksdb_options_set_dump_malloc_stats
import rocksdb.rocksdb_options_set_enable_pipelined_write
import rocksdb.rocksdb_options_set_enable_thread_tracking
import rocksdb.rocksdb_options_set_enable_write_thread_adaptive_yield
import rocksdb.rocksdb_options_set_is_fd_close_on_exec
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
import rocksdb.rocksdb_options_set_max_open_files
import rocksdb.rocksdb_options_set_max_subcompactions
import rocksdb.rocksdb_options_set_max_total_wal_size
import rocksdb.rocksdb_options_set_max_write_batch_group_size_bytes
import rocksdb.rocksdb_options_set_open_files_async
import rocksdb.rocksdb_options_set_persist_stats_to_disk
import rocksdb.rocksdb_options_set_recycle_log_file_num
import rocksdb.rocksdb_options_set_skip_stats_update_on_db_open
import rocksdb.rocksdb_options_set_stats_dump_period_sec
import rocksdb.rocksdb_options_set_stats_history_buffer_size
import rocksdb.rocksdb_options_set_stats_persist_period_sec
import rocksdb.rocksdb_options_set_strict_bytes_per_sync
import rocksdb.rocksdb_options_set_table_cache_numshardbits
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
import maryk.asUInt64
import maryk.sizeTToLong
import maryk.toBoolean
import maryk.toCheckedLong
import maryk.toUByte
import rocksdb.rocksdb_options_add_eventlistener
import rocksdb.rocksdb_options_create
import rocksdb.rocksdb_options_destroy
import rocksdb.rocksdb_options_get_WAL_size_limit_MB
import rocksdb.rocksdb_options_get_create_if_missing
import rocksdb.rocksdb_options_get_create_missing_column_families
import rocksdb.rocksdb_options_get_error_if_exists
import rocksdb.rocksdb_options_get_info_log_level
import rocksdb.rocksdb_options_get_keep_log_file_num
import rocksdb.rocksdb_options_get_log_file_time_to_roll
import rocksdb.rocksdb_options_get_max_log_file_size
import rocksdb.rocksdb_options_get_paranoid_checks
import rocksdb.rocksdb_options_get_wal_recovery_mode
import rocksdb.rocksdb_options_get_use_fsync
import rocksdb.rocksdb_options_set_WAL_size_limit_MB
import rocksdb.rocksdb_options_set_create_if_missing
import rocksdb.rocksdb_options_set_create_missing_column_families
import rocksdb.rocksdb_options_set_error_if_exists
import rocksdb.rocksdb_options_set_info_log_level
import rocksdb.rocksdb_options_set_keep_log_file_num
import rocksdb.rocksdb_options_set_log_file_time_to_roll
import rocksdb.rocksdb_options_set_max_log_file_size
import rocksdb.rocksdb_options_set_paranoid_checks
import rocksdb.rocksdb_options_set_use_fsync
import rocksdb.rocksdb_options_set_wal_recovery_mode
import rocksdb.rocksdb_options_set_env

actual fun DBOptions.addEventListener(listener: EventListener): DBOptions {
    checkOwningHandle()
    check(listener.disownHandle()) { "EventListener is already closed or registered." }
    rocksdb_options_add_eventlistener(native, listener.native)
    return this
}

actual fun Options.addEventListener(listener: EventListener): Options {
    checkOwningHandle()
    check(listener.disownHandle()) { "EventListener is already closed or registered." }
    rocksdb_options_add_eventlistener(native, listener.native)
    return this
}

actual class DBOptions internal constructor(
    internal val native: CPointer<rocksdb_options_t>
) : RocksObject(), DBOptionsInterface<DBOptions> {
    private var statistics: Statistics? = null
    private var writeBufferManager: WriteBufferManager? = null
    private var env: Env? = null

    actual constructor() : this(requireNotNull(rocksdb_options_create()) { "Unable to allocate RocksDB DB options" })

    actual fun setRowCache(rowCache: Cache): DBOptions {
        checkOwningHandle()
        rowCache.checkOwningHandle()
        rocksdb.rocksdb_options_set_row_cache(native, rowCache.native)
        return this
    }

    override fun close() {
        if (tryClose()) {
            statistics?.disconnectFromNative(native)
            rocksdb_options_destroy(native)
            statistics = null
            writeBufferManager = null
            env = null
            super.close()
        }
    }

    actual fun setCreateIfMissing(flag: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_create_if_missing(native, flag.toUByte())
        return this
    }

    actual fun createIfMissing(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_create_if_missing(native).toBoolean()
    }

    actual fun setCreateMissingColumnFamilies(flag: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_create_missing_column_families(native, flag.toUByte())
        return this
    }

    actual fun createMissingColumnFamilies(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_create_missing_column_families(native).toBoolean()
    }

    actual fun setErrorIfExists(errorIfExists: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_error_if_exists(native, errorIfExists.toUByte())
        return this
    }

    actual fun errorIfExists(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_error_if_exists(native).toBoolean()
    }

    actual fun setParanoidChecks(paranoidChecks: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_paranoid_checks(native, paranoidChecks.toUByte())
        return this
    }

    actual fun paranoidChecks(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_paranoid_checks(native).toBoolean()
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_info_log_level(native, infoLogLevel.value.toInt())
        return this
    }

    actual fun infoLogLevel(): InfoLogLevel {
        checkOwningHandle()
        return getInfoLogLevel(
            rocksdb_options_get_info_log_level(native).toUByte()
        )
    }

    actual fun setStatistics(statistics: Statistics): DBOptions {
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

    actual override fun setEnv(env: Env): DBOptions {
        checkOwningHandle()
        env.checkOwningHandle()
        rocksdb_options_set_env(native, env.native)
        this.env = env
        return this
    }

    actual override fun getEnv(): Env {
        checkOwningHandle()
        if (env == null) {
            env = getDefaultEnv()
        }
        return requireNotNull(env)
    }

    internal fun attachWriteBufferManager(manager: WriteBufferManager): DBOptions {
        checkOwningHandle()
        manager.checkOwningHandle()
        rocksdb.rocksdb_options_set_write_buffer_manager(native, manager.native)
        writeBufferManager = manager
        return this
    }

    internal fun retainedNativeReferences(): List<Any> =
        listOfNotNull(env, statistics, writeBufferManager)

    actual fun setUseFsync(useFsync: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_use_fsync(native, if (useFsync) 1 else 0)
        return this
    }

    actual fun useFsync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_fsync(native) == 1
    }

    actual fun setMaxLogFileSize(maxLogFileSize: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_log_file_size(native, maxLogFileSize.asSizeT())
        return this
    }

    actual fun maxLogFileSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_log_file_size(native), "DB options max log file size")
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_log_file_time_to_roll(native, logFileTimeToRoll.asSizeT())
        return this
    }

    actual fun logFileTimeToRoll(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_log_file_time_to_roll(native), "DB options log file time to roll")
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_keep_log_file_num(native, keepLogFileNum.asSizeT())
        return this
    }

    actual fun keepLogFileNum(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_keep_log_file_num(native), "DB options keep log file count")
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_WAL_size_limit_MB(native, sizeLimitMB.asUInt64())
        return this
    }

    actual fun walSizeLimitMB(): Long {
        checkOwningHandle()
        return rocksdb_options_get_WAL_size_limit_MB(native).toCheckedLong("DB options WAL size limit MB")
    }

    actual fun setWalRecoveryMode(mode: WALRecoveryMode): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_wal_recovery_mode(native, mode.getValue().toInt())
        return this
    }

    actual fun walRecoveryMode(): WALRecoveryMode {
        checkOwningHandle()
        return walRecoveryModeFromValue(rocksdb_options_get_wal_recovery_mode(native).toByte())
    }

    actual fun setOpenFilesAsync(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_open_files_async(native, value.toUByte())
        return this
    }

    actual fun openFilesAsync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_open_files_async(native).toBoolean()
    }

    actual fun setMaxOpenFiles(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_open_files(native, value)
        return this
    }

    actual fun maxOpenFiles(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_open_files(native)
    }

    actual fun setMaxFileOpeningThreads(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_file_opening_threads(native, value)
        return this
    }

    actual fun maxFileOpeningThreads(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_file_opening_threads(native)
    }

    actual fun setMaxTotalWalSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_total_wal_size(native, value.asUInt64())
        return this
    }

    actual fun maxTotalWalSize(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_total_wal_size(native).toCheckedLong("max_total_wal_size")
    }

    actual fun setDbLogDir(value: String): DBOptions {
        checkOwningHandle()
        require('\u0000' !in value) { "Option strings must not contain NUL" }
        rocksdb_options_set_db_log_dir(native, value)
        return this
    }

    actual fun dbLogDir(): String {
        checkOwningHandle()
        return readOptionsString { size -> rocksdb_options_get_db_log_dir(native, size) }
    }

    actual fun setWalDir(value: String): DBOptions {
        checkOwningHandle()
        require('\u0000' !in value) { "Option strings must not contain NUL" }
        rocksdb_options_set_wal_dir(native, value)
        return this
    }

    actual fun walDir(): String {
        checkOwningHandle()
        return readOptionsString { size -> rocksdb_options_get_wal_dir(native, size) }
    }

    actual fun setDeleteObsoleteFilesPeriodMicros(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_delete_obsolete_files_period_micros(native, value.asUInt64())
        return this
    }

    actual fun deleteObsoleteFilesPeriodMicros(): Long {
        checkOwningHandle()
        return rocksdb_options_get_delete_obsolete_files_period_micros(native).toCheckedLong("delete_obsolete_files_period_micros")
    }

    actual fun setMaxBackgroundJobs(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_background_jobs(native, value)
        return this
    }

    actual fun maxBackgroundJobs(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_background_jobs(native)
    }

    actual fun setMaxBackgroundCompactions(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_background_compactions(native, value)
        return this
    }

    actual fun maxBackgroundCompactions(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_background_compactions(native)
    }

    actual fun setMaxSubcompactions(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_subcompactions(native, value.asUInt32())
        return this
    }

    actual fun maxSubcompactions(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_subcompactions(native).toCheckedInt("max_subcompactions")
    }

    actual fun setMaxBackgroundFlushes(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_background_flushes(native, value)
        return this
    }

    actual fun maxBackgroundFlushes(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_background_flushes(native)
    }

    actual fun setRecycleLogFileNum(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_recycle_log_file_num(native, value.asSizeT())
        return this
    }

    actual fun recycleLogFileNum(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_recycle_log_file_num(native), "recycle_log_file_num")
    }

    actual fun setMaxManifestFileSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_manifest_file_size(native, value.asSizeT())
        return this
    }

    actual fun maxManifestFileSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_manifest_file_size(native), "max_manifest_file_size")
    }

    actual fun setTableCacheNumshardbits(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_table_cache_numshardbits(native, value)
        return this
    }

    actual fun tableCacheNumshardbits(): Int {
        checkOwningHandle()
        return rocksdb_options_get_table_cache_numshardbits(native)
    }

    actual fun setWalTtlSeconds(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_WAL_ttl_seconds(native, value.asUInt64())
        return this
    }

    actual fun walTtlSeconds(): Long {
        checkOwningHandle()
        return rocksdb_options_get_WAL_ttl_seconds(native).toCheckedLong("WAL_ttl_seconds")
    }

    actual fun setMaxWriteBatchGroupSizeBytes(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_write_batch_group_size_bytes(native, value.asUInt64())
        return this
    }

    actual fun maxWriteBatchGroupSizeBytes(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_write_batch_group_size_bytes(native).toCheckedLong("max_write_batch_group_size_bytes")
    }

    actual fun setManifestPreallocationSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_manifest_preallocation_size(native, value.asSizeT())
        return this
    }

    actual fun manifestPreallocationSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_manifest_preallocation_size(native), "manifest_preallocation_size")
    }

    actual fun setAllowMmapReads(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_allow_mmap_reads(native, value.toUByte())
        return this
    }

    actual fun allowMmapReads(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_mmap_reads(native).toBoolean()
    }

    actual fun setAllowMmapWrites(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_allow_mmap_writes(native, value.toUByte())
        return this
    }

    actual fun allowMmapWrites(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_mmap_writes(native).toBoolean()
    }

    actual fun setUseDirectReads(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_use_direct_reads(native, value.toUByte())
        return this
    }

    actual fun useDirectReads(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_direct_reads(native).toBoolean()
    }

    actual fun setUseDirectIoForFlushAndCompaction(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_use_direct_io_for_flush_and_compaction(native, value.toUByte())
        return this
    }

    actual fun useDirectIoForFlushAndCompaction(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_direct_io_for_flush_and_compaction(native).toBoolean()
    }

    actual fun setAllowFAllocate(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_allow_fallocate(native, value.toUByte())
        return this
    }

    actual fun allowFAllocate(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_fallocate(native).toBoolean()
    }

    actual fun setIsFdCloseOnExec(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_is_fd_close_on_exec(native, value.toUByte())
        return this
    }

    actual fun isFdCloseOnExec(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_is_fd_close_on_exec(native).toBoolean()
    }

    actual fun setStatsDumpPeriodSec(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_stats_dump_period_sec(native, value.asUInt32())
        return this
    }

    actual fun statsDumpPeriodSec(): Int {
        checkOwningHandle()
        return rocksdb_options_get_stats_dump_period_sec(native).toCheckedInt("stats_dump_period_sec")
    }

    actual fun setMaxCompactionTriggerWakeupSeconds(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_compaction_trigger_wakeup_seconds(native, value.asUInt64())
        return this
    }

    actual fun maxCompactionTriggerWakeupSeconds(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_compaction_trigger_wakeup_seconds(native).toCheckedLong("max_compaction_trigger_wakeup_seconds")
    }

    actual fun setStatsPersistPeriodSec(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_stats_persist_period_sec(native, value.asUInt32())
        return this
    }

    actual fun statsPersistPeriodSec(): Int {
        checkOwningHandle()
        return rocksdb_options_get_stats_persist_period_sec(native).toCheckedInt("stats_persist_period_sec")
    }

    actual fun setStatsHistoryBufferSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_stats_history_buffer_size(native, value.asSizeT())
        return this
    }

    actual fun statsHistoryBufferSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_stats_history_buffer_size(native), "stats_history_buffer_size")
    }

    actual fun setAdviseRandomOnOpen(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_advise_random_on_open(native, value.toUByte())
        return this
    }

    actual fun adviseRandomOnOpen(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_advise_random_on_open(native).toBoolean()
    }

    actual fun setDbWriteBufferSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_db_write_buffer_size(native, value.asSizeT())
        return this
    }

    actual fun dbWriteBufferSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_db_write_buffer_size(native), "db_write_buffer_size")
    }

    actual fun setCompactionReadaheadSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_compaction_readahead_size(native, value.asSizeT())
        return this
    }

    actual fun compactionReadaheadSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_compaction_readahead_size(native), "compaction_readahead_size")
    }

    actual fun setDailyOffpeakTimeUTC(value: String): DBOptions {
        checkOwningHandle()
        require('\u0000' !in value) { "Option strings must not contain NUL" }
        rocksdb_options_set_daily_offpeak_time_utc(native, value)
        return this
    }

    actual fun dailyOffpeakTimeUTC(): String {
        checkOwningHandle()
        return readOptionsString { size -> rocksdb_options_get_daily_offpeak_time_utc(native, size) }
    }

    actual fun setWritableFileMaxBufferSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_writable_file_max_buffer_size(native, value.asUInt64())
        return this
    }

    actual fun writableFileMaxBufferSize(): Long {
        checkOwningHandle()
        return rocksdb_options_get_writable_file_max_buffer_size(native).toCheckedLong("writable_file_max_buffer_size")
    }

    actual fun setUseAdaptiveMutex(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_use_adaptive_mutex(native, value.toUByte())
        return this
    }

    actual fun useAdaptiveMutex(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_use_adaptive_mutex(native).toBoolean()
    }

    actual fun setBytesPerSync(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_bytes_per_sync(native, value.asUInt64())
        return this
    }

    actual fun bytesPerSync(): Long {
        checkOwningHandle()
        return rocksdb_options_get_bytes_per_sync(native).toCheckedLong("bytes_per_sync")
    }

    actual fun setWalBytesPerSync(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_wal_bytes_per_sync(native, value.asUInt64())
        return this
    }

    actual fun walBytesPerSync(): Long {
        checkOwningHandle()
        return rocksdb_options_get_wal_bytes_per_sync(native).toCheckedLong("wal_bytes_per_sync")
    }

    actual fun setStrictBytesPerSync(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_strict_bytes_per_sync(native, value.toUByte())
        return this
    }

    actual fun strictBytesPerSync(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_strict_bytes_per_sync(native).toBoolean()
    }

    actual fun setEnableThreadTracking(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_enable_thread_tracking(native, value.toUByte())
        return this
    }

    actual fun enableThreadTracking(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_enable_thread_tracking(native).toBoolean()
    }

    actual fun setDelayedWriteRate(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_delayed_write_rate(native, value.asUInt64())
        return this
    }

    actual fun delayedWriteRate(): Long {
        checkOwningHandle()
        return rocksdb_options_get_delayed_write_rate(native).toCheckedLong("delayed_write_rate")
    }

    actual fun setEnablePipelinedWrite(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_enable_pipelined_write(native, value.toUByte())
        return this
    }

    actual fun enablePipelinedWrite(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_enable_pipelined_write(native).toBoolean()
    }

    actual fun setUnorderedWrite(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_unordered_write(native, value.toUByte())
        return this
    }

    actual fun unorderedWrite(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_unordered_write(native).toBoolean()
    }

    actual fun setAllowConcurrentMemtableWrite(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_allow_concurrent_memtable_write(native, value.toUByte())
        return this
    }

    actual fun allowConcurrentMemtableWrite(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_concurrent_memtable_write(native).toBoolean()
    }

    actual fun setEnableWriteThreadAdaptiveYield(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_enable_write_thread_adaptive_yield(native, value.toUByte())
        return this
    }

    actual fun enableWriteThreadAdaptiveYield(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_enable_write_thread_adaptive_yield(native).toBoolean()
    }

    actual fun setWriteThreadMaxYieldUsec(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_write_thread_max_yield_usec(native, value.asUInt64())
        return this
    }

    actual fun writeThreadMaxYieldUsec(): Long {
        checkOwningHandle()
        return rocksdb_options_get_write_thread_max_yield_usec(native).toCheckedLong("write_thread_max_yield_usec")
    }

    actual fun setWriteThreadSlowYieldUsec(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_write_thread_slow_yield_usec(native, value.asUInt64())
        return this
    }

    actual fun writeThreadSlowYieldUsec(): Long {
        checkOwningHandle()
        return rocksdb_options_get_write_thread_slow_yield_usec(native).toCheckedLong("write_thread_slow_yield_usec")
    }

    actual fun setSkipStatsUpdateOnDbOpen(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_skip_stats_update_on_db_open(native, value.toUByte())
        return this
    }

    actual fun skipStatsUpdateOnDbOpen(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_skip_stats_update_on_db_open(native).toBoolean()
    }

    actual fun setAllow2pc(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_allow_2pc(native, value.toUByte())
        return this
    }

    actual fun allow2pc(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_2pc(native).toBoolean()
    }

    actual fun setDumpMallocStats(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_dump_malloc_stats(native, value.toUByte())
        return this
    }

    actual fun dumpMallocStats(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_dump_malloc_stats(native).toBoolean()
    }

    actual fun setAvoidFlushDuringRecovery(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_avoid_flush_during_recovery(native, value.toUByte())
        return this
    }

    actual fun avoidFlushDuringRecovery(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_avoid_flush_during_recovery(native).toBoolean()
    }

    actual fun setAvoidFlushDuringShutdown(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_avoid_flush_during_shutdown(native, value.toUByte())
        return this
    }

    actual fun avoidFlushDuringShutdown(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_avoid_flush_during_shutdown(native).toBoolean()
    }

    actual fun setAllowIngestBehind(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_allow_ingest_behind(native, value.toUByte())
        return this
    }

    actual fun allowIngestBehind(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_allow_ingest_behind(native).toBoolean()
    }

    actual fun setTwoWriteQueues(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_two_write_queues(native, value.toUByte())
        return this
    }

    actual fun twoWriteQueues(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_two_write_queues(native).toBoolean()
    }

    actual fun setManualWalFlush(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_manual_wal_flush(native, value.toUByte())
        return this
    }

    actual fun manualWalFlush(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_manual_wal_flush(native).toBoolean()
    }

    actual fun setAtomicFlush(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_atomic_flush(native, value.toUByte())
        return this
    }

    actual fun atomicFlush(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_atomic_flush(native).toBoolean()
    }

    actual fun setAvoidUnnecessaryBlockingIO(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_avoid_unnecessary_blocking_io(native, value.toUByte())
        return this
    }

    actual fun avoidUnnecessaryBlockingIO(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_avoid_unnecessary_blocking_io(native).toBoolean()
    }

    actual fun setPersistStatsToDisk(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_persist_stats_to_disk(native, value.toUByte())
        return this
    }

    actual fun persistStatsToDisk(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_persist_stats_to_disk(native).toBoolean()
    }

    actual fun setWriteDbidToManifest(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_write_dbid_to_manifest(native, value.toUByte())
        return this
    }

    actual fun writeDbidToManifest(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_write_dbid_to_manifest(native).toBoolean()
    }

    actual fun setLogReadaheadSize(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_log_readahead_size(native, value.asSizeT())
        return this
    }

    actual fun logReadaheadSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_log_readahead_size(native), "log_readahead_size")
    }

    actual fun setBestEffortsRecovery(value: Boolean): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_best_efforts_recovery(native, value.toUByte())
        return this
    }

    actual fun bestEffortsRecovery(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_best_efforts_recovery(native).toBoolean()
    }

    actual fun setMaxBgErrorResumeCount(value: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_max_bgerror_resume_count(native, value)
        return this
    }

    actual fun maxBgerrorResumeCount(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_bgerror_resume_count(native)
    }

    actual fun setBgerrorResumeRetryInterval(value: Long): DBOptions {
        checkOwningHandle()
        rocksdb_options_set_bgerror_resume_retry_interval(native, value.asUInt64())
        return this
    }

    actual fun bgerrorResumeRetryInterval(): Long {
        checkOwningHandle()
        return rocksdb_options_get_bgerror_resume_retry_interval(native).toCheckedLong("bgerror_resume_retry_interval")
    }

    actual fun setIncreaseParallelism(totalThreads: Int): DBOptions {
        checkOwningHandle()
        rocksdb_options_increase_parallelism(native, totalThreads)
        return this
    }

}
