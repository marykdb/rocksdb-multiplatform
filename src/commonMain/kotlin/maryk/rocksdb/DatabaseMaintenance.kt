package maryk.rocksdb

/** Approximate records and bytes held in memtables for a half-open key range. */
data class MemTableStats(val count: Long, val size: Long)

/** A copied WAL file descriptor. It remains valid after closing the database. */
expect class LogFile {
    fun pathName(): String
    fun logNumber(): Long
    fun type(): WalFileType
    fun startSequence(): Long
    fun sizeFileBytes(): Long
}


/** Copies the sorted WAL inventory. WAL retention options determine which logs survive. */
expect fun RocksDB.getSortedWalFiles(): List<LogFile>

/** Estimates bytes in each [start, limit) range. Select at least one of files or memtables. */
expect fun RocksDB.approximateSizes(
    ranges: List<Range>, vararg sizeApproximationFlags: SizeApproximationFlag,
): LongArray
expect fun RocksDB.approximateSizes(
    columnFamilyHandle: ColumnFamilyHandle?, ranges: List<Range>,
    vararg sizeApproximationFlags: SizeApproximationFlag,
): LongArray

/** Compacts named SST files synchronously on the calling thread and returns output paths. */
expect fun RocksDB.compactFiles(
    options: CompactionOptions, inputFileNames: List<String>, outputLevel: Int, outputPathId: Int = -1,
): List<String>
expect fun RocksDB.compactFiles(
    options: CompactionOptions, columnFamilyHandle: ColumnFamilyHandle?,
    inputFileNames: List<String>, outputLevel: Int, outputPathId: Int = -1,
): List<String>

/** Estimates memtable records and bytes. Keep the range's slices alive for this call. */
expect fun RocksDB.approximateMemTableStats(range: Range, columnFamilyHandle: ColumnFamilyHandle? = null): MemTableStats

/** Copies per-SST properties. Returned snapshots remain valid after database close. */
expect fun RocksDB.tableProperties(columnFamilyHandle: ColumnFamilyHandle? = null): Map<String, TableProperties>

/** Removes keys outside [begin, end) in this CF. Do not use with active snapshots. */
expect fun RocksDB.clipColumnFamily(columnFamilyHandle: ColumnFamilyHandle, begin: ByteArray, end: ByteArray)
