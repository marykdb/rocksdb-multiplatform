package maryk.rocksdb

import cnames.structs.rocksdb_flushjobinfo_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toBoolean
import maryk.toByteArray
import platform.posix.size_tVar
import rocksdb.rocksdb_flushjobinfo_cf_id
import rocksdb.rocksdb_flushjobinfo_cf_name
import rocksdb.rocksdb_flushjobinfo_file_path
import rocksdb.rocksdb_flushjobinfo_flush_reason
import rocksdb.rocksdb_flushjobinfo_job_id
import rocksdb.rocksdb_flushjobinfo_largest_seqno
import rocksdb.rocksdb_flushjobinfo_smallest_seqno
import rocksdb.rocksdb_flushjobinfo_table_properties
import rocksdb.rocksdb_flushjobinfo_thread_id
import rocksdb.rocksdb_flushjobinfo_triggered_writes_slowdown
import rocksdb.rocksdb_flushjobinfo_triggered_writes_stop

actual class FlushJobInfo internal constructor(
    private val columnFamilyIdValue: Long,
    private val columnFamilyNameValue: String,
    private val filePathValue: String,
    private val threadIdValue: Long,
    private val jobIdValue: Int,
    private val triggeredWritesSlowdownValue: Boolean,
    private val triggeredWritesStopValue: Boolean,
    private val smallestSeqnoValue: Long,
    private val largestSeqnoValue: Long,
    private val tablePropertiesValue: TableProperties,
    private val flushReasonValue: FlushReason,
) {
    internal constructor(native: CPointer<rocksdb_flushjobinfo_t>) : this(
        columnFamilyIdValue = rocksdb_flushjobinfo_cf_id(native).toLong(),
        columnFamilyNameValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_flushjobinfo_cf_name(native, length.ptr)!!
                .toByteArray(length.value)
                .decodeToString()
        },
        filePathValue = memScoped {
            val length = alloc<size_tVar>()
            rocksdb_flushjobinfo_file_path(native, length.ptr)!!
                .toByteArray(length.value)
                .decodeToString()
        },
        threadIdValue = rocksdb_flushjobinfo_thread_id(native).toLong(),
        jobIdValue = rocksdb_flushjobinfo_job_id(native),
        triggeredWritesSlowdownValue =
            rocksdb_flushjobinfo_triggered_writes_slowdown(native).toBoolean(),
        triggeredWritesStopValue =
            rocksdb_flushjobinfo_triggered_writes_stop(native).toBoolean(),
        smallestSeqnoValue = rocksdb_flushjobinfo_smallest_seqno(native).toLong(),
        largestSeqnoValue = rocksdb_flushjobinfo_largest_seqno(native).toLong(),
        tablePropertiesValue = TableProperties(
            rocksdb_flushjobinfo_table_properties(native)!!
        ),
        flushReasonValue = flushReasonFromValue(
            rocksdb_flushjobinfo_flush_reason(native).toByte()
        ),
    )

    actual fun columnFamilyId(): Long = columnFamilyIdValue

    actual fun columnFamilyName(): String = columnFamilyNameValue

    actual fun filePath(): String = filePathValue

    actual fun threadId(): Long = threadIdValue

    actual fun jobId(): Int = jobIdValue

    actual fun triggeredWritesSlowdown(): Boolean = triggeredWritesSlowdownValue

    actual fun triggeredWritesStop(): Boolean = triggeredWritesStopValue

    actual fun smallestSeqno(): Long = smallestSeqnoValue

    actual fun largestSeqno(): Long = largestSeqnoValue

    actual fun tableProperties(): TableProperties = tablePropertiesValue

    actual fun flushReason(): FlushReason = flushReasonValue
}
