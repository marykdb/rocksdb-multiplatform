package maryk.rocksdb

/**
 * Aggregated statistics captured for a single compaction job.
 */
class CompactionJobStats internal constructor(
    private val elapsedMicrosValue: Long,
    private val numInputRecordsValue: Long,
    private val numOutputRecordsValue: Long,
    private val totalInputBytesValue: Long,
    private val totalOutputBytesValue: Long,
    private val numInputFilesValue: Long,
    private val numInputFilesAtOutputLevelValue: Long,
    private val numOutputFilesValue: Long,
    private val numCorruptKeysValue: Long,
    private val numRecordsReplacedValue: Long = 0,
    private val totalInputRawKeyBytesValue: Long = 0,
    private val totalInputRawValueBytesValue: Long = 0,
    private val numInputDeletionRecordsValue: Long = 0,
    private val numExpiredDeletionRecordsValue: Long = 0,
    private val fileWriteNanosValue: Long = 0,
    private val fileRangeSyncNanosValue: Long = 0,
    private val fileFsyncNanosValue: Long = 0,
    private val filePrepareWriteNanosValue: Long = 0,
    private val numSingleDelFallthruValue: Long = 0,
    private val numSingleDelMismatchValue: Long = 0,

) {
    /** Duration of the compaction in microseconds. */
    fun elapsedMicros(): Long = elapsedMicrosValue

    /** Number of logical records consumed from inputs. */
    fun numInputRecords(): Long = numInputRecordsValue

    /** Number of logical records produced in the outputs. */
    fun numOutputRecords(): Long = numOutputRecordsValue

    /** Total size of input SST data processed. */
    fun totalInputBytes(): Long = totalInputBytesValue

    /** Total size of SST data generated as output. */
    fun totalOutputBytes(): Long = totalOutputBytesValue

    /** Number of input files processed across all levels. */
    fun numInputFiles(): Long = numInputFilesValue

    /** Number of input files that already lived at the target level. */
    fun numInputFilesAtOutputLevel(): Long = numInputFilesAtOutputLevelValue

    /** Number of output files generated. */
    fun numOutputFiles(): Long = numOutputFilesValue

    /** Corrupt keys encountered while compacting. */
    fun numCorruptKeys(): Long = numCorruptKeysValue

    fun numRecordsReplaced(): Long = numRecordsReplacedValue

    fun totalInputRawKeyBytes(): Long = totalInputRawKeyBytesValue

    fun totalInputRawValueBytes(): Long = totalInputRawValueBytesValue

    fun numInputDeletionRecords(): Long = numInputDeletionRecordsValue

    fun numExpiredDeletionRecords(): Long = numExpiredDeletionRecordsValue

    fun fileWriteNanos(): Long = fileWriteNanosValue

    fun fileRangeSyncNanos(): Long = fileRangeSyncNanosValue

    fun fileFsyncNanos(): Long = fileFsyncNanosValue

    fun filePrepareWriteNanos(): Long = filePrepareWriteNanosValue

    fun numSingleDelFallthru(): Long = numSingleDelFallthruValue

    fun numSingleDelMismatch(): Long = numSingleDelMismatchValue

    internal companion object {
        internal fun empty(
            numInputFilesFallback: Long,
            numOutputFilesFallback: Long,
        ) = CompactionJobStats(
            elapsedMicrosValue = 0,
            numInputRecordsValue = 0,
            numOutputRecordsValue = 0,
            totalInputBytesValue = 0,
            totalOutputBytesValue = 0,
            numInputFilesValue = numInputFilesFallback,
            numInputFilesAtOutputLevelValue = 0,
            numOutputFilesValue = numOutputFilesFallback,
            numCorruptKeysValue = 0,
        )
    }
}
