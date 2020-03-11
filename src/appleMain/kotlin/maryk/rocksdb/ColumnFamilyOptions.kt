package maryk.rocksdb

import rocksdb.RocksDBColumnFamilyOptions
import rocksdb.RocksDBComparator
import rocksdb.RocksDBPrefixExtractor
import rocksdb.RocksDBPrefixType.RocksDBPrefixFixedLength

actual class ColumnFamilyOptions private constructor(
    internal val native: RocksDBColumnFamilyOptions
) : RocksObject() {
    actual constructor() : this(RocksDBColumnFamilyOptions())

    actual fun setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge)
        return this
    }

    actual fun minWriteBufferNumberToMerge(): Int {
        assert(isOwningHandle())
        return native.minWriteBufferNumberToMerge()
    }

    actual fun setMaxWriteBufferNumberToMaintain(maxWriteBufferNumberToMaintain: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMaxWriteBufferNumber(maxWriteBufferNumberToMaintain)
        return this
    }

    actual fun maxWriteBufferNumberToMaintain(): Int {
        assert(isOwningHandle())
        return native.maxWriteBufferNumber()
    }

    actual fun setBloomLocality(bloomLocality: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setBloomLocality(bloomLocality.toUInt())
        return this
    }

    actual fun bloomLocality(): Int {
        assert(isOwningHandle())
        return native.bloomLocality().toInt()
    }

    actual fun setNumLevels(numLevels: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setNumLevels(numLevels)
        return this
    }

    actual fun numLevels(): Int {
        assert(isOwningHandle())
        return native.numLevels()
    }

    actual fun setCompactionStyle(compactionStyle: CompactionStyle): ColumnFamilyOptions {
        native.setCompactionStyle(compactionStyle.value)
        return this
    }

    actual fun compactionStyle(): CompactionStyle {
        return getCompactionStyle(native.compactionStyle)
    }

    actual fun setComparator(builtinComparator: BuiltinComparator): ColumnFamilyOptions {
        native.setComparator(RocksDBComparator.comparatorWithType(builtinComparator.native))
        return this
    }

    actual fun setComparator(comparator: AbstractComparator): ColumnFamilyOptions {
        native.setComparator(comparator.native)
        return this
    }

    actual fun useFixedLengthPrefixExtractor(n: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setPrefixExtractor(
            RocksDBPrefixExtractor.prefixExtractorWithType(
                RocksDBPrefixFixedLength,
                n.toULong()
            )
        )
        return this
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): ColumnFamilyOptions {
        native.maxBytesForLevelMultiplier = multiplier
        return this
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        return native.maxBytesForLevelMultiplier
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setWriteBufferSize(writeBufferSize.toULong())
        return this
    }

    actual fun writeBufferSize(): Long {
        assert(isOwningHandle())
        return native.writeBufferSize().toLong()
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setDisableAutoCompactions(disableAutoCompactions)
        return this
    }

    actual fun disableAutoCompactions(): Boolean {
        assert(isOwningHandle())
        return native.disableAutoCompactions()
    }

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger)
        return this
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        assert(isOwningHandle())
        return native.level0FileNumCompactionTrigger()
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMaxBytesForLevelBase(maxBytesForLevelBase.toULong())
        return this
    }

    actual fun maxBytesForLevelBase(): Long {
        assert(isOwningHandle())
        return native.maxBytesForLevelBase().toLong()
    }

    actual fun setCompressionType(compressionType: CompressionType): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setCompressionType(compressionType.value)
        return this
    }

    actual fun compressionType(): CompressionType {
        assert(isOwningHandle())
        return getCompressionType(
            native.compressionType()
        )
    }

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMaxWriteBufferNumber(maxWriteBufferNumber)
        return this
    }

    actual fun maxWriteBufferNumber(): Int {
        assert(isOwningHandle())
        return native.maxWriteBufferNumber()
    }

    actual fun setMemtablePrefixBloomSizeRatio(memtablePrefixBloomSizeRatio: Double): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMemtablePrefixBloomSizeRatio(memtablePrefixBloomSizeRatio)
        return this
    }

    actual fun memtablePrefixBloomSizeRatio(): Double {
        assert(isOwningHandle())
        return native.memtablePrefixBloomSizeRatio()
    }

    actual fun setMemtableHugePageSize(memtableHugePageSize: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMemtableHugePageTlbSize(memtableHugePageSize.toULong())
        return this
    }

    actual fun memtableHugePageSize(): Long {
        assert(isOwningHandle())
        return native.memtableHugePageTlbSize.toLong()
    }

    actual fun setArenaBlockSize(arenaBlockSize: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setArenaBlockSize(arenaBlockSize.toULong())
        return this
    }

    actual fun arenaBlockSize(): Long {
        assert(isOwningHandle())
        return native.arenaBlockSize.toLong()
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger)
        return this
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        assert(isOwningHandle())
        return native.level0SlowdownWritesTrigger()
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setLevel0StopWritesTrigger(level0StopWritesTrigger)
        return this
    }

    actual fun level0StopWritesTrigger(): Int {
        assert(isOwningHandle())
        return native.level0StopWritesTrigger()
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setTargetFileSizeBase(targetFileSizeBase.toULong())
        return this
    }

    actual fun targetFileSizeBase(): Long {
        assert(isOwningHandle())
        return native.targetFileSizeBase().toLong()
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setTargetFileSizeMultiplier(multiplier)
        return this
    }

    actual fun targetFileSizeMultiplier(): Int {
        assert(isOwningHandle())
        return native.targetFileSizeMultiplier()
    }

    actual fun setMaxSequentialSkipInIterations(maxSequentialSkipInIterations: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMaxSequentialSkipInIterations(maxSequentialSkipInIterations.toULong())
        return this
    }

    actual fun maxSequentialSkipInIterations(): Long {
        assert(isOwningHandle())
        return native.maxSequentialSkipInIterations().toLong()
    }

    actual fun setMaxSuccessiveMerges(maxSuccessiveMerges: Long): ColumnFamilyOptions {
        assert(isOwningHandle())
        native.setMaxSuccessiveMerges(maxSuccessiveMerges.toULong())
        return this
    }

    actual fun maxSuccessiveMerges(): Long {
        assert(isOwningHandle())
        return native.maxSuccessiveMerges().toLong()
    }
}
