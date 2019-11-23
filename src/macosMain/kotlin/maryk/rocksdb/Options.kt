package maryk.rocksdb

import rocksdb.RocksDBComparator
import rocksdb.RocksDBOptions
import rocksdb.RocksDBPrefixExtractor
import rocksdb.RocksDBPrefixType.RocksDBPrefixFixedLength
import rocksdb.bloomLocality
import rocksdb.bytesPerSync
import rocksdb.compactionStyle
import rocksdb.compressionType
import rocksdb.createIfMissing
import rocksdb.createMissingColumnFamilies
import rocksdb.disableAutoCompactions
import rocksdb.env
import rocksdb.errorIfExists
import rocksdb.infoLogLevel
import rocksdb.keepLogFileNum
import rocksdb.level0FileNumCompactionTrigger
import rocksdb.level0SlowdownWritesTrigger
import rocksdb.level0StopWritesTrigger
import rocksdb.logFileTimeToRoll
import rocksdb.maxBytesForLevelBase
import rocksdb.maxBytesForLevelMultiplier
import rocksdb.maxLogFileSize
import rocksdb.maxOpenFiles
import rocksdb.maxWriteAheadLogSize
import rocksdb.maxWriteBufferNumber
import rocksdb.minWriteBufferNumberToMerge
import rocksdb.numLevels
import rocksdb.paranoidChecks
import rocksdb.setBytesPerSync
import rocksdb.setComparator
import rocksdb.setCompressionType
import rocksdb.setCreateMissingColumnFamilies
import rocksdb.setDisableAutoCompactions
import rocksdb.setEnv
import rocksdb.setErrorIfExists
import rocksdb.setMaxOpenFiles
import rocksdb.setMaxWriteAheadLogSize
import rocksdb.setParanoidChecks
import rocksdb.setPrefixExtractor
import rocksdb.statistics
import rocksdb.targetFileSizeBase
import rocksdb.targetFileSizeMultiplier
import rocksdb.useFSync
import rocksdb.writeBufferSize

