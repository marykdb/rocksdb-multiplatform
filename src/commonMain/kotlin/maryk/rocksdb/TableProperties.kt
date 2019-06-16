package maryk.rocksdb

/**
 * TableProperties contains read-only properties of its associated
 * table.
 */
expect class TableProperties {
    /** Get the total size of all data blocks. */
    fun getDataSize(): Long

    /** Get the size of index block. */
    fun getIndexSize(): Long

    /**
     * Get the total number of index partitions
     * if [IndexType.kTwoLevelIndexSearch] is used.
     *
     * @return the total number of index partitions.
     */
    fun getIndexPartitions(): Long

    /**
     * Size of the top-level index
     * if [IndexType.kTwoLevelIndexSearch] is used.
     *
     * @return the size of the top-level index.
     */
    fun getTopLevelIndexSize(): Long

    /**
     * Whether the index key is user key.
     * Otherwise it includes 8 byte of sequence
     * number added by internal key format.
     *
     * @return the index key
     */
    fun getIndexKeyIsUserKey(): Long

    /** Whether delta encoding is used to encode the index values. */
    fun getIndexValueIsDeltaEncoded(): Long

    /** Get the size of filter block. */
    fun getFilterSize(): Long

    /** Get the total raw key size. */
    fun getRawKeySize(): Long

    /** Get the total raw value size. */
    fun getRawValueSize(): Long

    /** Get the number of blocks in this table. */
    fun getNumDataBlocks(): Long

    /**
     * Get the number of entries in this table.
     * @return the number of entries in this table.
     */
    fun getNumEntries(): Long

    /**
     * Get the number of deletions in the table.
     * @return the number of deletions in the table.
     */
    fun getNumDeletions(): Long

    /**
     * Get the number of merge operands in the table.
     *
     * @return the number of merge operands in the table.
     */
    fun getNumMergeOperands(): Long

    /**
     * Get the number of range deletions in this table.
     * @return the number of range deletions in this table.
     */
    fun getNumRangeDeletions(): Long

    /**
     * Get the format version, reserved for backward compatibility.
     *
     * @return the format version.
     */
    fun getFormatVersion(): Long

    /**
     * Get the length of the keys.
     *
     * @return 0 when the key is variable length, otherwise number of
     * bytes for each key.
     */
    fun getFixedKeyLen(): Long

    /**
     * Get the ID of column family for this SST file,
     * corresponding to the column family identified by
     * [.getColumnFamilyName].
     *
     * @return the id of the column family.
     */
    fun getColumnFamilyId(): Long

    /**
     * The time when the SST file was created.
     * Since SST files are immutable, this is equivalent
     * to last modified time.
     *
     * @return the created time.
     */
    fun getCreationTime(): Long

    /**
     * Get the timestamp of the earliest key.
     * @return 0 means unknown, otherwise the timestamp.
     */
    fun getOldestKeyTime(): Long

    /**
     * Get the name of the column family with which this
     * SST file is associated.
     *
     * @return the name of the column family, or null if the
     * column family is unknown.
     */
    fun getColumnFamilyName(): ByteArray?

    /**
     * Get the name of the filter policy used in this table.
     *
     * @return the name of the filter policy, or null if
     * no filter policy is used.
     */
    fun getFilterPolicyName(): String?

    /**
     * Get the name of the comparator used in this table.
     */
    fun getComparatorName(): String

    /**
     * Get the name of the merge operator used in this table.
     *
     * @return the name of the merge operator, or null if no merge operator
     * is used.
     */
    fun getMergeOperatorName(): String?

    /**
     * Get the name of the prefix extractor used in this table.
     *
     * @return the name of the prefix extractor, or null if no prefix
     * extractor is used.
     */
    fun getPrefixExtractorName(): String?

    /**
     * Get the names of the property collectors factories used in this table.
     *
     * @return the names of the property collector factories separated
     * by commas, e.g. {collector_name[1]},{collector_name[2]},...
     */
    fun getPropertyCollectorsNames(): String

    /** Get the name of the compression algorithm used to compress the SST files. */
    fun getCompressionName(): String

    /** Get the user collected properties. */
    fun getUserCollectedProperties(): Map<String, String>

    /** Get the readable properties. */
    fun getReadableProperties(): Map<String, String>

    /** The offset of the value of each property in the file. */
    fun getPropertiesOffsets(): Map<String, Long>
}
