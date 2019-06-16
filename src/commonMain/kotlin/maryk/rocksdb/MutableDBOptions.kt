package maryk.rocksdb

expect class MutableDBOptions : AbstractMutableOptions

expect class MutableDBOptionsBuilder {
    fun build(): MutableDBOptions

    /**
     * Specifies the maximum number of concurrent background jobs (both flushes
     * and compactions combined).
     * Default: 2
     *
     * @param maxBackgroundJobs number of max concurrent background jobs
     * @return the instance of the current object.
     */
    fun setMaxBackgroundJobs(maxBackgroundJobs: Int): MutableDBOptionsBuilder

    /**
     * Returns the maximum number of concurrent background jobs (both flushes
     * and compactions combined).
     * Default: 2
     *
     * @return the maximum number of concurrent background jobs.
     */
    fun maxBackgroundJobs(): Int

    /**
     * Suggested number of concurrent background compaction jobs, submitted to
     * the default LOW priority thread pool.
     * Default: 1
     *
     * @param baseBackgroundCompactions Suggested number of background compaction
     * jobs
     *
     */
    @Deprecated("Use {@link #setMaxBackgroundJobs(int)}")
    fun setBaseBackgroundCompactions(baseBackgroundCompactions: Int)

    /**
     * Suggested number of concurrent background compaction jobs, submitted to
     * the default LOW priority thread pool.
     * Default: 1
     *
     * @return Suggested number of background compaction jobs
     */
    fun baseBackgroundCompactions(): Int

    /**
     * Specifies the maximum number of concurrent background compaction jobs,
     * submitted to the default LOW priority thread pool.
     * If you're increasing this, also consider increasing number of threads in
     * LOW priority thread pool. For more information, see
     * Default: 1
     *
     * @param maxBackgroundCompactions the maximum number of background
     * compaction jobs.
     * @return the instance of the current object.
     *
     * @see RocksEnv.setBackgroundThreads
     * @see RocksEnv.setBackgroundThreads
     * @see DBOptionsInterface.maxBackgroundFlushes
     */
    fun setMaxBackgroundCompactions(maxBackgroundCompactions: Int): MutableDBOptionsBuilder

    /**
     * Returns the maximum number of concurrent background compaction jobs,
     * submitted to the default LOW priority thread pool.
     * When increasing this number, we may also want to consider increasing
     * number of threads in LOW priority thread pool.
     * Default: 1
     *
     * @return the maximum number of concurrent background compaction jobs.
     * @see RocksEnv.setBackgroundThreads
     * @see RocksEnv.setBackgroundThreads
     */
    @Deprecated("Use {@link #setMaxBackgroundJobs(int)}")
    fun maxBackgroundCompactions(): Int

    /**
     * By default RocksDB will flush all memtables on DB close if there are
     * unpersisted data (i.e. with WAL disabled) The flush can be skip to speedup
     * DB close. Unpersisted data WILL BE LOST.
     *
     * DEFAULT: false
     *
     * Dynamically changeable through
     * [RocksDB.setOptions]
     * API.
     *
     * @param avoidFlushDuringShutdown true if we should avoid flush during
     * shutdown
     *
     * @return the reference to the current options.
     */
    fun setAvoidFlushDuringShutdown(avoidFlushDuringShutdown: Boolean): MutableDBOptionsBuilder

    /**
     * By default RocksDB will flush all memtables on DB close if there are
     * unpersisted data (i.e. with WAL disabled) The flush can be skip to speedup
     * DB close. Unpersisted data WILL BE LOST.
     *
     * DEFAULT: false
     *
     * Dynamically changeable through
     * [RocksDB.setOptions]
     * API.
     *
     * @return true if we should avoid flush during shutdown
     */
    fun avoidFlushDuringShutdown(): Boolean

    /**
     * This is the maximum buffer size that is used by WritableFileWriter.
     * On Windows, we need to maintain an aligned buffer for writes.
     * We allow the buffer to grow until it's size hits the limit.
     *
     * Default: 1024 * 1024 (1 MB)
     *
     * @param writableFileMaxBufferSize the maximum buffer size
     *
     * @return the reference to the current options.
     */
    fun setWritableFileMaxBufferSize(writableFileMaxBufferSize: Long): MutableDBOptionsBuilder

    /**
     * This is the maximum buffer size that is used by WritableFileWriter.
     * On Windows, we need to maintain an aligned buffer for writes.
     * We allow the buffer to grow until it's size hits the limit.
     *
     * Default: 1024 * 1024 (1 MB)
     *
     * @return the maximum buffer size
     */
    fun writableFileMaxBufferSize(): Long

    /**
     * The limited write rate to DB if
     * [ColumnFamilyOptions.softPendingCompactionBytesLimit] or
     * [ColumnFamilyOptions.level0SlowdownWritesTrigger] is triggered,
     * or we are writing to the last mem table allowed and we allow more than 3
     * mem tables. It is calculated using size of user write requests before
     * compression. RocksDB may decide to slow down more if the compaction still
     * gets behind further.
     *
     * Unit: bytes per second.
     *
     * Default: 16MB/s
     *
     * @param delayedWriteRate the rate in bytes per second
     *
     * @return the reference to the current options.
     */
    fun setDelayedWriteRate(delayedWriteRate: Long): MutableDBOptionsBuilder

    /**
     * The limited write rate to DB if
     * [ColumnFamilyOptions.softPendingCompactionBytesLimit] or
     * [ColumnFamilyOptions.level0SlowdownWritesTrigger] is triggered,
     * or we are writing to the last mem table allowed and we allow more than 3
     * mem tables. It is calculated using size of user write requests before
     * compression. RocksDB may decide to slow down more if the compaction still
     * gets behind further.
     *
     * Unit: bytes per second.
     *
     * Default: 16MB/s
     *
     * @return the rate in bytes per second
     */
    fun delayedWriteRate(): Long

    /**
     *
     * Once write-ahead logs exceed this size, we will start forcing the
     * flush of column families whose memtables are backed by the oldest live
     * WAL file (i.e. the ones that are causing all the space amplification).
     *
     *
     * If set to 0 (default), we will dynamically choose the WAL size limit to
     * be [sum of all write_buffer_size * max_write_buffer_number] * 2
     *
     * This option takes effect only when there are more than one column family as
     * otherwise the wal size is dictated by the write_buffer_size.
     *
     * Default: 0
     *
     * @param maxTotalWalSize max total wal size.
     * @return the instance of the current object.
     */
    fun setMaxTotalWalSize(maxTotalWalSize: Long): MutableDBOptionsBuilder

    /**
     *
     * Returns the max total wal size. Once write-ahead logs exceed this size,
     * we will start forcing the flush of column families whose memtables are
     * backed by the oldest live WAL file (i.e. the ones that are causing all
     * the space amplification).
     *
     *
     * If set to 0 (default), we will dynamically choose the WAL size limit
     * to be [sum of all write_buffer_size * max_write_buffer_number] * 2
     *
     *
     * @return max total wal size
     */
    fun maxTotalWalSize(): Long

    /**
     * The periodicity when obsolete files get deleted. The default
     * value is 6 hours. The files that get out of scope by compaction
     * process will still get automatically delete on every compaction,
     * regardless of this setting
     *
     * @param micros the time interval in micros
     * @return the instance of the current object.
     */
    fun setDeleteObsoleteFilesPeriodMicros(micros: Long): MutableDBOptionsBuilder

    /**
     * The periodicity when obsolete files get deleted. The default
     * value is 6 hours. The files that get out of scope by compaction
     * process will still get automatically delete on every compaction,
     * regardless of this setting
     *
     * @return the time interval in micros when obsolete files will be deleted.
     */
    fun deleteObsoleteFilesPeriodMicros(): Long

    /**
     * if not zero, dump rocksdb.stats to LOG every stats_dump_period_sec
     * Default: 600 (10 minutes)
     *
     * @param statsDumpPeriodSec time interval in seconds.
     * @return the instance of the current object.
     */
    fun setStatsDumpPeriodSec(statsDumpPeriodSec: Int): MutableDBOptionsBuilder

    /**
     * If not zero, dump rocksdb.stats to LOG every stats_dump_period_sec
     * Default: 600 (10 minutes)
     *
     * @return time interval in seconds.
     */
    fun statsDumpPeriodSec(): Int

    /**
     * Number of open files that can be used by the DB.  You may need to
     * increase this if your database has a large working set. Value -1 means
     * files opened are always kept open. You can estimate number of files based
     * on `target_file_size_base` and `target_file_size_multiplier`
     * for level-based compaction. For universal-style compaction, you can usually
     * set it to -1.
     * Default: 5000
     *
     * @param maxOpenFiles the maximum number of open files.
     * @return the instance of the current object.
     */
    fun setMaxOpenFiles(maxOpenFiles: Int): MutableDBOptionsBuilder

    /**
     * Number of open files that can be used by the DB.  You may need to
     * increase this if your database has a large working set. Value -1 means
     * files opened are always kept open. You can estimate number of files based
     * on `target_file_size_base` and `target_file_size_multiplier`
     * for level-based compaction. For universal-style compaction, you can usually
     * set it to -1.
     *
     * @return the maximum number of open files.
     */
    fun maxOpenFiles(): Int

    /**
     * Allows OS to incrementally sync files to disk while they are being
     * written, asynchronously, in the background.
     * Issue one request for every bytes_per_sync written. 0 turns it off.
     * Default: 0
     *
     * @param bytesPerSync size in bytes
     * @return the instance of the current object.
     */
    fun setBytesPerSync(bytesPerSync: Long): MutableDBOptionsBuilder

    /**
     * Allows OS to incrementally sync files to disk while they are being
     * written, asynchronously, in the background.
     * Issue one request for every bytes_per_sync written. 0 turns it off.
     * Default: 0
     *
     * @return size in bytes
     */
    fun bytesPerSync(): Long

    /**
     * Same as [.setBytesPerSync] , but applies to WAL files
     *
     * Default: 0, turned off
     *
     * @param walBytesPerSync size in bytes
     * @return the instance of the current object.
     */
    fun setWalBytesPerSync(walBytesPerSync: Long): MutableDBOptionsBuilder

    /**
     * Same as [.bytesPerSync] , but applies to WAL files
     *
     * Default: 0, turned off
     *
     * @return size in bytes
     */
    fun walBytesPerSync(): Long


    /**
     * If non-zero, we perform bigger reads when doing compaction. If you're
     * running RocksDB on spinning disks, you should set this to at least 2MB.
     *
     * That way RocksDB's compaction is doing sequential instead of random reads.
     * When non-zero, we also force
     * [DBOptionsInterface.newTableReaderForCompactionInputs] to true.
     *
     * Default: 0
     *
     * @param compactionReadaheadSize The compaction read-ahead size
     *
     * @return the reference to the current options.
     */
    fun setCompactionReadaheadSize(compactionReadaheadSize: Long): MutableDBOptionsBuilder

    /**
     * If non-zero, we perform bigger reads when doing compaction. If you're
     * running RocksDB on spinning disks, you should set this to at least 2MB.
     *
     * That way RocksDB's compaction is doing sequential instead of random reads.
     * When non-zero, we also force
     * [DBOptionsInterface.newTableReaderForCompactionInputs] to true.
     *
     * Default: 0
     *
     * @return The compaction read-ahead size
     */
    fun compactionReadaheadSize(): Long
}

expect enum class DBOption {
    max_background_jobs,
    base_background_compactions,
    max_background_compactions,
    avoid_flush_during_shutdown,
    writable_file_max_buffer_size,
    delayed_write_rate,
    max_total_wal_size,
    delete_obsolete_files_period_micros,
    stats_dump_period_sec,
    max_open_files,
    bytes_per_sync,
    wal_bytes_per_sync,
    compaction_readahead_size;

    fun getValueType(): MutableOptionKeyValueType
}

/**
 * Creates a builder which allows you
 * to set MutableDBOptions in a fluent
 * manner
 *
 * @return A builder for MutableDBOptions
 */
expect fun mutableDBOptionsBuilder(): MutableDBOptionsBuilder

/**
 * Parses a String representation of MutableDBOptions
 * The format is: key1=value1;key2=value2;key3=value3 etc
 *
 * For int[] values, each int should be separated by a comma, e.g.
 * key1=value1;intArrayKey1=1,2,3
 *
 * @param str The string representation of the mutable db options
 * @return A builder for the mutable db options
 */
expect fun parseMutableDBOptionsBuilder(str: String): MutableDBOptionsBuilder
