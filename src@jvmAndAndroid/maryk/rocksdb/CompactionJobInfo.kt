package maryk.rocksdb

import kotlin.use

actual class CompactionJobInfo internal constructor(
    internal val delegate: org.rocksdb.CompactionJobInfo
) {
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
            )
        }
    } ?: CompactionJobStats.empty(
        numInputFilesFallback = delegate.inputFiles().size.toLong(),
        numOutputFilesFallback = delegate.outputFiles().size.toLong(),
    )

    actual fun columnFamilyName(): ByteArray = delegate.columnFamilyName()

    actual fun baseInputLevel(): Int = delegate.baseInputLevel()

    actual fun outputLevel(): Int = delegate.outputLevel()

    actual fun inputFiles(): List<String> = delegate.inputFiles()

    actual fun outputFiles(): List<String> = delegate.outputFiles()

    actual fun elapsedMicros(): Long = stats.elapsedMicros()

    actual fun numCorruptKeys(): Long = stats.numCorruptKeys()

    actual fun inputRecords(): Long = stats.numInputRecords()

    actual fun outputRecords(): Long = stats.numOutputRecords()

    actual fun totalInputBytes(): Long = stats.totalInputBytes()

    actual fun totalOutputBytes(): Long = stats.totalOutputBytes()

    actual fun compactionReason(): CompactionReason = delegate.compactionReason()

    actual fun numInputFiles(): Long = stats.numInputFiles()

    actual fun numInputFilesAtOutputLevel(): Long = stats.numInputFilesAtOutputLevel()

    actual fun compactionStats(): CompactionJobStats = stats
}
