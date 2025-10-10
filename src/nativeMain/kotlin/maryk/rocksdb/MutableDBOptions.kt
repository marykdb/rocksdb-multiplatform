package maryk.rocksdb

import cnames.structs.rocksdb_mutable_db_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import maryk.toBoolean
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
        rocksdb_mutable_db_options_set_max_background_jobs(native, maxBackgroundJobs)
        return this
    }

    actual override fun maxBackgroundJobs(): Int =
        rocksdb_mutable_db_options_get_max_background_jobs(native)

    actual override fun setAvoidFlushDuringShutdown(
        avoidFlushDuringShutdown: Boolean,
    ): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_avoid_flush_during_shutdown(native, avoidFlushDuringShutdown.toUByte())
        return this
    }

    actual override fun avoidFlushDuringShutdown(): Boolean =
        rocksdb_mutable_db_options_get_avoid_flush_during_shutdown(native).toBoolean()

    actual override fun setWritableFileMaxBufferSize(
        writableFileMaxBufferSize: Long,
    ): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_writable_file_max_buffer_size(native, writableFileMaxBufferSize.toULong())
        return this
    }

    actual override fun writableFileMaxBufferSize(): Long =
        rocksdb_mutable_db_options_get_writable_file_max_buffer_size(native).toLong()

    actual override fun setDelayedWriteRate(delayedWriteRate: Long): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_delayed_write_rate(native, delayedWriteRate.toULong())
        return this
    }

    actual override fun delayedWriteRate(): Long =
        rocksdb_mutable_db_options_get_delayed_write_rate(native).toLong()

    actual override fun setMaxTotalWalSize(maxTotalWalSize: Long): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_max_total_wal_size(native, maxTotalWalSize.toULong())
        return this
    }

    actual override fun maxTotalWalSize(): Long =
        rocksdb_mutable_db_options_get_max_total_wal_size(native).toLong()

    actual override fun setDeleteObsoleteFilesPeriodMicros(
        micros: Long,
    ): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_delete_obsolete_files_period_micros(native, micros.toULong())
        return this
    }

    actual override fun deleteObsoleteFilesPeriodMicros(): Long =
        rocksdb_mutable_db_options_get_delete_obsolete_files_period_micros(native).toLong()

    actual override fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_stats_dump_period_sec(native, statsDumpPeriodSec)
        return this
    }

    actual override fun statsDumpPeriodSec(): Int =
        rocksdb_mutable_db_options_get_stats_dump_period_sec(native)

    actual override fun setStatsPersistPeriodSec(statsPersistPeriodSec: Int): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_stats_persist_period_sec(native, statsPersistPeriodSec)
        return this
    }

    actual override fun statsPersistPeriodSec(): Int =
        rocksdb_mutable_db_options_get_stats_persist_period_sec(native)

    actual override fun setStatsHistoryBufferSize(statsHistoryBufferSize: Long): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_stats_history_buffer_size(native, statsHistoryBufferSize.toULong())
        return this
    }

    actual override fun statsHistoryBufferSize(): Long =
        rocksdb_mutable_db_options_get_stats_history_buffer_size(native).toLong()

    actual override fun setMaxOpenFiles(maxOpenFiles: Int): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_max_open_files(native, maxOpenFiles)
        return this
    }

    actual override fun maxOpenFiles(): Int =
        rocksdb_mutable_db_options_get_max_open_files(native)

    actual override fun setBytesPerSync(bytesPerSync: Long): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_bytes_per_sync(native, bytesPerSync.toULong())
        return this
    }

    actual override fun bytesPerSync(): Long =
        rocksdb_mutable_db_options_get_bytes_per_sync(native).toLong()

    actual override fun setWalBytesPerSync(walBytesPerSync: Long): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_wal_bytes_per_sync(native, walBytesPerSync.toULong())
        return this
    }

    actual override fun walBytesPerSync(): Long =
        rocksdb_mutable_db_options_get_wal_bytes_per_sync(native).toLong()

    actual override fun setStrictBytesPerSync(strictBytesPerSync: Boolean): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_strict_bytes_per_sync(native, strictBytesPerSync.toUByte())
        return this
    }

    actual override fun strictBytesPerSync(): Boolean =
        rocksdb_mutable_db_options_get_strict_bytes_per_sync(native).toBoolean()

    actual override fun setCompactionReadaheadSize(
        compactionReadaheadSize: Long,
    ): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_compaction_readahead_size(native, compactionReadaheadSize.toULong())
        return this
    }

    actual override fun compactionReadaheadSize(): Long =
        rocksdb_mutable_db_options_get_compaction_readahead_size(native).toLong()

    actual override fun setDailyOffpeakTimeUTC(offpeakTimeUTC: String): MutableDBOptionsBuilder {
        rocksdb_mutable_db_options_set_daily_offpeak_time_utc(native, offpeakTimeUTC)
        return this
    }

    actual override fun dailyOffpeakTimeUTC(): String =
        rocksdb_mutable_db_options_get_daily_offpeak_time_utc(native)?.toKString().orEmpty()

    actual fun build(): MutableDBOptions {
        val copy = rocksdb_mutable_db_options_clone(native)
            ?: error("Unable to clone mutable DB options")
        return MutableDBOptions(copy)
    }
}
