package maryk.rocksdb

/** Mutable DB options shared between options classes. */
expect interface MutableDBOptionsInterface<T : MutableDBOptionsInterface<T>> {
    fun setMaxBackgroundJobs(maxBackgroundJobs: Int): T
    fun maxBackgroundJobs(): Int

    @Deprecated("Use setMaxBackgroundJobs instead")
    fun setMaxBackgroundCompactions(maxBackgroundCompactions: Int): T

    @Deprecated("Use maxBackgroundJobs instead")
    fun maxBackgroundCompactions(): Int

    fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): T
    fun avoidFlushDuringShutdown(): Boolean

    fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): T
    fun writableFileMaxBufferSize(): Long

    fun setDelayedWriteRate(delayedWriteRate: Long): T
    fun delayedWriteRate(): Long

    fun setMaxTotalWalSize(maxTotalWalSize: Long): T
    fun maxTotalWalSize(): Long

    fun setDeleteObsoleteFilesPeriodMicros(micros: Long): T
    fun deleteObsoleteFilesPeriodMicros(): Long

    fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): T
    fun statsDumpPeriodSec(): Int

    fun setStatsPersistPeriodSec(statsPersistPeriodSec: Int): T
    fun statsPersistPeriodSec(): Int

    fun setStatsHistoryBufferSize(statsHistoryBufferSize: Long): T
    fun statsHistoryBufferSize(): Long

    fun setMaxOpenFiles(maxOpenFiles: Int): T
    fun maxOpenFiles(): Int

    fun setBytesPerSync(bytesPerSync: Long): T
    fun bytesPerSync(): Long

    fun setWalBytesPerSync(walBytesPerSync: Long): T
    fun walBytesPerSync(): Long

    fun setStrictBytesPerSync(strictBytesPerSync: Boolean): T
    fun strictBytesPerSync(): Boolean

    fun setCompactionReadaheadSize(compactionReadaheadSize: Long): T
    fun compactionReadaheadSize(): Long

    fun setDailyOffpeakTimeUTC(offpeakTimeUTC: String): T
    fun dailyOffpeakTimeUTC(): String
}
