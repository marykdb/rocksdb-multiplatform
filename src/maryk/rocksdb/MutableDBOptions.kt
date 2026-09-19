package maryk.rocksdb

/** Mutable DB options entry point. */
expect class MutableDBOptions : AbstractMutableOptions

expect fun mutableDBOptionsBuilder(): MutableDBOptionsBuilder

expect fun parseMutableDBOptions(
    str: String,
    ignoreUnknown: Boolean = false
): MutableDBOptionsBuilder

expect class MutableDBOptionsBuilder : MutableDBOptionsInterface<MutableDBOptionsBuilder> {
    fun build(): MutableDBOptions
    override fun setMaxBackgroundJobs(maxBackgroundJobs: Int): MutableDBOptionsBuilder
    override fun maxBackgroundJobs(): Int
    override fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): MutableDBOptionsBuilder
    override fun avoidFlushDuringShutdown(): Boolean
    override fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): MutableDBOptionsBuilder
    override fun writableFileMaxBufferSize(): Long
    override fun setDelayedWriteRate(delayedWriteRate: Long): MutableDBOptionsBuilder
    override fun delayedWriteRate(): Long
    override fun setMaxTotalWalSize(maxTotalWalSize: Long): MutableDBOptionsBuilder
    override fun maxTotalWalSize(): Long
    override fun setDeleteObsoleteFilesPeriodMicros(micros: Long): MutableDBOptionsBuilder
    override fun deleteObsoleteFilesPeriodMicros(): Long
    override fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): MutableDBOptionsBuilder
    override fun statsDumpPeriodSec(): Int
    override fun setStatsPersistPeriodSec(statsPersistPeriodSec: Int): MutableDBOptionsBuilder
    override fun statsPersistPeriodSec(): Int
    override fun setStatsHistoryBufferSize(statsHistoryBufferSize: Long): MutableDBOptionsBuilder
    override fun statsHistoryBufferSize(): Long
    override fun setMaxOpenFiles(maxOpenFiles: Int): MutableDBOptionsBuilder
    override fun maxOpenFiles(): Int
    override fun setBytesPerSync(bytesPerSync: Long): MutableDBOptionsBuilder
    override fun bytesPerSync(): Long
    override fun setWalBytesPerSync(walBytesPerSync: Long): MutableDBOptionsBuilder
    override fun walBytesPerSync(): Long
    override fun setStrictBytesPerSync(strictBytesPerSync: Boolean): MutableDBOptionsBuilder
    override fun strictBytesPerSync(): Boolean
    override fun setCompactionReadaheadSize(compactionReadaheadSize: Long): MutableDBOptionsBuilder
    override fun compactionReadaheadSize(): Long
    override fun setDailyOffpeakTimeUTC(offpeakTimeUTC: String): MutableDBOptionsBuilder
    override fun dailyOffpeakTimeUTC(): String
}
