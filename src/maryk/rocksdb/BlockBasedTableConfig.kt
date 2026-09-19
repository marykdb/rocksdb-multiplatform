package maryk.rocksdb

/**
 * Configuration wrapper for RocksDB block-based table factories.
 */
expect class BlockBasedTableConfig() : TableFormatConfig {
    fun setNoBlockCache(noBlockCache: Boolean): BlockBasedTableConfig
    fun noBlockCache(): Boolean

    fun setBlockCache(cache: Cache?): BlockBasedTableConfig

    fun setBlockSize(blockSize: Long): BlockBasedTableConfig
    fun blockSize(): Long

    fun setBlockSizeDeviation(blockSizeDeviation: Int): BlockBasedTableConfig
    fun blockSizeDeviation(): Int

    fun setBlockRestartInterval(blockRestartInterval: Int): BlockBasedTableConfig
    fun blockRestartInterval(): Int

    fun setMetadataBlockSize(metadataBlockSize: Long): BlockBasedTableConfig
    fun metadataBlockSize(): Long

    fun setPartitionFilters(partitionFilters: Boolean): BlockBasedTableConfig
    fun partitionFilters(): Boolean

    fun setCacheIndexAndFilterBlocks(cacheIndexAndFilterBlocks: Boolean): BlockBasedTableConfig
    fun cacheIndexAndFilterBlocks(): Boolean

    fun setCacheIndexAndFilterBlocksWithHighPriority(highPriority: Boolean): BlockBasedTableConfig
    fun cacheIndexAndFilterBlocksWithHighPriority(): Boolean

    fun setPinL0FilterAndIndexBlocksInCache(pinL0: Boolean): BlockBasedTableConfig
    fun pinL0FilterAndIndexBlocksInCache(): Boolean

    fun setPinTopLevelIndexAndFilter(pinTopLevel: Boolean): BlockBasedTableConfig
    fun pinTopLevelIndexAndFilter(): Boolean

    fun setWholeKeyFiltering(wholeKeyFiltering: Boolean): BlockBasedTableConfig
    fun wholeKeyFiltering(): Boolean

    fun setFormatVersion(formatVersion: Int): BlockBasedTableConfig
    fun formatVersion(): Int

    fun setChecksumType(checksumType: ChecksumType): BlockBasedTableConfig
    fun checksumType(): ChecksumType

    fun setIndexType(indexType: IndexType): BlockBasedTableConfig
    fun indexType(): IndexType

    fun setDataBlockIndexType(dataBlockIndexType: DataBlockIndexType): BlockBasedTableConfig
    fun dataBlockIndexType(): DataBlockIndexType

    fun setDataBlockHashTableUtilRatio(ratio: Double): BlockBasedTableConfig
    fun dataBlockHashTableUtilRatio(): Double
}
