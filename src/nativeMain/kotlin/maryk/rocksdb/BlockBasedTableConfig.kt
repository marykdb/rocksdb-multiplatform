package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import maryk.asUInt32
import maryk.asSizeT
import maryk.asUInt64
import maryk.toUByte
import rocksdb.rocksdb_block_based_options_create
import rocksdb.rocksdb_block_based_options_destroy
import rocksdb.rocksdb_block_based_options_set_block_cache
import rocksdb.rocksdb_block_based_options_set_block_restart_interval
import rocksdb.rocksdb_block_based_options_set_block_size
import rocksdb.rocksdb_block_based_options_set_block_size_deviation
import rocksdb.rocksdb_block_based_options_set_cache_index_and_filter_blocks
import rocksdb.rocksdb_block_based_options_set_cache_index_and_filter_blocks_with_high_priority
import rocksdb.rocksdb_block_based_options_set_checksum
import rocksdb.rocksdb_block_based_options_set_data_block_hash_ratio
import rocksdb.rocksdb_block_based_options_set_data_block_index_type
import rocksdb.rocksdb_block_based_options_set_format_version
import rocksdb.rocksdb_block_based_options_set_index_type
import rocksdb.rocksdb_block_based_options_set_metadata_block_size
import rocksdb.rocksdb_block_based_options_set_no_block_cache
import rocksdb.rocksdb_block_based_options_set_partition_filters
import rocksdb.rocksdb_block_based_options_set_pin_l0_filter_and_index_blocks_in_cache
import rocksdb.rocksdb_block_based_options_set_pin_top_level_index_and_filter
import rocksdb.rocksdb_block_based_options_set_whole_key_filtering
import rocksdb.rocksdb_options_set_block_based_table_factory

