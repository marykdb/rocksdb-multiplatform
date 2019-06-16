package maryk.rocksdb

expect class BlockBasedTableConfig() : TableFormatConfig {
    /**
     * Indicating if we'd put index/filter blocks to the block cache.
     * If not specified, each "table reader" object will pre-load index/filter
     * block during table initialization.
     *
     * @return if index and filter blocks should be put in block cache.
     */
    fun cacheIndexAndFilterBlocks(): Boolean

    /**
     * Indicating if we'd put index/filter blocks to the block cache.
     * If not specified, each "table reader" object will pre-load index/filter
     * block during table initialization.
     *
     * @param cacheIndexAndFilterBlocks and filter blocks should be put in block cache.
     * @return the reference to the current config.
     */
    fun setCacheIndexAndFilterBlocks(
        cacheIndexAndFilterBlocks: Boolean
    ): BlockBasedTableConfig

    /**
     * Indicates if index and filter blocks will be treated as high-priority in the block cache.
     * See note below about applicability. If not specified, defaults to false.
     *
     * @return if index and filter blocks will be treated as high-priority.
     */
    fun cacheIndexAndFilterBlocksWithHighPriority(): Boolean

    /**
     * If true, cache index and filter blocks with high priority. If set to true,
     * depending on implementation of block cache, index and filter blocks may be
     * less likely to be evicted than data blocks.
     *
     * @param cacheIndexAndFilterBlocksWithHighPriority if index and filter blocks
     * will be treated as high-priority.
     * @return the reference to the current config.
     */
    fun setCacheIndexAndFilterBlocksWithHighPriority(
        cacheIndexAndFilterBlocksWithHighPriority: Boolean
    ): BlockBasedTableConfig

    /**
     * Indicating if we'd like to pin L0 index/filter blocks to the block cache.
     * If not specified, defaults to false.
     *
     * @return if L0 index and filter blocks should be pinned to the block cache.
     */
    fun pinL0FilterAndIndexBlocksInCache(): Boolean

    /**
     * Indicating if we'd like to pin L0 index/filter blocks to the block cache.
     * If not specified, defaults to false.
     *
     * @param pinL0FilterAndIndexBlocksInCache pin blocks in block cache
     * @return the reference to the current config.
     */
    fun setPinL0FilterAndIndexBlocksInCache(
        pinL0FilterAndIndexBlocksInCache: Boolean
    ): BlockBasedTableConfig

    /**
     * Indicates if top-level index and filter blocks should be pinned.
     *
     * @return if top-level index and filter blocks should be pinned.
     */
    fun pinTopLevelIndexAndFilter(): Boolean

    /**
     * If cacheIndexAndFilterBlocks is true and the below is true, then
     * the top-level index of partitioned filter and index blocks are stored in
     * the cache, but a reference is held in the "table reader" object so the
     * blocks are pinned and only evicted from cache when the table reader is
     * freed. This is not limited to l0 in LSM tree.
     *
     * @param pinTopLevelIndexAndFilter if top-level index and filter blocks should be pinned.
     * @return the reference to the current config.
     */
    fun setPinTopLevelIndexAndFilter(pinTopLevelIndexAndFilter: Boolean): BlockBasedTableConfig

    /**
     * Get the index type.
     *
     * @return the currently set index type
     */
    fun indexType(): IndexType?

    /**
     * Sets the index type to used with this table.
     *
     * @param indexType [org.rocksdb.IndexType] value
     * @return the reference to the current option.
     */
    fun setIndexType(
        indexType: IndexType
    ): BlockBasedTableConfig

    /**
     * Get the data block index type.
     *
     * @return the currently set data block index type
     */
    fun dataBlockIndexType(): DataBlockIndexType?

    /**
     * Sets the data block index type to used with this table.
     *
     * @param dataBlockIndexType [org.rocksdb.DataBlockIndexType] value
     * @return the reference to the current option.
     */
    fun setDataBlockIndexType(
        dataBlockIndexType: DataBlockIndexType
    ): BlockBasedTableConfig

    /**
     * Get the #entries/#buckets. It is valid only when [.dataBlockIndexType] is
     * [DataBlockIndexType.kDataBlockBinaryAndHash].
     *
     * @return the #entries/#buckets.
     */
    fun dataBlockHashTableUtilRatio(): Double

    /**
     * Set the #entries/#buckets. It is valid only when [.dataBlockIndexType] is
     * [DataBlockIndexType.kDataBlockBinaryAndHash].
     *
     * @param dataBlockHashTableUtilRatio #entries/#buckets
     * @return the reference to the current option.
     */
    fun setDataBlockHashTableUtilRatio(
        dataBlockHashTableUtilRatio: Double
    ): BlockBasedTableConfig

    /**
     * Get the checksum type to be used with this table.
     *
     * @return the currently set checksum type
     */
    fun checksumType(): ChecksumType?

    /**
     * Sets
     *
     * @param checksumType [org.rocksdb.ChecksumType] value.
     * @return the reference to the current option.
     */
    fun setChecksumType(
        checksumType: ChecksumType
    ): BlockBasedTableConfig

    /**
     * Determine if the block cache is disabled.
     *
     * @return if block cache is disabled
     */
    fun noBlockCache(): Boolean

    /**
     * Disable block cache. If this is set to true,
     * then no block cache should be used, and the [.setBlockCache]
     * should point to a `null` object.
     *
     * Default: false
     *
     * @param noBlockCache if use block cache
     * @return the reference to the current config.
     */
    fun setNoBlockCache(noBlockCache: Boolean): BlockBasedTableConfig

    /**
     * Use the specified cache for blocks.
     * When not null this take precedence even if the user sets a block cache size.
     *
     * [org.rocksdb.Cache] should not be disposed before options instances
     * using this cache is disposed.
     *
     * [org.rocksdb.Cache] instance can be re-used in multiple options
     * instances.
     *
     * @param blockCache [org.rocksdb.Cache] Cache java instance
     * (e.g. LRUCache).
     *
     * @return the reference to the current config.
     */
    fun setBlockCache(blockCache: Cache?): BlockBasedTableConfig

    /**
     * Use the specified persistent cache.
     *
     * If `!null` use the specified cache for pages read from device,
     * otherwise no page cache is used.
     *
     * @param persistentCache the persistent cache
     *
     * @return the reference to the current config.
     */
    fun setPersistentCache(
        persistentCache: PersistentCache
    ): BlockBasedTableConfig

    /**
     * Use the specified cache for compressed blocks.
     *
     * If `null`, RocksDB will not use a compressed block cache.
     *
     * Note: though it looks similar to [.setBlockCache], RocksDB
     * doesn't put the same type of object there.
     *
     * [Cache] should not be disposed before options instances
     * using this cache is disposed.
     *
     * [Cache] instance can be re-used in multiple options
     * instances.
     *
     * @param blockCacheCompressed [Cache] Cache java instance
     * (e.g. LRUCache).
     *
     * @return the reference to the current config.
     */
    fun setBlockCacheCompressed(
        blockCacheCompressed: Cache
    ): BlockBasedTableConfig

    /**
     * Get the approximate size of user data packed per block.
     *
     * @return block size in bytes
     */
    fun blockSize(): Long

    /**
     * Approximate size of user data packed per block. Note that the
     * block size specified here corresponds to uncompressed data.  The
     * actual size of the unit read from disk may be smaller if
     * compression is enabled.  This parameter can be changed dynamically.
     * Default: 4K
     *
     * @param blockSize block size in bytes
     * @return the reference to the current config.
     */
    fun setBlockSize(blockSize: Long): BlockBasedTableConfig

    /**
     * @return the hash table ratio.
     */
    fun blockSizeDeviation(): Int

    /**
     * This is used to close a block before it reaches the configured
     * [.blockSize]. If the percentage of free space in the current block
     * is less than this specified number and adding a new record to the block
     * will exceed the configured block size, then this block will be closed and
     * the new record will be written to the next block.
     *
     * Default is 10.
     *
     * @param blockSizeDeviation the deviation to block size allowed
     * @return the reference to the current config.
     */
    fun setBlockSizeDeviation(
        blockSizeDeviation: Int
    ): BlockBasedTableConfig

    /**
     * Get the block restart interval.
     *
     * @return block restart interval
     */
    fun blockRestartInterval(): Int

    /**
     * Set the block restart interval.
     *
     * @param restartInterval block restart interval.
     * @return the reference to the current config.
     */
    fun setBlockRestartInterval(
        restartInterval: Int
    ): BlockBasedTableConfig

    /**
     * Get the index block restart interval.
     *
     * @return index block restart interval
     */
    fun indexBlockRestartInterval(): Int

    /**
     * Set the index block restart interval
     *
     * @param restartInterval index block restart interval.
     * @return the reference to the current config.
     */
    fun setIndexBlockRestartInterval(
        restartInterval: Int
    ): BlockBasedTableConfig

    /**
     * Get the block size for partitioned metadata.
     *
     * @return block size for partitioned metadata.
     */
    fun metadataBlockSize(): Long

    /**
     * Set block size for partitioned metadata.
     *
     * @param metadataBlockSize Partitioned metadata block size.
     * @return the reference to the current config.
     */
    fun setMetadataBlockSize(
        metadataBlockSize: Long
    ): BlockBasedTableConfig

    /**
     * Indicates if we're using partitioned filters.
     *
     * @return if we're using partition filters.
     */
    fun partitionFilters(): Boolean

    /**
     * Use partitioned full filters for each SST file. This option is incompatible
     * with block-based filters.
     *
     * Defaults to false.
     *
     * @param partitionFilters use partition filters.
     * @return the reference to the current config.
     */
    fun setPartitionFilters(partitionFilters: Boolean): BlockBasedTableConfig

    /**
     * Determine if delta encoding is being used to compress block keys.
     *
     * @return true if delta encoding is enabled, false otherwise.
     */
    fun useDeltaEncoding(): Boolean

    /**
     * Use delta encoding to compress keys in blocks.
     *
     * NOTE: [ReadOptions.pinData] requires this option to be disabled.
     *
     * Default: true
     *
     * @param useDeltaEncoding true to enable delta encoding
     *
     * @return the reference to the current config.
     */
    fun setUseDeltaEncoding(
        useDeltaEncoding: Boolean
    ): BlockBasedTableConfig

    /**
     * Get the filter policy.
     *
     * @return the current filter policy.
     */
    fun filterPolicy(): FilterPolicy?

    /**
     * Use the specified filter policy to reduce disk reads.
     *
     * [FilterPolicy] should not be disposed before options instances
     * using this filter is disposed. If [FilterPolicy.dispose] function is not
     * called, then filter object will be GC'd automatically.
     *
     * [FilterPolicy] instance can be re-used in multiple options
     * instances.
     *
     * @param filterPolicy [FilterPolicy] Filter Policy java instance.
     * @return the reference to the current config.
     */
    fun setFilterPolicy(
        filterPolicy: FilterPolicy?
    ): BlockBasedTableConfig

    /**
     * Determine if whole keys as opposed to prefixes are placed in the filter.
     *
     * @return if whole key filtering is enabled
     */
    fun wholeKeyFiltering(): Boolean

    /**
     * If true, place whole keys in the filter (not just prefixes).
     * This must generally be true for gets to be efficient.
     * Default: true
     *
     * @param wholeKeyFiltering if enable whole key filtering
     * @return the reference to the current config.
     */
    fun setWholeKeyFiltering(
        wholeKeyFiltering: Boolean
    ): BlockBasedTableConfig

    /**
     * Returns true when compression verification is enabled.
     *
     * See [.setVerifyCompression].
     *
     * @return true if compression verification is enabled.
     */
    fun verifyCompression(): Boolean

    /**
     * Verify that decompressing the compressed block gives back the input. This
     * is a verification mode that we use to detect bugs in compression
     * algorithms.
     *
     * @param verifyCompression true to enable compression verification.
     *
     * @return the reference to the current config.
     */
    fun setVerifyCompression(
        verifyCompression: Boolean
    ): BlockBasedTableConfig

    /**
     * Get the Read amplification bytes per-bit.
     *
     * See [.setReadAmpBytesPerBit].
     *
     * @return the bytes per-bit.
     */
    fun readAmpBytesPerBit(): Int

    /**
     * Set the Read amplification bytes per-bit.
     *
     * If used, For every data block we load into memory, we will create a bitmap
     * of size ((block_size / `read_amp_bytes_per_bit`) / 8) bytes. This bitmap
     * will be used to figure out the percentage we actually read of the blocks.
     *
     * When this feature is used Tickers::READ_AMP_ESTIMATE_USEFUL_BYTES and
     * Tickers::READ_AMP_TOTAL_READ_BYTES can be used to calculate the
     * read amplification using this formula
     * (READ_AMP_TOTAL_READ_BYTES / READ_AMP_ESTIMATE_USEFUL_BYTES)
     *
     * value  =>  memory usage (percentage of loaded blocks memory)
     * 1      =>  12.50 %
     * 2      =>  06.25 %
     * 4      =>  03.12 %
     * 8      =>  01.56 %
     * 16     =>  00.78 %
     *
     * Note: This number must be a power of 2, if not it will be sanitized
     * to be the next lowest power of 2, for example a value of 7 will be
     * treated as 4, a value of 19 will be treated as 16.
     *
     * Default: 0 (disabled)
     *
     * @param readAmpBytesPerBit the bytes per-bit
     *
     * @return the reference to the current config.
     */
    fun setReadAmpBytesPerBit(readAmpBytesPerBit: Int): BlockBasedTableConfig

    /**
     * Get the format version.
     * See [.setFormatVersion].
     *
     * @return the currently configured format version.
     */
    fun formatVersion(): Int

    /**
     * We currently have five versions:
     *
     *  * **0** - This version is currently written
     * out by all RocksDB's versions by default. Can be read by really old
     * RocksDB's. Doesn't support changing checksum (default is CRC32).
     *  * **1** - Can be read by RocksDB's versions since 3.0.
     * Supports non-default checksum, like xxHash. It is written by RocksDB when
     * BlockBasedTableOptions::checksum is something other than kCRC32c. (version
     * 0 is silently upconverted)
     *  * **2** - Can be read by RocksDB's versions since 3.10.
     * Changes the way we encode compressed blocks with LZ4, BZip2 and Zlib
     * compression. If you don't plan to run RocksDB before version 3.10,
     * you should probably use this.
     *  * **3** - Can be read by RocksDB's versions since 5.15. Changes the way we
     * encode the keys in index blocks. If you don't plan to run RocksDB before
     * version 5.15, you should probably use this.
     * This option only affects newly written tables. When reading existing
     * tables, the information about version is read from the footer.
     *  * **4** - Can be read by RocksDB's versions since 5.16. Changes the way we
     * encode the values in index blocks. If you don't plan to run RocksDB before
     * version 5.16 and you are using index_block_restart_interval &gt; 1, you should
     * probably use this as it would reduce the index size.
     *
     *  This option only affects newly written tables. When reading existing
     * tables, the information about version is read from the footer.
     *
     * @param formatVersion integer representing the version to be used.
     *
     * @return the reference to the current option.
     */
    fun setFormatVersion(
        formatVersion: Int
    ): BlockBasedTableConfig

    /**
     * Determine if index compression is enabled.
     *
     * See [.setEnableIndexCompression].
     *
     * @return true if index compression is enabled, false otherwise
     */
    fun enableIndexCompression(): Boolean

    /**
     * Store index blocks on disk in compressed format.
     *
     * Changing this option to false  will avoid the overhead of decompression
     * if index blocks are evicted and read back.
     *
     * @param enableIndexCompression true to enable index compression,
     * false to disable
     *
     * @return the reference to the current option.
     */
    fun setEnableIndexCompression(
        enableIndexCompression: Boolean
    ): BlockBasedTableConfig

    /**
     * Determines whether data blocks are aligned on the lesser of page size
     * and block size.
     *
     * @return true if data blocks are aligned on the lesser of page size
     * and block size.
     */
    fun blockAlign(): Boolean

    /**
     * Set whether data blocks should be aligned on the lesser of page size
     * and block size.
     *
     * @param blockAlign true to align data blocks on the lesser of page size
     * and block size.
     *
     * @return the reference to the current option.
     */
    fun setBlockAlign(blockAlign: Boolean): BlockBasedTableConfig
}
