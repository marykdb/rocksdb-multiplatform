package maryk.rocksdb

actual interface MutableDBOptionsInterface<T : MutableDBOptionsInterface<T>> {
    actual fun setMaxBackgroundJobs(maxBackgroundJobs: Int): T
    actual fun maxBackgroundJobs(): Int
    actual fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): T
    actual fun avoidFlushDuringShutdown(): Boolean
    actual fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): T
    actual fun writableFileMaxBufferSize(): Long
    actual fun setDelayedWriteRate(delayedWriteRate: Long): T
    actual fun delayedWriteRate(): Long
    actual fun setMaxTotalWalSize(maxTotalWalSize: Long): T
    actual fun maxTotalWalSize(): Long
    actual fun setDeleteObsoleteFilesPeriodMicros(micros: Long): T
    actual fun deleteObsoleteFilesPeriodMicros(): Long
    actual fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): T
    actual fun statsDumpPeriodSec(): Int
    actual fun setStatsPersistPeriodSec(statsPersistPeriodSec: Int): T
    actual fun statsPersistPeriodSec(): Int
    actual fun setStatsHistoryBufferSize(statsHistoryBufferSize: Long): T
    actual fun statsHistoryBufferSize(): Long
    actual fun setMaxOpenFiles(maxOpenFiles: Int): T
    actual fun maxOpenFiles(): Int
    actual fun setBytesPerSync(bytesPerSync: Long): T
    actual fun bytesPerSync(): Long
    actual fun setWalBytesPerSync(walBytesPerSync: Long): T
    actual fun walBytesPerSync(): Long
    actual fun setStrictBytesPerSync(strictBytesPerSync: Boolean): T
    actual fun strictBytesPerSync(): Boolean
    actual fun setCompactionReadaheadSize(compactionReadaheadSize: Long): T
    actual fun compactionReadaheadSize(): Long
    actual fun setDailyOffpeakTimeUTC(offpeakTimeUTC: String): T
    actual fun dailyOffpeakTimeUTC(): String
}
