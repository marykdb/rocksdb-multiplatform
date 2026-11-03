package maryk.rocksdb

import kotlin.use

actual class CompactionJobInfo internal constructor(
    delegate: org.rocksdb.CompactionJobInfo
) {
    // The JNI object is borrowed for the callback, so copy every exposed field.
    private val columnFamilyNameValue = delegate.columnFamilyName().copyOf()
    private val baseInputLevelValue = delegate.baseInputLevel()
    private val outputLevelValue = delegate.outputLevel()
    private val inputFilesValue = delegate.inputFiles().toList()
    private val outputFilesValue = delegate.outputFiles().toList()
    private val compactionReasonValue = delegate.compactionReason()
    private val threadIdValue = delegate.threadId()
    private val jobIdValue = delegate.jobId()
    private val compressionValue = delegate.compression()
    private val stats: CompactionJobStats = delegate.stats()?.let { stats ->
        stats.use {
            CompactionJobStats(
                elapsedMicrosValue = it.elapsedMicros(),
                numInputRecordsValue = it.numInputRecords(),
                numOutputRecordsValue = it.numOutputRecords(),
                totalInputBytesValue = it.totalInputBytes(),
                totalOutputBytesValue = it.totalOutputBytes(),
                numInputFilesValue = it.numInputFiles(),
                numInputFilesAtOutputLevelValue = it.numInputFilesAtOutputLevel(),
                numOutputFilesValue = it.numOutputFiles(),
                numCorruptKeysValue = it.numCorruptKeys(),
                numRecordsReplacedValue = it.numRecordsReplaced(),
                totalInputRawKeyBytesValue = it.totalInputRawKeyBytes(),
                totalInputRawValueBytesValue = it.totalInputRawValueBytes(),
                numInputDeletionRecordsValue = it.numInputDeletionRecords(),
                numExpiredDeletionRecordsValue = it.numExpiredDeletionRecords(),
                fileWriteNanosValue = it.fileWriteNanos(),
                fileRangeSyncNanosValue = it.fileRangeSyncNanos(),
                fileFsyncNanosValue = it.fileFsyncNanos(),
                filePrepareWriteNanosValue = it.filePrepareWriteNanos(),
                numSingleDelFallthruValue = it.numSingleDelFallthru(),
                numSingleDelMismatchValue = it.numSingleDelMismatch(),
            )
        }
    } ?: CompactionJobStats.empty(
        numInputFilesFallback = inputFilesValue.size.toLong(),
        numOutputFilesFallback = outputFilesValue.size.toLong(),
    )

    actual fun columnFamilyName(): ByteArray = columnFamilyNameValue.copyOf()

    actual fun baseInputLevel(): Int = baseInputLevelValue

    actual fun outputLevel(): Int = outputLevelValue

    actual fun inputFiles(): List<String> = inputFilesValue.toList()

    actual fun outputFiles(): List<String> = outputFilesValue.toList()

    actual fun elapsedMicros(): Long = stats.elapsedMicros()

    actual fun numCorruptKeys(): Long = stats.numCorruptKeys()

    actual fun inputRecords(): Long = stats.numInputRecords()

    actual fun outputRecords(): Long = stats.numOutputRecords()

    actual fun totalInputBytes(): Long = stats.totalInputBytes()

    actual fun totalOutputBytes(): Long = stats.totalOutputBytes()

    actual fun compactionReason(): CompactionReason = compactionReasonValue

    actual fun numInputFiles(): Long = stats.numInputFiles()

    actual fun numInputFilesAtOutputLevel(): Long = stats.numInputFilesAtOutputLevel()

    actual fun threadId(): Long = threadIdValue

    actual fun jobId(): Int = jobIdValue

    actual fun compression(): CompressionType = compressionValue

    actual fun compactionStats(): CompactionJobStats = stats
}
