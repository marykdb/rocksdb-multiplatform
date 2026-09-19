@file:OptIn(UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_compactionjobinfo_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.asSizeT
import maryk.sizeTToInt
import maryk.sizeTToLong
import maryk.toByteArray
import maryk.toCheckedLong
import platform.posix.size_t
import platform.posix.size_tVar
import rocksdb.rocksdb_compactionjobinfo_base_input_level
import rocksdb.rocksdb_compactionjobinfo_compaction_reason
import rocksdb.rocksdb_compactionjobinfo_elapsed_micros
import rocksdb.rocksdb_compactionjobinfo_input_file_at
import rocksdb.rocksdb_compactionjobinfo_input_files_count
import rocksdb.rocksdb_compactionjobinfo_input_records
import rocksdb.rocksdb_compactionjobinfo_num_corrupt_keys
import rocksdb.rocksdb_compactionjobinfo_num_input_files
import rocksdb.rocksdb_compactionjobinfo_num_input_files_at_output_level
import rocksdb.rocksdb_compactionjobinfo_output_file_at
import rocksdb.rocksdb_compactionjobinfo_output_files_count
import rocksdb.rocksdb_compactionjobinfo_output_level
import rocksdb.rocksdb_compactionjobinfo_output_records
import rocksdb.rocksdb_compactionjobinfo_total_input_bytes
import rocksdb.rocksdb_compactionjobinfo_total_output_bytes
import rocksdb.rocksdb_compactionjobinfo_cf_name

actual class CompactionJobInfo internal constructor(
    private val columnFamilyNameValue: ByteArray,
    private val baseInputLevelValue: Int,
    private val outputLevelValue: Int,
    private val inputFilesValue: List<String>,
    private val outputFilesValue: List<String>,
    elapsedMicrosValue: Long,
    numCorruptKeysValue: Long,
    inputRecordsValue: Long,
    outputRecordsValue: Long,
    totalInputBytesValue: Long,
    totalOutputBytesValue: Long,
    private val compactionReasonValue: CompactionReason,
    numInputFilesValue: Long,
    numInputFilesAtOutputLevelValue: Long,
) {
    private val statsValue = CompactionJobStats(
        elapsedMicrosValue = elapsedMicrosValue,
        numInputRecordsValue = inputRecordsValue,
        numOutputRecordsValue = outputRecordsValue,
        totalInputBytesValue = totalInputBytesValue,
        totalOutputBytesValue = totalOutputBytesValue,
        numInputFilesValue = numInputFilesValue,
        numInputFilesAtOutputLevelValue = numInputFilesAtOutputLevelValue,
        numOutputFilesValue = outputFilesValue.size.toLong(),
        numCorruptKeysValue = numCorruptKeysValue,
    )

    internal constructor(native: CPointer<rocksdb_compactionjobinfo_t>) : this(
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            requireNotNull(rocksdb_compactionjobinfo_cf_name(native, length.ptr)) {
                "RocksDB returned null compaction column family name"
            }
                .toByteArray(length.value)
        },
        baseInputLevelValue = rocksdb_compactionjobinfo_base_input_level(native),
        outputLevelValue = rocksdb_compactionjobinfo_output_level(native),
        inputFilesValue = collectPaths(native, ::rocksdb_compactionjobinfo_input_files_count, ::rocksdb_compactionjobinfo_input_file_at),
        outputFilesValue = collectPaths(native, ::rocksdb_compactionjobinfo_output_files_count, ::rocksdb_compactionjobinfo_output_file_at),
        elapsedMicrosValue = rocksdb_compactionjobinfo_elapsed_micros(native).toCheckedLong("compaction elapsed micros"),
        numCorruptKeysValue = rocksdb_compactionjobinfo_num_corrupt_keys(native).toCheckedLong("compaction corrupt key count"),
        inputRecordsValue = rocksdb_compactionjobinfo_input_records(native).toCheckedLong("compaction input record count"),
        outputRecordsValue = rocksdb_compactionjobinfo_output_records(native).toCheckedLong("compaction output record count"),
        totalInputBytesValue = rocksdb_compactionjobinfo_total_input_bytes(native).toCheckedLong("compaction total input bytes"),
        totalOutputBytesValue = rocksdb_compactionjobinfo_total_output_bytes(native).toCheckedLong("compaction total output bytes"),
        compactionReasonValue = compactionReasonFromValue(rocksdb_compactionjobinfo_compaction_reason(native)),
        numInputFilesValue = sizeTToLong(rocksdb_compactionjobinfo_num_input_files(native), "compaction input file count"),
        numInputFilesAtOutputLevelValue =
            sizeTToLong(rocksdb_compactionjobinfo_num_input_files_at_output_level(native), "compaction output-level input file count"),
    )

    actual fun columnFamilyName(): ByteArray = columnFamilyNameValue

    actual fun baseInputLevel(): Int = baseInputLevelValue

    actual fun outputLevel(): Int = outputLevelValue

    actual fun inputFiles(): List<String> = inputFilesValue

    actual fun outputFiles(): List<String> = outputFilesValue

    actual fun elapsedMicros(): Long = statsValue.elapsedMicros()

    actual fun numCorruptKeys(): Long = statsValue.numCorruptKeys()

    actual fun inputRecords(): Long = statsValue.numInputRecords()

    actual fun outputRecords(): Long = statsValue.numOutputRecords()

    actual fun totalInputBytes(): Long = statsValue.totalInputBytes()

    actual fun totalOutputBytes(): Long = statsValue.totalOutputBytes()

    actual fun compactionReason(): CompactionReason = compactionReasonValue

    actual fun numInputFiles(): Long = statsValue.numInputFiles()

    actual fun numInputFilesAtOutputLevel(): Long = statsValue.numInputFilesAtOutputLevel()

    actual fun compactionStats(): CompactionJobStats = statsValue
}

private fun collectPaths(
    native: CPointer<rocksdb_compactionjobinfo_t>,
    count: (CPointer<rocksdb_compactionjobinfo_t>) -> size_t,
    fetch: (CPointer<rocksdb_compactionjobinfo_t>, size_t, CPointer<size_tVar>) -> CPointer<ByteVar>?
): List<String> = buildList {
    val total = sizeTToInt(count(native), "compaction job file count")
    if (total == 0) return@buildList
    memScoped {
        val length = alloc<size_tVar>()
        repeat(total) { index ->
            val ptr = requireNotNull(fetch(native, index.asSizeT(), length.ptr)) {
                "RocksDB returned null compaction file path at index $index"
            }
            add(ptr.toByteArray(length.value).decodeToString())
        }
    }
}
