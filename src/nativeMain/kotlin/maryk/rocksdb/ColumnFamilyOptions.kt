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
import maryk.usePointer
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_options_create
import rocksdb.rocksdb_options_destroy
import rocksdb.rocksdb_options_get_arena_block_size
import rocksdb.rocksdb_options_get_bloom_locality
import rocksdb.rocksdb_options_get_compaction_style
import rocksdb.rocksdb_options_get_compression
import rocksdb.rocksdb_options_get_disable_auto_compactions
import rocksdb.rocksdb_options_get_level0_file_num_compaction_trigger
import rocksdb.rocksdb_options_get_level0_slowdown_writes_trigger
import rocksdb.rocksdb_options_get_level0_stop_writes_trigger
import rocksdb.rocksdb_options_get_max_bytes_for_level_base
import rocksdb.rocksdb_options_get_max_bytes_for_level_multiplier
import rocksdb.rocksdb_options_get_max_sequential_skip_in_iterations
import rocksdb.rocksdb_options_get_max_successive_merges
import rocksdb.rocksdb_options_get_max_write_buffer_number
import rocksdb.rocksdb_options_get_memtable_huge_page_size
import rocksdb.rocksdb_options_get_memtable_prefix_bloom_size_ratio
import rocksdb.rocksdb_options_get_min_write_buffer_number_to_merge
import rocksdb.rocksdb_options_get_num_levels
import rocksdb.rocksdb_options_get_target_file_size_base
import rocksdb.rocksdb_options_get_target_file_size_multiplier
import rocksdb.rocksdb_options_get_write_buffer_size
import rocksdb.rocksdb_options_set_arena_block_size
import rocksdb.rocksdb_options_set_bloom_locality
import rocksdb.rocksdb_options_set_compaction_style
import rocksdb.rocksdb_options_set_comparator
import rocksdb.rocksdb_options_set_compaction_filter
import rocksdb.rocksdb_options_set_compaction_filter_factory
import rocksdb.rocksdb_options_set_compression
import rocksdb.rocksdb_options_set_disable_auto_compactions
import rocksdb.rocksdb_options_set_level0_file_num_compaction_trigger
import rocksdb.rocksdb_options_set_level0_slowdown_writes_trigger
import rocksdb.rocksdb_options_set_level0_stop_writes_trigger
import rocksdb.rocksdb_options_set_max_bytes_for_level_base
import rocksdb.rocksdb_options_set_max_bytes_for_level_multiplier
import rocksdb.rocksdb_options_set_max_sequential_skip_in_iterations
import rocksdb.rocksdb_options_set_max_successive_merges
import rocksdb.rocksdb_options_set_max_write_buffer_number
import rocksdb.rocksdb_options_set_memtable_huge_page_size
import rocksdb.rocksdb_options_set_memtable_prefix_bloom_size_ratio
import rocksdb.rocksdb_options_set_min_write_buffer_number_to_merge
import rocksdb.rocksdb_options_set_num_levels
import rocksdb.rocksdb_options_set_prefix_extractor
import rocksdb.rocksdb_options_set_target_file_size_base
import rocksdb.rocksdb_options_set_target_file_size_multiplier
import rocksdb.rocksdb_options_set_write_buffer_size
import rocksdb.rocksdb_slicetransform_create_fixed_prefix
import kotlin.experimental.ExperimentalNativeApi

