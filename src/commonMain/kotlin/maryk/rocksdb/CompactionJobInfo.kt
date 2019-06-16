package maryk.rocksdb

expect class CompactionJobInfo() : RocksObject {
    /** Get the name of the column family where the compaction happened. */
    fun columnFamilyName(): ByteArray

    /** Get the status indicating whether the compaction was successful or not. */
    fun status(): Status

    /**  Get the id of the thread that completed this compaction job. */
    fun threadId(): Long

    /** Get the job id, which is unique in the same thread. */
    fun jobId(): Int

    /**  Get the smallest input level of the compaction. */
    fun baseInputLevel(): Int

    /** Get the output level of the compaction. */
    fun outputLevel(): Int

    /** Get the names of the compaction input files. */
    fun inputFiles(): List<String>

    /** Get the names of the compaction output files. */
    fun outputFiles(): List<String>

    /**
     * Get the table properties for the input and output tables.
     *
     * The map is keyed by values from [.inputFiles] and
     * [.outputFiles].
     *
     * @return the table properties
     */
    fun tableProperties(): Map<String, TableProperties>

    /**
     * Get the Reason for running the compaction.
     *
     * @return the reason.
     */
    fun compactionReason(): CompactionReason

    /**
     * Get the compression algorithm used for output files.
     *
     * @return the compression algorithm
     */
    fun compression(): CompressionType

    /** Get detailed information about this compaction, or null if not available. */
    fun stats(): CompactionJobStats?
}
