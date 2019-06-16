package maryk.rocksdb

/**
 * CompactionOptions are used in
 * {@link RocksDB#compactFiles(CompactionOptions, ColumnFamilyHandle, List, int, int, CompactionJobInfo)}
 * calls.
 */
expect class CompactionOptions() : RocksObject {
  /**
   * Get the compaction output compression type.
   *
   * See {@link #setCompression(CompressionType)}.
   *
   * @return the compression type.
   */
  fun compression(): CompressionType

  /**
   * Set the compaction output compression type.
   *
   * Default: snappy
   *
   * If set to {@link CompressionType#DISABLE_COMPRESSION_OPTION},
   * RocksDB will choose compression type according to the
   * {@link ColumnFamilyOptions#compressionType()}, taking into account
   * the output level if {@link ColumnFamilyOptions#compressionPerLevel()}
   * is specified.
   *
   * @param compression the compression type to use for compaction output.
   *
   * @return the instance of the current Options.
   */
  fun  setCompression(compression: CompressionType): CompactionOptions

  /**
   * Get the compaction output file size limit.
   *
   * See {@link #setOutputFileSizeLimit(long)}.
   *
   * @return the file size limit.
   */
  fun outputFileSizeLimit(): Long

  /**
   * Compaction will create files of size {@link #outputFileSizeLimit()}.
   *
   * Default: 2^64-1, which means that compaction will create a single file
   *
   * @param outputFileSizeLimit the size limit
   *
   * @return the instance of the current Options.
   */
  fun setOutputFileSizeLimit(outputFileSizeLimit:Long): CompactionOptions

  /**
   * Get the maximum number of threads that will concurrently perform a
   * compaction job.
   *
   * @return the maximum number of threads.
   */
  fun maxSubcompactions() : Int

  /**
   * This value represents the maximum number of threads that will
   * concurrently perform a compaction job by breaking it into multiple,
   * smaller ones that are run simultaneously.
   *
   * Default: 0 (i.e. no subcompactions)
   *
   * If > 0, it will replace the option in
   * {@link DBOptions#maxSubcompactions()} for this compaction.
   *
   * @param maxSubcompactions The maximum number of threads that will
   *     concurrently perform a compaction job
   *
   * @return the instance of the current Options.
   */
  fun setMaxSubcompactions(maxSubcompactions: Int): CompactionOptions
}
