@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)
package maryk.rocksdb

import cnames.structs.rocksdb_table_properties_collection_t
import kotlinx.cinterop.*
import maryk.asSizeT
import maryk.sizeTToInt
import maryk.toCheckedLong
import maryk.toByteArray
import maryk.usePointers
import maryk.wrapWithNullErrorThrower
import maryk.wrapWithErrorThrower
import platform.posix.size_tVar
import platform.posix.uint64_tVar
import rocksdb.*

actual class LogFile internal constructor(
    private val path: String, private val number: Long, private val kind: WalFileType,
    private val sequence: Long, private val bytes: Long,
) {
    actual fun pathName(): String = path
    actual fun logNumber(): Long = number
    actual fun type(): WalFileType = kind
    actual fun startSequence(): Long = sequence
    actual fun sizeFileBytes(): Long = bytes
}

actual fun RocksDB.getSortedWalFiles(): List<LogFile> {
    checkOwningHandle()
    val files = requireNotNull(wrapWithNullErrorThrower { rocksdb_get_sorted_wal_files(native, it) })
    return try {
        List(sizeTToInt(rocksdb_wal_files_count(files), "WAL file count")) { index ->
            val file = requireNotNull(rocksdb_wal_files_get_wal_file(files, index.asSizeT()))
            LogFile(
                requireNotNull(rocksdb_wal_file_path_name(file)).toKString(),
                rocksdb_wal_file_log_number(file).toCheckedLong("WAL number"),
                when (val kind = rocksdb_wal_file_type(file)) {
                    0 -> WalFileType.kArchivedLogFile
                    1 -> WalFileType.kAliveLogFile
                    else -> error("Unknown WAL file type: $kind")
                },
                rocksdb_wal_file_start_sequence(file).toCheckedLong("WAL sequence"),
                rocksdb_wal_file_size_file_bytes(file).toCheckedLong("WAL bytes"),
            )
        }
    } finally { rocksdb_wal_files_destroy(files) }
}

private class NativeRanges(scope: MemScope, ranges: List<Range>) {
    val starts = scope.allocArray<CPointerVar<ByteVar>>(maxOf(1, ranges.size))
    val limits = scope.allocArray<CPointerVar<ByteVar>>(maxOf(1, ranges.size))
    val startLengths = scope.allocArray<size_tVar>(maxOf(1, ranges.size))
    val limitLengths = scope.allocArray<size_tVar>(maxOf(1, ranges.size))
    init {
        ranges.forEachIndexed { index, range ->
            val start = range.start.data()
            val limit = range.limit.data()
            starts[index] = scope.allocArray<ByteVar>(maxOf(1, start.size)).also { p -> start.forEachIndexed { i, v -> p[i] = v } }
            limits[index] = scope.allocArray<ByteVar>(maxOf(1, limit.size)).also { p -> limit.forEachIndexed { i, v -> p[i] = v } }
            startLengths[index] = start.size.asSizeT()
            limitLengths[index] = limit.size.asSizeT()
        }
    }
}

actual fun RocksDB.approximateSizes(ranges: List<Range>, vararg sizeApproximationFlags: SizeApproximationFlag): LongArray =
    approximateSizes(null, ranges, *sizeApproximationFlags)
actual fun RocksDB.approximateSizes(columnFamilyHandle: ColumnFamilyHandle?, ranges: List<Range>, vararg sizeApproximationFlags: SizeApproximationFlag): LongArray = memScoped {
    checkOwningHandle()
    columnFamilyHandle?.checkOwningHandle()
    require(sizeApproximationFlags.any { it != SizeApproximationFlag.NONE }) { "Select files and/or memtables for size estimation" }
    if (ranges.isEmpty()) return@memScoped LongArray(0)
    if (columnFamilyHandle == null) return@memScoped getDefaultColumnFamily().use {
        approximateSizes(it, ranges, *sizeApproximationFlags)
    }
    val bounds = NativeRanges(this, ranges)
    val sizes = allocArray<uint64_tVar>(ranges.size)
    val flags = sizeApproximationFlags.fold(0) { flags, value -> flags or value.value.toInt() }
    wrapWithErrorThrower {
        rocksdb_approximate_sizes_cf_with_flags(native, columnFamilyHandle?.native, ranges.size,
            bounds.starts, bounds.startLengths, bounds.limits, bounds.limitLengths, flags.toUByte(), sizes, it)
    }
    LongArray(ranges.size) { sizes[it].toCheckedLong("approximate range size") }
}

actual fun RocksDB.compactFiles(options: CompactionOptions, inputFileNames: List<String>, outputLevel: Int, outputPathId: Int): List<String> =
    compactFiles(options, null, inputFileNames, outputLevel, outputPathId)
