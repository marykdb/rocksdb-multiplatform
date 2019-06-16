package maryk.rocksdb

/** Options for FIFO Compaction*/
expect class CompactionOptionsFIFO() : RocksObject {
    /**
     * Once the total sum of table files reaches this, we will delete the oldest
     * table file
     *
     * Default: 1GB
     *
     * @param maxTableFilesSize The maximum size of the table files
     *
     * @return the reference to the current options.
     */
    fun setMaxTableFilesSize(
        maxTableFilesSize: Long
    ): CompactionOptionsFIFO

    /**
     * Once the total sum of table files reaches this, we will delete the oldest
     * table file
     *
     * Default: 1GB
     *
     * @return max table file size in bytes
     */
    fun maxTableFilesSize(): Long

    /**
     * If true, try to do compaction to compact smaller files into larger ones.
     * Minimum files to compact follows options.level0_file_num_compaction_trigger
     * and compaction won't trigger if average compact bytes per del file is
     * larger than options.write_buffer_size. This is to protect large files
     * from being compacted again.
     *
     * Default: false
     *
     * @param allowCompaction true to allow intra-L0 compaction
     *
     * @return the reference to the current options.
     */
    fun setAllowCompaction(
        allowCompaction: Boolean
    ): CompactionOptionsFIFO

    /**
     * Check if intra-L0 compaction is enabled.
     * When enabled, we try to compact smaller files into larger ones.
     *
     * See [.setAllowCompaction].
     *
     * Default: false
     *
     * @return true if intra-L0 compaction is enabled, false otherwise.
     */
    fun allowCompaction(): Boolean
}
