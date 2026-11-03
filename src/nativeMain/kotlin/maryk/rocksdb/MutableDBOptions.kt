package maryk.rocksdb

import rocksdb.rocksdb_mutable_db_options_get_max_compaction_trigger_wakeup_seconds
import rocksdb.rocksdb_mutable_db_options_set_max_compaction_trigger_wakeup_seconds

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
    private val explicitValues: Map<String, String>,
) : AbstractMutableOptions() {
    internal fun valuesForApply(): Map<String, String> {
        checkOpen()
        return explicitValues
    }

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
    require('\u0000' !in str) { "Option strings must not contain NUL" }
    val native = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_mutable_db_options_create_from_string(str, ignoreUnknown.toUByte(), error)
    } ?: error("Unable to parse mutable DB options")
    return try {
        val values = parsedLiveOptionValues(str, ignoreUnknown) { entry ->
            val parsed = Unit.wrapWithNullErrorThrower { error ->
                rocksdb_mutable_db_options_create_from_string(entry, 0u, error)
            } ?: error("Unable to parse mutable options entry")
            rocksdb_mutable_db_options_destroy(parsed)
        }
        MutableDBOptionsBuilder(native, values.toMutableMap())
    } catch (error: Throwable) {
        rocksdb_mutable_db_options_destroy(native)
        throw error
    }
}

actual class MutableDBOptionsBuilder internal constructor(
    internal val native: CPointer<rocksdb_mutable_db_options_t>,
    private val explicitValues: MutableMap<String, String> = linkedMapOf(),
) : AbstractMutableOptions(), MutableDBOptionsInterface<MutableDBOptionsBuilder> {

    protected override fun dispose() {
        rocksdb_mutable_db_options_destroy(native)
    }

    actual override fun setMaxBackgroundJobs(maxBackgroundJobs: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_max_background_jobs(native, maxBackgroundJobs)
        explicitValues["max_background_jobs"] = maxBackgroundJobs.toString()
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
        explicitValues["avoid_flush_during_shutdown"] = avoidFlushDuringShutdown.toString()
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
        explicitValues["writable_file_max_buffer_size"] = writableFileMaxBufferSize.toString()
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
        explicitValues["delayed_write_rate"] = delayedWriteRate.toString()
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
        explicitValues["max_total_wal_size"] = maxTotalWalSize.toString()
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
        explicitValues["delete_obsolete_files_period_micros"] = micros.toString()
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
        explicitValues["stats_dump_period_sec"] = statsDumpPeriodSec.toString()
        return this
    }

    actual override fun statsDumpPeriodSec(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_stats_dump_period_sec(native)
    }

    actual override fun setStatsPersistPeriodSec(statsPersistPeriodSec: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_stats_persist_period_sec(native, statsPersistPeriodSec)
        explicitValues["stats_persist_period_sec"] = statsPersistPeriodSec.toString()
        return this
    }

    actual override fun statsPersistPeriodSec(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_stats_persist_period_sec(native)
    }

    actual override fun setStatsHistoryBufferSize(statsHistoryBufferSize: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_stats_history_buffer_size(native, statsHistoryBufferSize.asUInt64())
        explicitValues["stats_history_buffer_size"] = statsHistoryBufferSize.toString()
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
        explicitValues["max_open_files"] = maxOpenFiles.toString()
        return this
    }

    actual override fun maxOpenFiles(): Int {
        checkOpen()
        return rocksdb_mutable_db_options_get_max_open_files(native)
    }

    actual override fun setBytesPerSync(bytesPerSync: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_bytes_per_sync(native, bytesPerSync.asUInt64())
        explicitValues["bytes_per_sync"] = bytesPerSync.toString()
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
        explicitValues["wal_bytes_per_sync"] = walBytesPerSync.toString()
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
        explicitValues["strict_bytes_per_sync"] = strictBytesPerSync.toString()
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
        explicitValues["compaction_readahead_size"] = compactionReadaheadSize.toString()
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
        explicitValues["daily_offpeak_time_utc"] = offpeakTimeUTC.toString()
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
        return MutableDBOptions(copy, explicitValues.toMap())
    }

    actual override fun setMaxBackgroundCompactions(value: Int): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_max_background_compactions(native, value)
        explicitValues["max_background_compactions"] = value.toString()
        return this
    }

    actual override fun maxBackgroundCompactions(): Int {
        checkOpen()
        if ("max_background_compactions" !in explicitValues) throw NoSuchElementException("max_background_compactions has not been set")
        return rocksdb_mutable_db_options_get_max_background_compactions(native)
    }

    actual override fun setMaxCompactionTriggerWakeupSeconds(value: Long): MutableDBOptionsBuilder {
        checkOpen()
        rocksdb_mutable_db_options_set_max_compaction_trigger_wakeup_seconds(native, value.asUInt64())
        explicitValues["max_compaction_trigger_wakeup_seconds"] = value.toString()
        return this
    }

    actual override fun maxCompactionTriggerWakeupSeconds(): Long {
        checkOpen()
        if ("max_compaction_trigger_wakeup_seconds" !in explicitValues) throw NoSuchElementException("max_compaction_trigger_wakeup_seconds has not been set")
        return rocksdb_mutable_db_options_get_max_compaction_trigger_wakeup_seconds(native).toCheckedLong("max_compaction_trigger_wakeup_seconds")
    }

}
