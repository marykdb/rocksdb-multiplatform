package maryk.rocksdb

/** Why RocksDB created an SST file. */
enum class TableFileCreationReason { FLUSH, COMPACTION, RECOVERY, MISC }

/** Copied event metadata that remains valid after its callback returns. */
open class TableFileCreationBriefInfo internal constructor(
    private val dbNameValue: String,
    private val columnFamilyNameValue: String,
    private val filePathValue: String,
    private val jobIdValue: Int,
    private val reasonValue: TableFileCreationReason,
) {
    fun dbName(): String = dbNameValue
    fun columnFamilyName(): String = columnFamilyNameValue
    fun filePath(): String = filePathValue
    fun jobId(): Int = jobIdValue
    fun reason(): TableFileCreationReason = reasonValue
}

class TableFileCreationInfo internal constructor(
    dbName: String, columnFamilyName: String, filePath: String, jobId: Int,
    reason: TableFileCreationReason,
    private val fileSizeValue: Long,
    private val tablePropertiesValue: TableProperties,
    private val statusValue: Status,
) : TableFileCreationBriefInfo(dbName, columnFamilyName, filePath, jobId, reason) {
    fun fileSize(): Long = fileSizeValue
    fun tableProperties(): TableProperties = tablePropertiesValue
    fun status(): Status = statusValue
}

class TableFileDeletionInfo internal constructor(
    private val dbNameValue: String,
    private val filePathValue: String,
    private val jobIdValue: Int,
    private val statusValue: Status,
) {
    fun dbName(): String = dbNameValue
    fun filePath(): String = filePathValue
    fun jobId(): Int = jobIdValue
    fun status(): Status = statusValue
}

/**
 * File-I/O timing uses nanoseconds, matching Java's FileOperationInfo.
 * Offset/length are meaningful for read/write/range-sync only; other operations return zero.
 */
class FileOperationInfo internal constructor(
    private val pathValue: String,
    private val offsetValue: Long,
    private val lengthValue: Long,
    private val startTimestampValue: Long,
    private val durationValue: Long,
    private val statusValue: Status,
) {
    fun path(): String = pathValue
    fun offset(): Long = offsetValue
    fun length(): Long = lengthValue
    fun startTimestamp(): Long = startTimestampValue
    fun duration(): Long = durationValue
    fun status(): Status = statusValue
}
