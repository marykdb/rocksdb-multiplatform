package maryk.rocksdb

import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import maryk.asSizeT
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
    private var blockSize: Long = 0
    private var blockSizeDeviation: Int = 0
    private var blockRestartInterval: Int = 0
    private var metadataBlockSize: Long = 0
    private var partitionFilters = false
    private var cacheIndexAndFilterBlocks = false
    private var cacheIndexAndFilterBlocksWithHighPriority = false
    private var pinL0FilterAndIndexBlocksInCache = false
    private var pinTopLevelIndexAndFilter = false
    private var wholeKeyFiltering = false
    private var formatVersion = 0
    private var checksumType: ChecksumType = ChecksumType.kCRC32c
    private var indexType: IndexType = IndexType.kBinarySearch
    private var dataBlockIndexType: DataBlockIndexType = DataBlockIndexType.kDataBlockBinarySearch
    private var dataBlockHashTableUtilRatio: Double = 0.0

    @OptIn(UnsafeNumber::class)
    internal fun applyToOptions(options: CPointer<rocksdb_options_t>) {
        val native = rocksdb_block_based_options_create()!!
        try {
            rocksdb_block_based_options_set_no_block_cache(native, noBlockCache.toUByte())
            rocksdb_block_based_options_set_block_size(native, blockSize.asSizeT())
            rocksdb_block_based_options_set_block_size_deviation(native, blockSizeDeviation)
            rocksdb_block_based_options_set_block_restart_interval(native, blockRestartInterval)
            rocksdb_block_based_options_set_metadata_block_size(native, metadataBlockSize.toULong())
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
            rocksdb_block_based_options_set_format_version(native, formatVersion)
            rocksdb_block_based_options_set_checksum(native, checksumType.value)
            rocksdb_block_based_options_set_index_type(native, indexType.value.toInt())
            rocksdb_block_based_options_set_data_block_index_type(native, dataBlockIndexType.ordinal)
            rocksdb_block_based_options_set_data_block_hash_ratio(native, dataBlockHashTableUtilRatio)
            blockCache?.let { cache ->
                rocksdb_block_based_options_set_block_cache(native, cache.native)
            }

            rocksdb_options_set_block_based_table_factory(options, native)
        } finally {
            rocksdb_block_based_options_destroy(native)
        }
    }

    actual fun setNoBlockCache(noBlockCache: Boolean): BlockBasedTableConfig {
        this.noBlockCache = noBlockCache
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
}
