package maryk.rocksdb

/**
 * The config for plain table sst format.
 *
 * PlainTable is a RocksDB's SST file format optimized for low query
 * latency on pure-memory or really low-latency media.
 *
 * It also support prefix hash feature.
 */
expect class PlainTableConfig() : TableFormatConfig {
    /**
     * Set the length of the user key. If it is set to be
     * VARIABLE_LENGTH, then it indicates the user keys are
     * of variable length.
     *
     * Otherwise, all the keys need to have the same length
     * in byte.
     *
     * DEFAULT: VARIABLE_LENGTH
     *
     * @param keySize the length of the user key.
     * @return the reference to the current config.
     */
    fun setKeySize(keySize: Int): PlainTableConfig

    /**
     * @return the specified size of the user key.  If VARIABLE_LENGTH,
     * then it indicates variable-length key.
     */
    fun keySize(): Int

    /**
     * Set the number of bits per key used by the internal bloom filter
     * in the plain table sst format.
     *
     * @param bitsPerKey the number of bits per key for bloom filer.
     * @return the reference to the current config.
     */
    fun setBloomBitsPerKey(bitsPerKey: Int): PlainTableConfig

    /**
     * @return the number of bits per key used for the bloom filter.
     */
    fun bloomBitsPerKey(): Int

    /**
     * hashTableRatio is the desired utilization of the hash table used
     * for prefix hashing.  The ideal ratio would be the number of
     * prefixes / the number of hash buckets.  If this value is set to
     * zero, then hash table will not be used.
     *
     * @param ratio the hash table ratio.
     * @return the reference to the current config.
     */
    fun setHashTableRatio(ratio: Double): PlainTableConfig

    /**
     * @return the hash table ratio.
     */
    fun hashTableRatio(): Double

    /**
     * Index sparseness determines the index interval for keys inside the
     * same prefix.  This number is equal to the maximum number of linear
     * search required after hash and binary search.  If it's set to 0,
     * then each key will be indexed.
     *
     * @param sparseness the index sparseness.
     * @return the reference to the current config.
     */
    fun setIndexSparseness(sparseness: Int): PlainTableConfig

    /**
     * @return the index sparseness.
     */
    fun indexSparseness(): Long

    /**
     * huge_page_tlb_size: if 0, allocate hash indexes and blooms
     * from malloc otherwise from huge page TLB.
     *
     * The user needs to reserve huge pages for it to be allocated,
     * like: `sysctl -w vm.nr_hugepages=20`
     *
     * See linux doc Documentation/vm/hugetlbpage.txt
     *
     * @param hugePageTlbSize huge page tlb size
     * @return the reference to the current config.
     */
    fun setHugePageTlbSize(hugePageTlbSize: Int): PlainTableConfig

    /**
     * Returns the value for huge page tlb size
     *
     * @return hugePageTlbSize
     */
    fun hugePageTlbSize(): Int

    /**
     * Sets the encoding type.
     *
     * This setting determines how to encode
     * the keys. See enum [EncodingType] for
     * the choices.
     *
     *
     * The value will determine how to encode keys
     * when writing to a new SST file. This value will be stored
     * inside the SST file which will be used when reading from
     * the file, which makes it possible for users to choose
     * different encoding type when reopening a DB. Files with
     * different encoding types can co-exist in the same DB and
     * can be read.
     *
     * @param encodingType [org.rocksdb.EncodingType] value.
     * @return the reference to the current config.
     */
    fun setEncodingType(encodingType: EncodingType): PlainTableConfig

    /**
     * Returns the active EncodingType
     *
     * @return currently set encoding type
     */
    fun encodingType(): EncodingType?

    /**
     * Set full scan mode, if true the whole file will be read
     * one record by one without using the index.
     *
     * @param fullScanMode boolean value indicating if full
     * scan mode shall be enabled.
     * @return the reference to the current config.
     */
    fun setFullScanMode(fullScanMode: Boolean): PlainTableConfig

    /**
     * Return if full scan mode is active
     * @return boolean value indicating if the full scan mode is
     * enabled.
     */
    fun fullScanMode(): Boolean

    /**
     *
     * If set to true: compute plain table index and bloom
     * filter during file building and store it in file.
     * When reading file, index will be mmaped instead
     * of doing recomputation.
     *
     * @param storeIndexInFile value indicating if index shall
     * be stored in a file
     * @return the reference to the current config.
     */
    fun setStoreIndexInFile(storeIndexInFile: Boolean): PlainTableConfig

    /**
     * Return a boolean value indicating if index shall be stored
     * in a file.
     *
     * @return currently set value for store index in file.
     */
    fun storeIndexInFile(): Boolean
}
