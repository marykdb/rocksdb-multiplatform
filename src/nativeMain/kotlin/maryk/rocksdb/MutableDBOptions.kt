package maryk.rocksdb

import cnames.structs.rocksdb_mutable_db_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import maryk.asUInt64
import maryk.toBoolean
import maryk.toCheckedLong
import maryk.toUByte
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_mutable_db_options_clone
import rocksdb.rocksdb_mutable_db_options_create
import rocksdb.rocksdb_mutable_db_options_create_from_string
import rocksdb.rocksdb_mutable_db_options_destroy
import rocksdb.rocksdb_mutable_db_options_get_avoid_flush_during_shutdown
import rocksdb.rocksdb_mutable_db_options_get_bytes_per_sync
import rocksdb.rocksdb_mutable_db_options_get_compaction_readahead_size
import rocksdb.rocksdb_mutable_db_options_get_daily_offpeak_time_utc
import rocksdb.rocksdb_mutable_db_options_get_delayed_write_rate
import rocksdb.rocksdb_mutable_db_options_get_delete_obsolete_files_period_micros
import rocksdb.rocksdb_mutable_db_options_get_max_background_compactions
import rocksdb.rocksdb_mutable_db_options_get_max_background_jobs
import rocksdb.rocksdb_mutable_db_options_get_max_open_files
import rocksdb.rocksdb_mutable_db_options_get_max_total_wal_size
import rocksdb.rocksdb_mutable_db_options_get_stats_dump_period_sec
import rocksdb.rocksdb_mutable_db_options_get_stats_history_buffer_size
import rocksdb.rocksdb_mutable_db_options_get_stats_persist_period_sec
import rocksdb.rocksdb_mutable_db_options_get_strict_bytes_per_sync
import rocksdb.rocksdb_mutable_db_options_get_wal_bytes_per_sync
import rocksdb.rocksdb_mutable_db_options_get_writable_file_max_buffer_size
import rocksdb.rocksdb_mutable_db_options_set_avoid_flush_during_shutdown
import rocksdb.rocksdb_mutable_db_options_set_bytes_per_sync
import rocksdb.rocksdb_mutable_db_options_set_compaction_readahead_size
import rocksdb.rocksdb_mutable_db_options_set_daily_offpeak_time_utc
import rocksdb.rocksdb_mutable_db_options_set_delayed_write_rate
import rocksdb.rocksdb_mutable_db_options_set_delete_obsolete_files_period_micros
import rocksdb.rocksdb_mutable_db_options_set_max_background_compactions
import rocksdb.rocksdb_mutable_db_options_set_max_background_jobs
import rocksdb.rocksdb_mutable_db_options_set_max_open_files
import rocksdb.rocksdb_mutable_db_options_set_max_total_wal_size
import rocksdb.rocksdb_mutable_db_options_set_stats_dump_period_sec
import rocksdb.rocksdb_mutable_db_options_set_stats_history_buffer_size
import rocksdb.rocksdb_mutable_db_options_set_stats_persist_period_sec
import rocksdb.rocksdb_mutable_db_options_set_strict_bytes_per_sync
import rocksdb.rocksdb_mutable_db_options_set_wal_bytes_per_sync
import rocksdb.rocksdb_mutable_db_options_set_writable_file_max_buffer_size

actual class MutableDBOptions internal constructor(
    internal val native: CPointer<rocksdb_mutable_db_options_t>,
) : AbstractMutableOptions() {
    protected override fun dispose() {
        rocksdb_mutable_db_options_destroy(native)
    }
}

actual fun mutableDBOptionsBuilder(): MutableDBOptionsBuilder =
    MutableDBOptionsBuilder(requireNotNull(rocksdb_mutable_db_options_create()) {
        "Unable to allocate mutable DB options"
    })

actual fun parseMutableDBOptions(
    str: String,
    ignoreUnknown: Boolean,
): MutableDBOptionsBuilder {
    val native = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_mutable_db_options_create_from_string(str, ignoreUnknown.toUByte(), error)
    } ?: error("Unable to parse mutable DB options")
    return MutableDBOptionsBuilder(native)
}