actual class ColumnFamilyOptions private constructor(
    internal val native: CPointer<rocksdb_options_t>
) : RocksObject() {
    private var tableFormatConfig: TableFormatConfig? = null
    private var ownedComparator: AbstractComparator? = null
    private var compactionFilter: AbstractCompactionFilter<out AbstractSlice<*>>? = null
    private var compactionFilterFactory: AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>>? = null

    actual constructor() : this(requireNotNull(rocksdb_options_create()) { "Unable to allocate RocksDB column-family options" })

    companion object {
        internal fun wrap(native: CPointer<rocksdb_options_t>, owning: Boolean): ColumnFamilyOptions {
            val options = ColumnFamilyOptions(native)
            if (!owning) {
                options.disownHandle()
            }
            return options
        }
    }

    override fun close() {
        if (tryClose()) {
            val comparator = ownedComparator
            ownedComparator = null
            if (comparator != null) {
                rocksdb_options_set_comparator(native, null)
            }
            rocksdb_options_destroy(native)
            comparator?.closeFromOptions()
            tableFormatConfig = null
            compactionFilter = null
            compactionFilterFactory = null
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

    internal fun retainedNativeReferences(): List<Any> =
        listOfNotNull(compactionFilter, compactionFilterFactory)

    actual fun setTableFormatConfig(tableFormatConfig: TableFormatConfig): ColumnFamilyOptions {
        checkOwningHandle()
        when (tableFormatConfig) {
            is BlockBasedTableConfig -> tableFormatConfig.applyToOptions(native)
            is PlainTableConfig -> tableFormatConfig.applyToOptions(native)
            else -> error("Unsupported table format config: ${tableFormatConfig::class.simpleName}")
        }
        this.tableFormatConfig = tableFormatConfig
        return this
    }

    actual fun setMinWriteBufferNumberToMerge(minWriteBufferNumberToMerge: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_min_write_buffer_number_to_merge(native, minWriteBufferNumberToMerge)
        return this
    }

    actual fun minWriteBufferNumberToMerge(): Int {
        checkOwningHandle()
        return rocksdb_options_get_min_write_buffer_number_to_merge(native)
    }

    actual fun setBloomLocality(bloomLocality: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_bloom_locality(native, bloomLocality.asUInt32())
        return this
    }

    actual fun bloomLocality(): Int {
        checkOwningHandle()
        return rocksdb_options_get_bloom_locality(native).toCheckedInt("column-family options bloom locality")
    }

    actual fun setNumLevels(numLevels: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_num_levels(native, numLevels)
        return this
    }

    actual fun numLevels(): Int {
        checkOwningHandle()
        return rocksdb_options_get_num_levels(native)
    }

    actual fun setCompactionStyle(compactionStyle: CompactionStyle): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_compaction_style(native, compactionStyle.value.toInt())
        return this
    }

    actual fun compactionStyle(): CompactionStyle {
        checkOwningHandle()
        return getCompactionStyle(rocksdb_options_get_compaction_style(native).toByte())
    }

    actual fun setComparator(builtinComparator: BuiltinComparator): ColumnFamilyOptions {
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

    actual fun setComparator(comparator: AbstractComparator): ColumnFamilyOptions {
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

    actual fun setMergeOperator(mergeOperator: MergeOperator): ColumnFamilyOptions {
        checkOwningHandle()
        mergeOperator.transferOwnershipToNative()
        rocksdb.rocksdb_options_set_merge_operator(native, mergeOperator.native)
        return this
    }

    actual fun setMergeOperatorName(name: String): ColumnFamilyOptions {
        checkOwningHandle()
        val nameBytes = name.encodeToByteArray()
        wrapWithErrorThrower { error ->
            nameBytes.usePointer { namePointer ->
                rocksdb.rocksdb_options_set_merge_operator_name(
                    native,
                    namePointer,
                    nameBytes.size.asSizeT(),
                    error
                )
            }
        }
        return this
    }

    actual fun setCompactionFilter(
        compactionFilter: AbstractCompactionFilter<out AbstractSlice<*>>
    ): ColumnFamilyOptions {
        checkOwningHandle()
        compactionFilter.checkOwningHandle()
        rocksdb_options_set_compaction_filter(native, compactionFilter.native)
        this.compactionFilter = compactionFilter
        return this
    }

    actual fun compactionFilter(): AbstractCompactionFilter<out AbstractSlice<*>>? {
        checkOwningHandle()
        return compactionFilter
    }

    actual fun setCompactionFilterFactory(
        compactionFilterFactory: AbstractCompactionFilterFactory<out AbstractCompactionFilter<*>>
    ): ColumnFamilyOptions {
        checkOwningHandle()
        val nativeFactory = compactionFilterFactory.transferOwnershipToNative()
        rocksdb_options_set_compaction_filter_factory(native, nativeFactory)
        this.compactionFilterFactory = compactionFilterFactory
        return this
    }

    actual fun useFixedLengthPrefixExtractor(n: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_prefix_extractor(native, rocksdb_slicetransform_create_fixed_prefix(n.asSizeT()))
        return this
    }

    actual fun setMaxBytesForLevelMultiplier(multiplier: Double): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_max_bytes_for_level_multiplier(native, multiplier)
        return this
    }

    actual fun maxBytesForLevelMultiplier(): Double {
        checkOwningHandle()
        return rocksdb_options_get_max_bytes_for_level_multiplier(native)
    }

    actual fun setWriteBufferSize(writeBufferSize: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_write_buffer_size(native, writeBufferSize.asSizeT())
        return this
    }

    actual fun writeBufferSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_write_buffer_size(native), "column-family options write buffer size")
    }

    actual fun setDisableAutoCompactions(disableAutoCompactions: Boolean): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_disable_auto_compactions(native, if (disableAutoCompactions) 1 else 0)
        return this
    }

    actual fun disableAutoCompactions(): Boolean {
        checkOwningHandle()
        return rocksdb_options_get_disable_auto_compactions(native).toBoolean()
    }

    actual fun setLevel0FileNumCompactionTrigger(level0FileNumCompactionTrigger: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_level0_file_num_compaction_trigger(native, level0FileNumCompactionTrigger)
        return this
    }

    actual fun level0FileNumCompactionTrigger(): Int {
        checkOwningHandle()
        return rocksdb_options_get_level0_file_num_compaction_trigger(native)
    }

    actual fun setMaxBytesForLevelBase(maxBytesForLevelBase: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_max_bytes_for_level_base(native, maxBytesForLevelBase.asUInt64())
        return this
    }

    actual fun maxBytesForLevelBase(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_bytes_for_level_base(native).toCheckedLong("column-family options max bytes for level base")
    }

    actual fun setCompressionType(compressionType: CompressionType): ColumnFamilyOptions {
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

    actual fun setMaxWriteBufferNumber(maxWriteBufferNumber: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_max_write_buffer_number(native, maxWriteBufferNumber)
        return this
    }

    actual fun maxWriteBufferNumber(): Int {
        checkOwningHandle()
        return rocksdb_options_get_max_write_buffer_number(native)
    }

    actual fun setMemtablePrefixBloomSizeRatio(memtablePrefixBloomSizeRatio: Double): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_memtable_prefix_bloom_size_ratio(native, memtablePrefixBloomSizeRatio)
        return this
    }

    actual fun memtablePrefixBloomSizeRatio(): Double {
        checkOwningHandle()
        return rocksdb_options_get_memtable_prefix_bloom_size_ratio(native)
    }

    actual fun setMemtableHugePageSize(memtableHugePageSize: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_memtable_huge_page_size(native, memtableHugePageSize.asSizeT())
        return this
    }

    actual fun memtableHugePageSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_memtable_huge_page_size(native), "column-family options memtable huge page size")
    }

    actual fun setArenaBlockSize(arenaBlockSize: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_arena_block_size(native, arenaBlockSize.asSizeT())
        return this
    }

    actual fun arenaBlockSize(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_arena_block_size(native), "column-family options arena block size")
    }

    actual fun setLevel0SlowdownWritesTrigger(level0SlowdownWritesTrigger: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_level0_slowdown_writes_trigger(native, level0SlowdownWritesTrigger)
        return this
    }

    actual fun level0SlowdownWritesTrigger(): Int {
        checkOwningHandle()
        return rocksdb_options_get_level0_slowdown_writes_trigger(native)
    }

    actual fun setLevel0StopWritesTrigger(level0StopWritesTrigger: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_level0_stop_writes_trigger(native, level0StopWritesTrigger)
        return this
    }

    actual fun level0StopWritesTrigger(): Int {
        checkOwningHandle()
        return rocksdb_options_get_level0_stop_writes_trigger(native)
    }

    actual fun setTargetFileSizeBase(targetFileSizeBase: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_target_file_size_base(native, targetFileSizeBase.asUInt64())
        return this
    }

    actual fun targetFileSizeBase(): Long {
        checkOwningHandle()
        return rocksdb_options_get_target_file_size_base(native).toCheckedLong("column-family options target file size base")
    }

    actual fun setTargetFileSizeMultiplier(multiplier: Int): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_target_file_size_multiplier(native, multiplier)
        return this
    }

    actual fun targetFileSizeMultiplier(): Int {
        checkOwningHandle()
        return rocksdb_options_get_target_file_size_multiplier(native)
    }

    actual fun setMaxSequentialSkipInIterations(maxSequentialSkipInIterations: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_max_sequential_skip_in_iterations(native, maxSequentialSkipInIterations.asUInt64())
        return this
    }

    actual fun maxSequentialSkipInIterations(): Long {
        checkOwningHandle()
        return rocksdb_options_get_max_sequential_skip_in_iterations(native)
            .toCheckedLong("column-family options max sequential skip in iterations")
    }

    actual fun setMaxSuccessiveMerges(maxSuccessiveMerges: Long): ColumnFamilyOptions {
        checkOwningHandle()
        rocksdb_options_set_max_successive_merges(native, maxSuccessiveMerges.asSizeT())
        return this
    }

    actual fun maxSuccessiveMerges(): Long {
        checkOwningHandle()
        return sizeTToLong(rocksdb_options_get_max_successive_merges(native), "column-family options max successive merges")
    }
}
