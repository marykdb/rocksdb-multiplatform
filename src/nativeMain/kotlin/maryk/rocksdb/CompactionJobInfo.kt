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
import rocksdb.rocksdb_compaction_job_stats_file_fsync_nanos
import rocksdb.rocksdb_compaction_job_stats_file_prepare_write_nanos
import rocksdb.rocksdb_compaction_job_stats_file_range_sync_nanos
import rocksdb.rocksdb_compaction_job_stats_file_write_nanos
import rocksdb.rocksdb_compaction_job_stats_num_expired_deletion_records
import rocksdb.rocksdb_compaction_job_stats_num_input_deletion_records
import rocksdb.rocksdb_compaction_job_stats_num_output_files
import rocksdb.rocksdb_compaction_job_stats_num_records_replaced
import rocksdb.rocksdb_compaction_job_stats_num_single_del_fallthru
import rocksdb.rocksdb_compaction_job_stats_num_single_del_mismatch
import rocksdb.rocksdb_compaction_job_stats_total_input_raw_key_bytes
import rocksdb.rocksdb_compaction_job_stats_total_input_raw_value_bytes
import rocksdb.rocksdb_compactionjobinfo_base_input_level
import rocksdb.rocksdb_compactionjobinfo_cf_name
import rocksdb.rocksdb_compactionjobinfo_compaction_reason
import rocksdb.rocksdb_compactionjobinfo_compression
import rocksdb.rocksdb_compactionjobinfo_elapsed_micros
import rocksdb.rocksdb_compactionjobinfo_input_file_at
import rocksdb.rocksdb_compactionjobinfo_input_files_count
import rocksdb.rocksdb_compactionjobinfo_input_records
import rocksdb.rocksdb_compactionjobinfo_job_id
import rocksdb.rocksdb_compactionjobinfo_num_corrupt_keys
import rocksdb.rocksdb_compactionjobinfo_num_input_files
import rocksdb.rocksdb_compactionjobinfo_num_input_files_at_output_level
import rocksdb.rocksdb_compactionjobinfo_output_file_at
import rocksdb.rocksdb_compactionjobinfo_output_files_count
import rocksdb.rocksdb_compactionjobinfo_output_level
import rocksdb.rocksdb_compactionjobinfo_output_records
import rocksdb.rocksdb_compactionjobinfo_stats
import rocksdb.rocksdb_compactionjobinfo_thread_id
import rocksdb.rocksdb_compactionjobinfo_total_input_bytes
import rocksdb.rocksdb_compactionjobinfo_total_output_bytes

