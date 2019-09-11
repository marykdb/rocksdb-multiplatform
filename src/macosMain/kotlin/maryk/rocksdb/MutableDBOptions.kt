package maryk.rocksdb

import maryk.rocksdb.MutableOptionKeyValueType.BOOLEAN
import maryk.rocksdb.MutableOptionKeyValueType.INT
import maryk.rocksdb.MutableOptionKeyValueType.LONG

actual class MutableDBOptions : AbstractMutableOptions()
actual class MutableDBOptionsBuilder {
    actual fun build(): MutableDBOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBackgroundJobs(maxBackgroundJobs: Int): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBackgroundJobs(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBaseBackgroundCompactions(baseBackgroundCompactions: Int) {
    }

    actual fun baseBackgroundCompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBackgroundCompactions(maxBackgroundCompactions: Int): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBackgroundCompactions(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun avoidFlushDuringShutdown(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writableFileMaxBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDelayedWriteRate(delayedWriteRate: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun delayedWriteRate(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxTotalWalSize(maxTotalWalSize: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxTotalWalSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeleteObsoleteFilesPeriodMicros(micros: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun deleteObsoleteFilesPeriodMicros(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun statsDumpPeriodSec(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxOpenFiles(maxOpenFiles: Int): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxOpenFiles(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setBytesPerSync(bytesPerSync: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWalBytesPerSync(walBytesPerSync: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun walBytesPerSync(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompactionReadaheadSize(compactionReadaheadSize: Long): MutableDBOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compactionReadaheadSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

actual enum class DBOption(
    private val valueType: MutableOptionKeyValueType
) {
    max_background_jobs(INT),
    base_background_compactions(INT),
    max_background_compactions(INT),
    avoid_flush_during_shutdown(BOOLEAN),
    writable_file_max_buffer_size(LONG),
    delayed_write_rate(LONG),
    max_total_wal_size(LONG),
    delete_obsolete_files_period_micros(LONG),
    stats_dump_period_sec(INT),
    max_open_files(INT),
    bytes_per_sync(LONG),
    wal_bytes_per_sync(LONG),
    compaction_readahead_size(LONG);

    actual fun getValueType() = valueType
}

actual fun mutableDBOptionsBuilder(): MutableDBOptionsBuilder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun parseMutableDBOptionsBuilder(str: String): MutableDBOptionsBuilder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
