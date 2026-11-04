package maryk.rocksdb

/**
 * Configuration wrapper for RocksDB block-based table factories.
 */
expect class BlockBasedTableConfig() : TableFormatConfig {
    fun setNoBlockCache(noBlockCache: Boolean): BlockBasedTableConfig
    fun noBlockCache(): Boolean

    fun setBlockCache(cache: Cache?): BlockBasedTableConfig

    /**
     * Attach a shared persistent cache, or null to disable it for subsequently
     * configured table factories. Existing factories retain their cache.
     * Keep the cache's environment and logger open until all using databases,
     * options and caches have closed.
     */
    fun setPersistentCache(persistentCache: PersistentCache?): BlockBasedTableConfig

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

    /**
     * Sets the SST filter policy, or null to disable filtering.
     * A policy can be reused across configurations and options. Keep it open until
     * the options and databases using it have closed.
     */
    fun setFilterPolicy(filterPolicy: FilterPolicy?): BlockBasedTableConfig
    fun filterPolicy(): FilterPolicy?

    fun setFormatVersion(formatVersion: Int): BlockBasedTableConfig
    fun formatVersion(): Int

    fun setChecksumType(checksumType: ChecksumType): BlockBasedTableConfig
    fun checksumType(): ChecksumType

    fun setIndexType(indexType: IndexType): BlockBasedTableConfig
    fun indexType(): IndexType

    fun setIndexSearchType(indexSearchType: IndexSearchType): BlockBasedTableConfig
    fun indexSearchType(): IndexSearchType

    fun setDataBlockIndexType(dataBlockIndexType: DataBlockIndexType): BlockBasedTableConfig
    fun dataBlockIndexType(): DataBlockIndexType

    fun setDataBlockHashTableUtilRatio(ratio: Double): BlockBasedTableConfig
    fun dataBlockHashTableUtilRatio(): Double

    fun setIndexBlockRestartInterval(value: Int): BlockBasedTableConfig
    fun indexBlockRestartInterval(): Int

    fun setOptimizeFiltersForMemory(value: Boolean): BlockBasedTableConfig
    fun optimizeFiltersForMemory(): Boolean

    fun setUseDeltaEncoding(value: Boolean): BlockBasedTableConfig
    fun useDeltaEncoding(): Boolean

    fun setVerifyCompression(value: Boolean): BlockBasedTableConfig
    fun verifyCompression(): Boolean

    fun setReadAmpBytesPerBit(value: Int): BlockBasedTableConfig
    fun readAmpBytesPerBit(): Int

    fun setSeparateKeyValueInDataBlock(value: Boolean): BlockBasedTableConfig
    fun separateKeyValueInDataBlock(): Boolean

    fun setUniformCvThreshold(value: Double): BlockBasedTableConfig
    fun uniformCvThreshold(): Double

    fun setEnableIndexCompression(value: Boolean): BlockBasedTableConfig
    fun enableIndexCompression(): Boolean

    fun setBlockAlign(value: Boolean): BlockBasedTableConfig
    fun blockAlign(): Boolean

    fun setSuperBlockAlignmentSize(value: Long): BlockBasedTableConfig
    fun superBlockAlignmentSize(): Long

    fun setSuperBlockAlignmentSpaceOverheadRatio(value: Long): BlockBasedTableConfig
    fun superBlockAlignmentSpaceOverheadRatio(): Long

    fun setIndexShortening(value: IndexShorteningMode): BlockBasedTableConfig
    fun indexShortening(): IndexShorteningMode

}
