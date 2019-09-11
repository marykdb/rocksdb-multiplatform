package maryk.rocksdb

import maryk.rocksdb.MutableOptionKeyValueType.BOOLEAN
import maryk.rocksdb.MutableOptionKeyValueType.DOUBLE
import maryk.rocksdb.MutableOptionKeyValueType.ENUM
import maryk.rocksdb.MutableOptionKeyValueType.INT
import maryk.rocksdb.MutableOptionKeyValueType.INT_ARRAY
import maryk.rocksdb.MutableOptionKeyValueType.LONG

actual class MutableColumnFamilyOptions : AbstractMutableOptions()
actual enum class MemtableOption(
    private val valueType: MutableOptionKeyValueType
) {
    write_buffer_size(LONG),
    arena_block_size(LONG),
    memtable_prefix_bloom_size_ratio(DOUBLE),
    memtable_huge_page_size(LONG),
    max_successive_merges(LONG),
    max_write_buffer_number(INT),
    inplace_update_num_locks(LONG);

    actual fun getValueType() = valueType
}

actual enum class CompactionOption(
    private val valueType: MutableOptionKeyValueType
) {
    disable_auto_compactions(BOOLEAN),
    soft_pending_compaction_bytes_limit(LONG),
    hard_pending_compaction_bytes_limit(LONG),
    level0_file_num_compaction_trigger(INT),
    level0_slowdown_writes_trigger(INT),
    level0_stop_writes_trigger(INT),
    max_compaction_bytes(LONG),
    target_file_size_base(LONG),
    target_file_size_multiplier(INT),
    max_bytes_for_level_base(LONG),
    max_bytes_for_level_multiplier(INT),
    max_bytes_for_level_multiplier_additional(INT_ARRAY),
    ttl(LONG);

    actual fun getValueType() = valueType
}

actual enum class MiscOption(
    private val valueType: MutableOptionKeyValueType
) {
    max_sequential_skip_in_iterations(LONG),
    paranoid_file_checks(BOOLEAN),
    report_bg_io_stats(BOOLEAN),
    compression_type(ENUM);

    actual fun getValueType() = valueType
}

actual class MutableColumnFamilyOptionsBuilder {
    actual fun build(): MutableColumnFamilyOptions {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun writeBufferSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun disableAutoCompactions(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxCompactionBytes(maxCompactionBytes: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxCompactionBytes(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelBase(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCompressionType(compressionType: CompressionType): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun compressionType(): CompressionType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxWriteBufferNumber(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setInplaceUpdateNumLocks(inplaceUpdateNumLocks: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun inplaceUpdateNumLocks(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemtablePrefixBloomSizeRatio(memtablePrefixBloomSizeRatio: Double): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memtablePrefixBloomSizeRatio(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMemtableHugePageSize(memtableHugePageSize: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun memtableHugePageSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setArenaBlockSize(arenaBlockSize: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun arenaBlockSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun level0StopWritesTrigger(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun targetFileSizeBase(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun targetFileSizeMultiplier(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxBytesForLevelMultiplierAdditional(maxBytesForLevelMultiplierAdditional: IntArray): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxBytesForLevelMultiplierAdditional(): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setSoftPendingCompactionBytesLimit(softPendingCompactionBytesLimit: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun softPendingCompactionBytesLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setHardPendingCompactionBytesLimit(hardPendingCompactionBytesLimit: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun hardPendingCompactionBytesLimit(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSequentialSkipInIterations(maxSequentialSkipInIterations: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSequentialSkipInIterations(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxSuccessiveMerges(maxSuccessiveMerges: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun maxSuccessiveMerges(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setParanoidFileChecks(paranoidFileChecks: Boolean): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun paranoidFileChecks(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setReportBgIoStats(reportBgIoStats: Boolean): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun reportBgIoStats(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setTtl(ttl: Long): MutableColumnFamilyOptionsBuilder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun ttl(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

actual fun mutableColumnFamilyOptionsBuilder(): MutableColumnFamilyOptionsBuilder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun mutableColumnFamilyOptionsParse(str: String): MutableColumnFamilyOptionsBuilder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
