package maryk.rocksdb

/** Options for Universal Compaction */
expect class CompactionOptionsUniversal() : RocksObject {
    /**
     * Percentage flexibility while comparing file size. If the candidate file(s)
     * size is 1% smaller than the next file's size, then include next file into
     * this candidate set.
     *
     * Default: 1
     *
     * @param sizeRatio The size ratio to use
     *
     * @return the reference to the current options.
     */
    fun setSizeRatio(sizeRatio: Int): CompactionOptionsUniversal

    /**
     * Percentage flexibility while comparing file size. If the candidate file(s)
     * size is 1% smaller than the next file's size, then include next file into
     * this candidate set.
     *
     * Default: 1
     *
     * @return The size ratio in use
     */
    fun sizeRatio(): Int

    /**
     * The minimum number of files in a single compaction run.
     *
     * Default: 2
     *
     * @param minMergeWidth minimum number of files in a single compaction run
     *
     * @return the reference to the current options.
     */
    fun setMinMergeWidth(minMergeWidth: Int): CompactionOptionsUniversal

    /**
     * The minimum number of files in a single compaction run.
     *
     * Default: 2
     *
     * @return minimum number of files in a single compaction run
     */
    fun minMergeWidth(): Int

    /**
     * The maximum number of files in a single compaction run.
     *
     * Default: [Long.MAX_VALUE]
     *
     * @param maxMergeWidth maximum number of files in a single compaction run
     *
     * @return the reference to the current options.
     */
    fun setMaxMergeWidth(maxMergeWidth: Int): CompactionOptionsUniversal

    /**
     * The maximum number of files in a single compaction run.
     *
     * Default: [Long.MAX_VALUE]
     *
     * @return maximum number of files in a single compaction run
     */
    fun maxMergeWidth(): Int

    /**
     * The size amplification is defined as the amount (in percentage) of
     * additional storage needed to store a single byte of data in the database.
     * For example, a size amplification of 2% means that a database that
     * contains 100 bytes of user-data may occupy upto 102 bytes of
     * physical storage. By this definition, a fully compacted database has
     * a size amplification of 0%. Rocksdb uses the following heuristic
     * to calculate size amplification: it assumes that all files excluding
     * the earliest file contribute to the size amplification.
     *
     * Default: 200, which means that a 100 byte database could require upto
     * 300 bytes of storage.
     *
     * @param maxSizeAmplificationPercent the amount of additional storage needed
     * (as a percentage) to store a single byte in the database
     *
     * @return the reference to the current options.
     */
    fun setMaxSizeAmplificationPercent(
        maxSizeAmplificationPercent: Int
    ): CompactionOptionsUniversal

    /**
     * The size amplification is defined as the amount (in percentage) of
     * additional storage needed to store a single byte of data in the database.
     * For example, a size amplification of 2% means that a database that
     * contains 100 bytes of user-data may occupy upto 102 bytes of
     * physical storage. By this definition, a fully compacted database has
     * a size amplification of 0%. Rocksdb uses the following heuristic
     * to calculate size amplification: it assumes that all files excluding
     * the earliest file contribute to the size amplification.
     *
     * Default: 200, which means that a 100 byte database could require upto
     * 300 bytes of storage.
     *
     * @return the amount of additional storage needed (as a percentage) to store
     * a single byte in the database
     */
    fun maxSizeAmplificationPercent(): Int

    /**
     * If this option is set to be -1 (the default value), all the output files
     * will follow compression type specified.
     *
     * If this option is not negative, we will try to make sure compressed
     * size is just above this value. In normal cases, at least this percentage
     * of data will be compressed.
     *
     * When we are compacting to a new file, here is the criteria whether
     * it needs to be compressed: assuming here are the list of files sorted
     * by generation time:
     * A1...An B1...Bm C1...Ct
     * where A1 is the newest and Ct is the oldest, and we are going to compact
     * B1...Bm, we calculate the total size of all the files as total_size, as
     * well as  the total size of C1...Ct as total_C, the compaction output file
     * will be compressed iff
     * total_C / total_size < this percentage
     *
     * Default: -1
     *
     * @param compressionSizePercent percentage of size for compression
     *
     * @return the reference to the current options.
     */
    fun setCompressionSizePercent(
        compressionSizePercent: Int
    ): CompactionOptionsUniversal

    /**
     * If this option is set to be -1 (the default value), all the output files
     * will follow compression type specified.
     *
     * If this option is not negative, we will try to make sure compressed
     * size is just above this value. In normal cases, at least this percentage
     * of data will be compressed.
     *
     * When we are compacting to a new file, here is the criteria whether
     * it needs to be compressed: assuming here are the list of files sorted
     * by generation time:
     * A1...An B1...Bm C1...Ct
     * where A1 is the newest and Ct is the oldest, and we are going to compact
     * B1...Bm, we calculate the total size of all the files as total_size, as
     * well as  the total size of C1...Ct as total_C, the compaction output file
     * will be compressed iff
     * total_C / total_size < this percentage
     *
     * Default: -1
     *
     * @return percentage of size for compression
     */
    fun compressionSizePercent(): Int

    /**
     * The algorithm used to stop picking files into a single compaction run
     *
     * Default: [CompactionStopStyle.CompactionStopStyleTotalSize]
     *
     * @param compactionStopStyle The compaction algorithm
     *
     * @return the reference to the current options.
     */
    fun setStopStyle(
        compactionStopStyle: CompactionStopStyle
    ): CompactionOptionsUniversal

    /**
     * The algorithm used to stop picking files into a single compaction run
     *
     * Default: [CompactionStopStyle.CompactionStopStyleTotalSize]
     *
     * @return The compaction algorithm
     */
    fun stopStyle(): CompactionStopStyle

    /**
     * Option to optimize the universal multi level compaction by enabling
     * trivial move for non overlapping files.
     *
     * Default: false
     *
     * @param allowTrivialMove true if trivial move is allowed
     *
     * @return the reference to the current options.
     */
    fun setAllowTrivialMove(
        allowTrivialMove: Boolean
    ): CompactionOptionsUniversal

    /**
     * Option to optimize the universal multi level compaction by enabling
     * trivial move for non overlapping files.
     *
     * Default: false
     *
     * @return true if trivial move is allowed
     */
    fun allowTrivialMove(): Boolean
}