actual class Options private constructor(val native: RocksDBOptions) : RocksObject() {
    private var statistics: Statistics? = null

    actual constructor() : this(RocksDBOptions())

    actual fun setMaxOpenFiles(maxOpenFiles: Int): Options {
        native.setMaxOpenFiles(maxOpenFiles)
        return this
    }

    actual fun maxOpenFiles() = native.maxOpenFiles()

    actual fun setBytesPerSync(bytesPerSync: Long): Options {
        native.setBytesPerSync(bytesPerSync.toULong())
        return this
    }

    actual fun bytesPerSync() = native.bytesPerSync().toLong()

    actual fun setCreateIfMissing(flag: Boolean): Options {
        native.createIfMissing = flag
        return this
    }

    actual fun maxWriteBufferNumber(): Int {
        return native.maxWriteBufferNumber
    }

    actual fun minWriteBufferNumberToMerge(): Int {
        return native.minWriteBufferNumberToMerge
    }

    actual fun setBloomLocality(bloomLocality: Int): Options {
        native.bloomLocality = bloomLocality.toUInt()
        return this
    }

    actual fun bloomLocality() = native.bloomLocality.toInt()

    actual fun setNumLevels(numLevels: Int): Options {
        native.numLevels = numLevels
        return this
    }

    actual fun numLevels() = native.numLevels

    actual fun setCompactionStyle(compactionStyle: CompactionStyle): Options {
        native.compactionStyle = compactionStyle.value
        return this
    }

    actual fun compactionStyle(): CompactionStyle {
        return getCompactionStyle(native.compactionStyle)
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): Options {
        native.writeBufferSize = writeBufferSize.toULong()
        return this
    }

    actual fun writeBufferSize(): Long {
        return native.writeBufferSize.toLong()
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): Options {
        native.setDisableAutoCompactions(disableAutoCompactions)
        return this
    }

    actual fun disableAutoCompactions(): Boolean = native.disableAutoCompactions

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): Options {
        native.level0FileNumCompactionTrigger = level0FileNumCompactionTrigger
        return this
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        return native.level0FileNumCompactionTrigger
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): Options {
        native.maxBytesForLevelBase = maxBytesForLevelBase.toULong()
        return this
    }

    actual fun maxBytesForLevelBase(): Long {
        return native.maxBytesForLevelBase.toLong()
    }

    actual fun setCompressionType(compressionType: CompressionType): Options {
        native.setCompressionType(compressionType.value)
        return this
    }

    actual fun compressionType() = getCompressionType(native.compressionType)

    actual fun setComparator(builtinComparator: BuiltinComparator): Options {
        native.setComparator(RocksDBComparator.comparatorWithType(builtinComparator.native))
        return this
    }

    actual fun setComparator(comparator: AbstractComparator<out AbstractSlice<*>>): Options {
        native.setComparator(comparator.native)
        return this
    }

    actual fun useFixedLengthPrefixExtractor(n: Int): Options {
        native.setPrefixExtractor(
            RocksDBPrefixExtractor.prefixExtractorWithType(RocksDBPrefixFixedLength,  n.toULong())
        )
        return this
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): Options {
        native.maxBytesForLevelMultiplier = multiplier
        return this
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        return native.maxBytesForLevelMultiplier
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): Options {
        native.level0SlowdownWritesTrigger = level0SlowdownWritesTrigger
        return this
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        return native.level0SlowdownWritesTrigger
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): Options {
        native.level0StopWritesTrigger = level0StopWritesTrigger
        return this
    }

    actual fun level0StopWritesTrigger(): Int {
        return native.level0StopWritesTrigger
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): Options {
        native.targetFileSizeBase = targetFileSizeBase.toULong()
        return this
    }

    actual fun targetFileSizeBase(): Long {
        return native.targetFileSizeBase.toLong()
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): Options {
        native.targetFileSizeMultiplier = multiplier
        return this
    }

    actual fun targetFileSizeMultiplier(): Int {
        return native.targetFileSizeMultiplier
    }

    actual fun createIfMissing(): Boolean {
        return native.createIfMissing
    }

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): Options {
        native.maxWriteBufferNumber = maxWriteBufferNumber
        return this
    }

    actual fun setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge: Int): Options {
        native.minWriteBufferNumberToMerge = minWriteBufferNumberToMerge
        return this
    }

    actual fun setCreateMissingColumnFamilies(flag: Boolean): Options {
        native.setCreateMissingColumnFamilies(flag)
        return this
    }

    actual fun createMissingColumnFamilies(): Boolean {
        return native.createMissingColumnFamilies()
    }

    actual fun setErrorIfExists(errorIfExists: Boolean): Options {
        native.setErrorIfExists(errorIfExists)
        return this
    }

    actual fun errorIfExists(): Boolean {
        return native.errorIfExists()
    }

    actual fun setParanoidChecks(paranoidChecks: Boolean): Options {
        native.setParanoidChecks(paranoidChecks)
        return this
    }

    actual fun paranoidChecks(): Boolean {
        return native.paranoidChecks()
    }

    actual fun setInfoLogLevel(infoLogLevel: InfoLogLevel): Options {
        native.infoLogLevel = infoLogLevel.value
        return this
    }

    actual fun infoLogLevel(): InfoLogLevel {
        return getInfoLogLevel(native.infoLogLevel)
    }

    actual fun setStatistics(statistics: Statistics): Options {
        this.statistics = statistics
        native.statistics = statistics.native
        return this
    }

    actual fun statistics(): Statistics? {
        return this.statistics ?:
            native.statistics?.let { Statistics(it) }
    }

    actual fun setUseFsync(useFsync: Boolean): Options {
        native.useFSync = useFsync
        return this
    }

    actual fun useFsync(): Boolean {
        return native.useFSync
    }

    actual fun setMaxLogFileSize(maxLogFileSize: Long): Options {
        native.maxLogFileSize = maxLogFileSize.toULong()
        return this
    }

    actual fun maxLogFileSize(): Long {
        return native.maxLogFileSize.toLong()
    }

    actual fun setLogFileTimeToRoll(logFileTimeToRoll: Long): Options {
        native.logFileTimeToRoll = logFileTimeToRoll.toULong()
        return this
    }

    actual fun logFileTimeToRoll(): Long {
        return native.logFileTimeToRoll.toLong()
    }

    actual fun setKeepLogFileNum(keepLogFileNum: Long): Options {
        native.keepLogFileNum = keepLogFileNum.toULong()
        return this
    }

    actual fun keepLogFileNum(): Long {
        return native.keepLogFileNum.toLong()
    }

    actual fun setWalSizeLimitMB(sizeLimitMB: Long): Options {
        native.setMaxWriteAheadLogSize(sizeLimitMB.toULong() * 1024uL * 1024uL)
        return this
    }

    actual fun walSizeLimitMB(): Long {
        return (native.maxWriteAheadLogSize / 1024uL / 1024uL).toLong()
    }

    actual fun setEnv(env: Env): Options {
        native.setEnv(env.native)
        return this
    }

    actual fun getEnv(): Env {
        return RocksEnv(native.env())
    }
}