actual fun RocksDB.compactFiles(options: CompactionOptions, columnFamilyHandle: ColumnFamilyHandle?, inputFileNames: List<String>, outputLevel: Int, outputPathId: Int): List<String> = memScoped {
    checkOwningHandle()
    options.checkOwningHandle()
    columnFamilyHandle?.checkOwningHandle()
    require(inputFileNames.none { '\u0000' in it }) { "SST filename contains NUL" }
    val config = requireNotNull(rocksdb_compaction_options_create())
    try {
        rocksdb_compaction_options_set_compression(config, options.compression().value.toInt())
        rocksdb_compaction_options_set_output_file_size_limit(config, options.outputFileSizeLimit().let { if (it == -1L) ULong.MAX_VALUE else it.toULong() })
        rocksdb_compaction_options_set_max_subcompactions(config, options.maxSubcompactions().toUInt())
        val inputs = allocArray<CPointerVar<ByteVar>>(maxOf(1, inputFileNames.size))
        inputFileNames.forEachIndexed { i, name -> inputs[i] = name.cstr.getPointer(this) }
        val outputs = alloc<CPointerVar<CPointerVar<ByteVar>>>().apply { value = null }
        val count = alloc<size_tVar>().apply { value = 0.convert() }
        try {
            wrapWithErrorThrower {
                rocksdb_compact_files_cf(native, columnFamilyHandle?.native, config, inputs, inputFileNames.size.asSizeT(), outputLevel, outputPathId, outputs.ptr, count.ptr, null, it)
            }
            List(sizeTToInt(count.value, "compaction output count")) { requireNotNull(outputs.value?.get(it)).toKString() }
        } finally { rocksdb_compact_files_output_file_names_destroy(outputs.value, count.value) }
    } finally { rocksdb_compaction_options_destroy(config) }
}

actual fun RocksDB.approximateMemTableStats(range: Range, columnFamilyHandle: ColumnFamilyHandle?): MemTableStats = memScoped {
    checkOwningHandle()
    columnFamilyHandle?.checkOwningHandle()
    val bounds = NativeRanges(this, listOf(range))
    val count = alloc<uint64_tVar>()
    val size = alloc<uint64_tVar>()
    wrapWithErrorThrower {
        if (columnFamilyHandle == null) rocksdb_approximate_memtable_stats(native, bounds.starts[0], bounds.startLengths[0], bounds.limits[0], bounds.limitLengths[0], count.ptr, size.ptr, it)
        else rocksdb_approximate_memtable_stats_cf(native, columnFamilyHandle.native, bounds.starts[0], bounds.startLengths[0], bounds.limits[0], bounds.limitLengths[0], count.ptr, size.ptr, it)
    }
    MemTableStats(count.value.toCheckedLong("memtable record count"), size.value.toCheckedLong("memtable bytes"))
}

private fun copyTableProperties(collection: CPointer<rocksdb_table_properties_collection_t>): Map<String, TableProperties> = try {
    memScoped {
        val length = alloc<size_tVar>()
        buildMap {
            repeat(sizeTToInt(rocksdb_table_properties_collection_count(collection), "table properties count")) { index ->
                val name = requireNotNull(rocksdb_table_properties_collection_key_at(collection, index.asSizeT(), length.ptr)).toByteArray(length.value).decodeToString()
                put(name, TableProperties(requireNotNull(rocksdb_table_properties_collection_value_at(collection, index.asSizeT()))))
            }
        }
    }
} finally { rocksdb_table_properties_collection_destroy(collection) }

actual fun RocksDB.tableProperties(columnFamilyHandle: ColumnFamilyHandle?): Map<String, TableProperties> {
    checkOwningHandle()
    columnFamilyHandle?.checkOwningHandle()
    return copyTableProperties(requireNotNull(wrapWithNullErrorThrower {
        if (columnFamilyHandle == null) rocksdb_get_properties_of_all_tables(native, it)
        else rocksdb_get_properties_of_all_tables_cf(native, columnFamilyHandle.native, it)
    }))
}
actual fun RocksDB.clipColumnFamily(columnFamilyHandle: ColumnFamilyHandle, begin: ByteArray, end: ByteArray) {
    checkOwningHandle()
    columnFamilyHandle.checkOwningHandle()
    usePointers(begin, end) { start, limit ->
        wrapWithErrorThrower { rocksdb_clip_column_family(native, columnFamilyHandle.native, start, begin.size.asSizeT(), limit, end.size.asSizeT(), it) }
    }
}