actual class BlockBasedTableConfig actual constructor() : TableFormatConfig() {
    private var noBlockCache = false
    private var blockCache: Cache? = null
    private var persistentCache: PersistentCache? = null
    private var blockSize: Long = 4096
    private var blockSizeDeviation: Int = 10
    private var blockRestartInterval: Int = 16
    private var metadataBlockSize: Long = 4096
    private var partitionFilters = false
    private var cacheIndexAndFilterBlocks = false
    private var cacheIndexAndFilterBlocksWithHighPriority = true
    private var pinL0FilterAndIndexBlocksInCache = false
    private var pinTopLevelIndexAndFilter = true
    private var wholeKeyFiltering = true
    private var filterPolicy: FilterPolicy? = null
    private var formatVersion = 7
    private var checksumType: ChecksumType = ChecksumType.kXXH3
    private var indexType: IndexType = IndexType.kBinarySearch
    private var indexSearchType: IndexSearchType = IndexSearchType.kBinary
    private var dataBlockIndexType: DataBlockIndexType = DataBlockIndexType.kDataBlockBinarySearch
    private var dataBlockHashTableUtilRatio: Double = 0.75

    private var indexBlockRestartIntervalValue: Int = 1
    private var optimizeFiltersForMemoryValue: Boolean = true
    private var useDeltaEncodingValue: Boolean = true
    private var verifyCompressionValue: Boolean = false
    private var readAmpBytesPerBitValue: Int = 0
    private var separateKeyValueInDataBlockValue: Boolean = false
    private var uniformCvThresholdValue: Double = 0.2
    private var enableIndexCompressionValue: Boolean = true
    private var blockAlignValue: Boolean = false
    private var superBlockAlignmentSizeValue: Long = 0L
    private var superBlockAlignmentSpaceOverheadRatioValue: Long = 128L
    private var indexShorteningValue: IndexShorteningMode = IndexShorteningMode.kShortenSeparators

    @OptIn(UnsafeNumber::class)
    internal fun applyToOptions(options: CPointer<rocksdb_options_t>) {
        val native = requireNotNull(rocksdb_block_based_options_create()) {
            "Unable to allocate block-based table options"
        }
        try {
            rocksdb_block_based_options_set_no_block_cache(native, noBlockCache.toUByte())
            rocksdb_block_based_options_set_block_size(native, blockSize.asSizeT())
            rocksdb_block_based_options_set_block_size_deviation(native, blockSizeDeviation)
            rocksdb_block_based_options_set_block_restart_interval(native, blockRestartInterval)
            rocksdb_block_based_options_set_metadata_block_size(native, metadataBlockSize.asUInt64())
            rocksdb_block_based_options_set_partition_filters(native, partitionFilters.toUByte())
            rocksdb_block_based_options_set_cache_index_and_filter_blocks(native, cacheIndexAndFilterBlocks.toUByte())
            rocksdb_block_based_options_set_cache_index_and_filter_blocks_with_high_priority(
                native,
                cacheIndexAndFilterBlocksWithHighPriority.toUByte()
            )
            rocksdb_block_based_options_set_pin_l0_filter_and_index_blocks_in_cache(
                native,
                pinL0FilterAndIndexBlocksInCache.toUByte()
            )
            rocksdb_block_based_options_set_pin_top_level_index_and_filter(
                native,
                pinTopLevelIndexAndFilter.toUByte()
            )
            rocksdb_block_based_options_set_whole_key_filtering(native, wholeKeyFiltering.toUByte())
            filterPolicy?.let { policy ->
                // The C setter takes ownership. Each factory needs its own allocation.
                rocksdb.rocksdb_block_based_options_set_filter_policy(native, policy.createNativeCopy())
            }
            rocksdb_block_based_options_set_format_version(native, formatVersion)
            rocksdb_block_based_options_set_checksum(native, checksumType.value)
            rocksdb_block_based_options_set_index_type(native, indexType.value.toInt())
            rocksdb.rocksdb_block_based_options_set_index_block_search_type(native, indexSearchType.ordinal)
            rocksdb_block_based_options_set_data_block_index_type(native, dataBlockIndexType.ordinal)
            rocksdb_block_based_options_set_data_block_hash_ratio(native, dataBlockHashTableUtilRatio)
            blockCache?.let { cache ->
                cache.checkOwningHandle()
                rocksdb_block_based_options_set_block_cache(native, cache.native)
            }
            rocksdb.rocksdb_block_based_options_set_persistent_cache(native, persistentCache?.let { cache ->
                cache.checkOwningHandle()
                cache.native
            })

            rocksdb.rocksdb_block_based_options_set_index_block_restart_interval(native, indexBlockRestartIntervalValue)
            rocksdb.rocksdb_block_based_options_set_optimize_filters_for_memory(native, optimizeFiltersForMemoryValue.toUByte())
            rocksdb.rocksdb_block_based_options_set_use_delta_encoding(native, useDeltaEncodingValue.toUByte())
            rocksdb.rocksdb_block_based_options_set_verify_compression(native, verifyCompressionValue.toUByte())
            rocksdb.rocksdb_block_based_options_set_read_amp_bytes_per_bit(native, readAmpBytesPerBitValue.asUInt32())
            rocksdb.rocksdb_block_based_options_set_separate_key_value_in_data_block(native, separateKeyValueInDataBlockValue.toUByte())
            rocksdb.rocksdb_block_based_options_set_uniform_cv_threshold(native, uniformCvThresholdValue)
            rocksdb.rocksdb_block_based_options_set_enable_index_compression(native, enableIndexCompressionValue.toUByte())
            rocksdb.rocksdb_block_based_options_set_block_align(native, blockAlignValue.toUByte())
            rocksdb.rocksdb_block_based_options_set_super_block_alignment_size(native, superBlockAlignmentSizeValue.asSizeT())
            rocksdb.rocksdb_block_based_options_set_super_block_alignment_space_overhead_ratio(native, superBlockAlignmentSpaceOverheadRatioValue.asSizeT())
            rocksdb.rocksdb_block_based_options_set_index_shortening(native, indexShorteningValue.ordinal)

            rocksdb_options_set_block_based_table_factory(options, native)
        } finally {
            rocksdb_block_based_options_destroy(native)
        }
    }

    actual fun setNoBlockCache(noBlockCache: Boolean): BlockBasedTableConfig {
        this.noBlockCache = noBlockCache
        return this
    }

    actual fun setPersistentCache(persistentCache: PersistentCache?): BlockBasedTableConfig {
        persistentCache?.checkOwningHandle()
        this.persistentCache = persistentCache
        return this
    }

    actual fun noBlockCache(): Boolean = noBlockCache

    actual fun setBlockCache(cache: Cache?): BlockBasedTableConfig {
        blockCache = cache
        return this
    }

    actual fun setBlockSize(blockSize: Long): BlockBasedTableConfig {
        this.blockSize = blockSize
        return this
    }

    actual fun blockSize(): Long = blockSize

    actual fun setBlockSizeDeviation(blockSizeDeviation: Int): BlockBasedTableConfig {
        this.blockSizeDeviation = blockSizeDeviation
        return this
    }

    actual fun blockSizeDeviation(): Int = blockSizeDeviation

    actual fun setBlockRestartInterval(blockRestartInterval: Int): BlockBasedTableConfig {
        this.blockRestartInterval = blockRestartInterval
        return this
    }

    actual fun blockRestartInterval(): Int = blockRestartInterval

    actual fun setMetadataBlockSize(metadataBlockSize: Long): BlockBasedTableConfig {
        this.metadataBlockSize = metadataBlockSize
        return this
    }

    actual fun metadataBlockSize(): Long = metadataBlockSize

    actual fun setPartitionFilters(partitionFilters: Boolean): BlockBasedTableConfig {
        this.partitionFilters = partitionFilters
        return this
    }

    actual fun partitionFilters(): Boolean = partitionFilters

    actual fun setCacheIndexAndFilterBlocks(cacheIndexAndFilterBlocks: Boolean): BlockBasedTableConfig {
        this.cacheIndexAndFilterBlocks = cacheIndexAndFilterBlocks
        return this
    }

    actual fun cacheIndexAndFilterBlocks(): Boolean = cacheIndexAndFilterBlocks

    actual fun setCacheIndexAndFilterBlocksWithHighPriority(highPriority: Boolean): BlockBasedTableConfig {
        this.cacheIndexAndFilterBlocksWithHighPriority = highPriority
        return this
    }

    actual fun cacheIndexAndFilterBlocksWithHighPriority(): Boolean = cacheIndexAndFilterBlocksWithHighPriority

    actual fun setPinL0FilterAndIndexBlocksInCache(pinL0: Boolean): BlockBasedTableConfig {
        this.pinL0FilterAndIndexBlocksInCache = pinL0
        return this
    }

    actual fun pinL0FilterAndIndexBlocksInCache(): Boolean = pinL0FilterAndIndexBlocksInCache

    actual fun setPinTopLevelIndexAndFilter(pinTopLevel: Boolean): BlockBasedTableConfig {
        this.pinTopLevelIndexAndFilter = pinTopLevel
        return this
    }

    actual fun pinTopLevelIndexAndFilter(): Boolean = pinTopLevelIndexAndFilter

    actual fun setWholeKeyFiltering(wholeKeyFiltering: Boolean): BlockBasedTableConfig {
        this.wholeKeyFiltering = wholeKeyFiltering
        return this
    }

    actual fun wholeKeyFiltering(): Boolean = wholeKeyFiltering

    actual fun setFilterPolicy(filterPolicy: FilterPolicy?): BlockBasedTableConfig {
        filterPolicy?.checkOwningHandle()
        this.filterPolicy = filterPolicy
        return this
    }

    actual fun filterPolicy(): FilterPolicy? = filterPolicy

    actual fun setFormatVersion(formatVersion: Int): BlockBasedTableConfig {
        this.formatVersion = formatVersion
        return this
    }

    actual fun formatVersion(): Int = formatVersion

    actual fun setChecksumType(checksumType: ChecksumType): BlockBasedTableConfig {
        this.checksumType = checksumType
        return this
    }

    actual fun checksumType(): ChecksumType = checksumType

    actual fun setIndexType(indexType: IndexType): BlockBasedTableConfig {
        this.indexType = indexType
        return this
    }

    actual fun indexType(): IndexType = indexType

    actual fun setIndexSearchType(indexSearchType: IndexSearchType): BlockBasedTableConfig {
        this.indexSearchType = indexSearchType
        return this
    }

    actual fun indexSearchType(): IndexSearchType = indexSearchType

    actual fun setDataBlockIndexType(dataBlockIndexType: DataBlockIndexType): BlockBasedTableConfig {
        this.dataBlockIndexType = dataBlockIndexType
        return this
    }

    actual fun dataBlockIndexType(): DataBlockIndexType = dataBlockIndexType

    actual fun setDataBlockHashTableUtilRatio(ratio: Double): BlockBasedTableConfig {
        this.dataBlockHashTableUtilRatio = ratio
        return this
    }

    actual fun dataBlockHashTableUtilRatio(): Double = dataBlockHashTableUtilRatio

    actual fun setIndexBlockRestartInterval(value: Int): BlockBasedTableConfig {
        require(value >= 0) { "value must be non-negative" }
        indexBlockRestartIntervalValue = value
        return this
    }

    actual fun indexBlockRestartInterval(): Int = indexBlockRestartIntervalValue

    actual fun setOptimizeFiltersForMemory(value: Boolean): BlockBasedTableConfig {
        optimizeFiltersForMemoryValue = value
        return this
    }

    actual fun optimizeFiltersForMemory(): Boolean = optimizeFiltersForMemoryValue

    actual fun setUseDeltaEncoding(value: Boolean): BlockBasedTableConfig {
        useDeltaEncodingValue = value
        return this
    }

    actual fun useDeltaEncoding(): Boolean = useDeltaEncodingValue

    actual fun setVerifyCompression(value: Boolean): BlockBasedTableConfig {
        verifyCompressionValue = value
        return this
    }

    actual fun verifyCompression(): Boolean = verifyCompressionValue

    actual fun setReadAmpBytesPerBit(value: Int): BlockBasedTableConfig {
        require(value >= 0) { "value must be non-negative" }
        readAmpBytesPerBitValue = value
        return this
    }

    actual fun readAmpBytesPerBit(): Int = readAmpBytesPerBitValue

    actual fun setSeparateKeyValueInDataBlock(value: Boolean): BlockBasedTableConfig {
        separateKeyValueInDataBlockValue = value
        return this
    }

    actual fun separateKeyValueInDataBlock(): Boolean = separateKeyValueInDataBlockValue

    actual fun setUniformCvThreshold(value: Double): BlockBasedTableConfig {
        require(value.isFinite() && value >= 0.0) { "threshold must be finite and non-negative" }
        uniformCvThresholdValue = value
        return this
    }

    actual fun uniformCvThreshold(): Double = uniformCvThresholdValue

    actual fun setEnableIndexCompression(value: Boolean): BlockBasedTableConfig {
        enableIndexCompressionValue = value
        return this
    }

    actual fun enableIndexCompression(): Boolean = enableIndexCompressionValue

    actual fun setBlockAlign(value: Boolean): BlockBasedTableConfig {
        blockAlignValue = value
        return this
    }

    actual fun blockAlign(): Boolean = blockAlignValue

    actual fun setSuperBlockAlignmentSize(value: Long): BlockBasedTableConfig {
        require(value >= 0) { "value must be non-negative" }
        superBlockAlignmentSizeValue = value
        return this
    }

    actual fun superBlockAlignmentSize(): Long = superBlockAlignmentSizeValue

    actual fun setSuperBlockAlignmentSpaceOverheadRatio(value: Long): BlockBasedTableConfig {
        require(value >= 0) { "value must be non-negative" }
        superBlockAlignmentSpaceOverheadRatioValue = value
        return this
    }

    actual fun superBlockAlignmentSpaceOverheadRatio(): Long = superBlockAlignmentSpaceOverheadRatioValue

    actual fun setIndexShortening(value: IndexShorteningMode): BlockBasedTableConfig {
        indexShorteningValue = value
        return this
    }

    actual fun indexShortening(): IndexShorteningMode = indexShorteningValue

}