actual class CompactionJobInfo internal constructor(native: CPointer<rocksdb_compactionjobinfo_t>) {
    private val columnFamilyNameValue = memScoped {
        val length = alloc<size_tVar>()
        requireNotNull(rocksdb_compactionjobinfo_cf_name(native, length.ptr)) {
            "RocksDB returned null compaction column family name"
        }.toByteArray(length.value)
    }
    private val baseInputLevelValue = rocksdb_compactionjobinfo_base_input_level(native)
    private val outputLevelValue = rocksdb_compactionjobinfo_output_level(native)
    private val inputFilesValue = collectPaths(native, ::rocksdb_compactionjobinfo_input_files_count, ::rocksdb_compactionjobinfo_input_file_at)
    private val outputFilesValue = collectPaths(native, ::rocksdb_compactionjobinfo_output_files_count, ::rocksdb_compactionjobinfo_output_file_at)
    private val compactionReasonValue = compactionReasonFromValue(rocksdb_compactionjobinfo_compaction_reason(native))
    private val threadIdValue = rocksdb_compactionjobinfo_thread_id(native).toCheckedLong("compaction thread id")
    private val jobIdValue = rocksdb_compactionjobinfo_job_id(native)
    private val compressionValue = getCompressionType(rocksdb_compactionjobinfo_compression(native).toByte())
    private val statsValue = memScoped {
        val stats = requireNotNull(rocksdb_compactionjobinfo_stats(native)) { "RocksDB returned null compaction statistics" }
        CompactionJobStats(
            elapsedMicrosValue = rocksdb_compactionjobinfo_elapsed_micros(native).toCheckedLong("compaction elapsed micros"),
            numInputRecordsValue = rocksdb_compactionjobinfo_input_records(native).toCheckedLong("compaction input record count"),
            numOutputRecordsValue = rocksdb_compactionjobinfo_output_records(native).toCheckedLong("compaction output record count"),
            totalInputBytesValue = rocksdb_compactionjobinfo_total_input_bytes(native).toCheckedLong("compaction input bytes"),
            totalOutputBytesValue = rocksdb_compactionjobinfo_total_output_bytes(native).toCheckedLong("compaction output bytes"),
            numInputFilesValue = sizeTToLong(rocksdb_compactionjobinfo_num_input_files(native), "compaction input files"),
            numInputFilesAtOutputLevelValue = sizeTToLong(rocksdb_compactionjobinfo_num_input_files_at_output_level(native), "compaction output-level input files"),
            numOutputFilesValue = sizeTToLong(rocksdb_compaction_job_stats_num_output_files(stats), "compaction output files"),
            numCorruptKeysValue = rocksdb_compactionjobinfo_num_corrupt_keys(native).toCheckedLong("compaction corrupt keys"),
            numRecordsReplacedValue = rocksdb_compaction_job_stats_num_records_replaced(stats).toCheckedLong("compaction numRecordsReplaced"),
            totalInputRawKeyBytesValue = rocksdb_compaction_job_stats_total_input_raw_key_bytes(stats).toCheckedLong("compaction totalInputRawKeyBytes"),
            totalInputRawValueBytesValue = rocksdb_compaction_job_stats_total_input_raw_value_bytes(stats).toCheckedLong("compaction totalInputRawValueBytes"),
            numInputDeletionRecordsValue = rocksdb_compaction_job_stats_num_input_deletion_records(stats).toCheckedLong("compaction numInputDeletionRecords"),
            numExpiredDeletionRecordsValue = rocksdb_compaction_job_stats_num_expired_deletion_records(stats).toCheckedLong("compaction numExpiredDeletionRecords"),
            fileWriteNanosValue = rocksdb_compaction_job_stats_file_write_nanos(stats).toCheckedLong("compaction fileWriteNanos"),
            fileRangeSyncNanosValue = rocksdb_compaction_job_stats_file_range_sync_nanos(stats).toCheckedLong("compaction fileRangeSyncNanos"),
            fileFsyncNanosValue = rocksdb_compaction_job_stats_file_fsync_nanos(stats).toCheckedLong("compaction fileFsyncNanos"),
            filePrepareWriteNanosValue = rocksdb_compaction_job_stats_file_prepare_write_nanos(stats).toCheckedLong("compaction filePrepareWriteNanos"),
            numSingleDelFallthruValue = rocksdb_compaction_job_stats_num_single_del_fallthru(stats).toCheckedLong("compaction numSingleDelFallthru"),
            numSingleDelMismatchValue = rocksdb_compaction_job_stats_num_single_del_mismatch(stats).toCheckedLong("compaction numSingleDelMismatch"),
        )
    }

    actual fun columnFamilyName(): ByteArray = columnFamilyNameValue.copyOf()
    actual fun baseInputLevel(): Int = baseInputLevelValue
    actual fun outputLevel(): Int = outputLevelValue
    actual fun inputFiles(): List<String> = inputFilesValue.toList()
    actual fun outputFiles(): List<String> = outputFilesValue.toList()
    actual fun elapsedMicros(): Long = statsValue.elapsedMicros()
    actual fun numCorruptKeys(): Long = statsValue.numCorruptKeys()
    actual fun inputRecords(): Long = statsValue.numInputRecords()
    actual fun outputRecords(): Long = statsValue.numOutputRecords()
    actual fun totalInputBytes(): Long = statsValue.totalInputBytes()
    actual fun totalOutputBytes(): Long = statsValue.totalOutputBytes()
    actual fun compactionReason(): CompactionReason = compactionReasonValue
    actual fun numInputFiles(): Long = statsValue.numInputFiles()
    actual fun numInputFilesAtOutputLevel(): Long = statsValue.numInputFilesAtOutputLevel()
    actual fun threadId(): Long = threadIdValue
    actual fun jobId(): Int = jobIdValue
    actual fun compression(): CompressionType = compressionValue
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