actual class MutableDBOptionsBuilder internal constructor(
    internal val native: CPointer<rocksdb_mutable_db_options_t>,
) : AbstractMutableOptions(), MutableDBOptionsInterface<MutableDBOptionsBuilder> {

    protected override fun dispose() {
        rocksdb_mutable_db_options_destroy(native)
    }

    actual override fun setMaxBackgroundJobs(maxBackgroundJobs: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_max_background_jobs(native, maxBackgroundJobs)
        return this
    }

    actual override fun maxBackgroundJobs(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_max_background_jobs(native)
    }

    actual override fun setAvoidFlushDuringShutdown(
        avoidFlushDuringShutdown: Boolean,
    ): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_avoid_flush_during_shutdown(native, avoidFlushDuringShutdown.toUByte())
        return this
    }

    actual override fun avoidFlushDuringShutdown(): Boolean {
        checkOpen()
        return rocksdb_mutable_db_options_get_avoid_flush_during_shutdown(native).toBoolean()
    }

    actual override fun setWritableFileMaxBufferSize(
        writableFileMaxBufferSize: Long,
    ): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_writable_file_max_buffer_size(native, writableFileMaxBufferSize.asUInt64())
        return this
    }

    actual override fun writableFileMaxBufferSize(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_writable_file_max_buffer_size(native)
            .toCheckedLong("mutable DB options writable file max buffer size")
    }

    actual override fun setDelayedWriteRate(delayedWriteRate: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_delayed_write_rate(native, delayedWriteRate.asUInt64())
        return this
    }

    actual override fun delayedWriteRate(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_delayed_write_rate(native)
            .toCheckedLong("mutable DB options delayed write rate")
    }

    actual override fun setMaxTotalWalSize(maxTotalWalSize: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_max_total_wal_size(native, maxTotalWalSize.asUInt64())
        return this
    }

    actual override fun maxTotalWalSize(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_max_total_wal_size(native)
            .toCheckedLong("mutable DB options max total WAL size")
    }

    actual override fun setDeleteObsoleteFilesPeriodMicros(
        micros: Long,
    ): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_delete_obsolete_files_period_micros(native, micros.asUInt64())
        return this
    }

    actual override fun deleteObsoleteFilesPeriodMicros(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_delete_obsolete_files_period_micros(native)
            .toCheckedLong("mutable DB options delete obsolete files period micros")
    }

    actual override fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_stats_dump_period_sec(native, statsDumpPeriodSec)
        return this
    }

    actual override fun statsDumpPeriodSec(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_stats_dump_period_sec(native)
    }

    actual override fun setStatsPersistPeriodSec(statsPersistPeriodSec: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_stats_persist_period_sec(native, statsPersistPeriodSec)
        return this
    }

    actual override fun statsPersistPeriodSec(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_stats_persist_period_sec(native)
    }

    actual override fun setStatsHistoryBufferSize(statsHistoryBufferSize: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_stats_history_buffer_size(native, statsHistoryBufferSize.asUInt64())
        return this
    }

    actual override fun statsHistoryBufferSize(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_stats_history_buffer_size(native)
            .toCheckedLong("mutable DB options stats history buffer size")
    }

    actual override fun setMaxOpenFiles(maxOpenFiles: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_max_open_files(native, maxOpenFiles)
        return this
    }

    actual override fun maxOpenFiles(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_max_open_files(native)
    }

    actual override fun setBytesPerSync(bytesPerSync: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_bytes_per_sync(native, bytesPerSync.asUInt64())
        return this
    }

    actual override fun bytesPerSync(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_bytes_per_sync(native)
            .toCheckedLong("mutable DB options bytes per sync")
    }

    actual override fun setWalBytesPerSync(walBytesPerSync: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_wal_bytes_per_sync(native, walBytesPerSync.asUInt64())
        return this
    }

    actual override fun walBytesPerSync(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_wal_bytes_per_sync(native)
            .toCheckedLong("mutable DB options WAL bytes per sync")
    }

    actual override fun setStrictBytesPerSync(strictBytesPerSync: Boolean): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_strict_bytes_per_sync(native, strictBytesPerSync.toUByte())
        return this
    }

    actual override fun strictBytesPerSync(): Boolean {
        checkOpen()
        return rocksdb_mutable_db_options_get_strict_bytes_per_sync(native).toBoolean()
    }

    actual override fun setCompactionReadaheadSize(
        compactionReadaheadSize: Long,
    ): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_compaction_readahead_size(native, compactionReadaheadSize.asUInt64())
        return this
    }

    actual override fun compactionReadaheadSize(): Long {
        checkOpen()
        return rocksdb_mutable_db_options_get_compaction_readahead_size(native)
            .toCheckedLong("mutable DB options compaction readahead size")
    }

    actual override fun setDailyOffpeakTimeUTC(offpeakTimeUTC: String): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_daily_offpeak_time_utc(native, offpeakTimeUTC)
        return this
    }

    actual override fun dailyOffpeakTimeUTC(): String {
        checkOpen()
        return rocksdb_mutable_db_options_get_daily_offpeak_time_utc(native)?.toKString().orEmpty()
    }

    actual fun build(): MutableDBOptions {
        checkOpen()
        val copy = rocksdb_mutable_db_options_clone(native)
            ?: error("Unable to clone mutable DB options")
        return MutableDBOptions(copy)
    }
}
