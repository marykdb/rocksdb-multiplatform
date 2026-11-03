@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")
package maryk.rocksdb

actual typealias LogFile = org.rocksdb.LogFile

actual fun RocksDB.getSortedWalFiles(): List<LogFile> = getSortedWalFiles()
actual fun RocksDB.approximateSizes(ranges: List<Range>, vararg sizeApproximationFlags: SizeApproximationFlag): LongArray =
    approximateSizes(null, ranges, *sizeApproximationFlags)
actual fun RocksDB.approximateSizes(columnFamilyHandle: ColumnFamilyHandle?, ranges: List<Range>, vararg sizeApproximationFlags: SizeApproximationFlag): LongArray {
    require(sizeApproximationFlags.any { it != SizeApproximationFlag.NONE }) { "Select files and/or memtables for size estimation" }
    if (ranges.isEmpty()) return LongArray(0)
    return getApproximateSizes(columnFamilyHandle, ranges, *sizeApproximationFlags)
}
actual fun RocksDB.compactFiles(options: CompactionOptions, inputFileNames: List<String>, outputLevel: Int, outputPathId: Int): List<String> =
    compactFiles(options, inputFileNames, outputLevel, outputPathId, null)
actual fun RocksDB.compactFiles(options: CompactionOptions, columnFamilyHandle: ColumnFamilyHandle?, inputFileNames: List<String>, outputLevel: Int, outputPathId: Int): List<String> =
    compactFiles(options, columnFamilyHandle, inputFileNames, outputLevel, outputPathId, null)
actual fun RocksDB.approximateMemTableStats(range: Range, columnFamilyHandle: ColumnFamilyHandle?): MemTableStats =
    getApproximateMemTableStats(columnFamilyHandle, range).let { MemTableStats(it.count, it.size) }
actual fun RocksDB.tableProperties(columnFamilyHandle: ColumnFamilyHandle?): Map<String, TableProperties> =
    (if (columnFamilyHandle == null) getPropertiesOfAllTables() else getPropertiesOfAllTables(columnFamilyHandle))
        .mapValues { TableProperties(it.value) }
actual fun RocksDB.clipColumnFamily(columnFamilyHandle: ColumnFamilyHandle, begin: ByteArray, end: ByteArray) =
    clipColumnFamily(columnFamilyHandle, begin, end)
