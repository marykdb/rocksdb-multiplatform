@file:OptIn(ExperimentalNativeApi::class, UnsafeNumber::class)

package maryk.rocksdb

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
    private var env: Env? = null
    private var tableFormatConfig: TableFormatConfig? = null
    private var ownedComparator: AbstractComparator? = null
    private var sstFileManager: SstFileManager? = null

    actual constructor() : this(requireNotNull(rocksdb_options_create()) { "Unable to allocate RocksDB options" })

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

    internal fun retainedNativeReferences(): List<Any> =
        listOfNotNull(env, statistics, sstFileManager)

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
}
