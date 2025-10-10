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
import maryk.toByteArray
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
            rocksdb_compactionjobinfo_cf_name(native, length.ptr)!!
                .toByteArray(length.value)
        },
        baseInputLevelValue = rocksdb_compactionjobinfo_base_input_level(native),
        outputLevelValue = rocksdb_compactionjobinfo_output_level(native),
        inputFilesValue = collectPaths(native, ::rocksdb_compactionjobinfo_input_files_count, ::rocksdb_compactionjobinfo_input_file_at),
        outputFilesValue = collectPaths(native, ::rocksdb_compactionjobinfo_output_files_count, ::rocksdb_compactionjobinfo_output_file_at),
        elapsedMicrosValue = rocksdb_compactionjobinfo_elapsed_micros(native).toLong(),
        numCorruptKeysValue = rocksdb_compactionjobinfo_num_corrupt_keys(native).toLong(),
        inputRecordsValue = rocksdb_compactionjobinfo_input_records(native).toLong(),
        outputRecordsValue = rocksdb_compactionjobinfo_output_records(native).toLong(),
        totalInputBytesValue = rocksdb_compactionjobinfo_total_input_bytes(native).toLong(),
        totalOutputBytesValue = rocksdb_compactionjobinfo_total_output_bytes(native).toLong(),
        compactionReasonValue = compactionReasonFromValue(rocksdb_compactionjobinfo_compaction_reason(native)),
        numInputFilesValue = rocksdb_compactionjobinfo_num_input_files(native).toLong(),
        numInputFilesAtOutputLevelValue =
            rocksdb_compactionjobinfo_num_input_files_at_output_level(native).toLong(),
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
    val total = count(native).toInt()
    if (total == 0) return@buildList
    memScoped {
        val length = alloc<size_tVar>()
        repeat(total) { index ->
            val ptr = fetch(native, index.asSizeT(), length.ptr) ?: return@repeat
            add(ptr.toByteArray(length.value).decodeToString())
        }
    }
}
