package maryk.rocksdb

/**
 * Independent snapshot of the portable scalar DB options currently in effect.
 * Keys use RocksDB's option-string names. This is a supported subset, not every
 * DB option. Later updates do not change this map. Apply changes with a fresh
 * [mutableDBOptionsBuilder], rather than replacing all options from a snapshot.
 */
expect fun RocksDB.effectiveDBOptions(): Map<String, String>

/**
 * Independent snapshot of the portable scalar mutable options for the supplied
 * column family, or the default family when null. Enum and structured settings
 * are omitted because JNI 11.8.1 cannot safely round-trip all of them. TTL and
 * periodic-compaction engine-default sentinel values are represented as "-2".
 */
expect fun RocksDB.effectiveColumnFamilyOptions(
    columnFamilyHandle: ColumnFamilyHandle? = null,
): Map<String, String>

internal val effectiveDBOptionKeys = setOf(
    "max_background_jobs", "max_background_compactions", "max_compaction_trigger_wakeup_seconds",
    "avoid_flush_during_shutdown", "writable_file_max_buffer_size", "delayed_write_rate",
    "max_total_wal_size", "delete_obsolete_files_period_micros", "stats_dump_period_sec",
    "stats_persist_period_sec", "stats_history_buffer_size", "max_open_files", "bytes_per_sync",
    "wal_bytes_per_sync", "strict_bytes_per_sync", "compaction_readahead_size", "daily_offpeak_time_utc",
)

internal val effectiveColumnFamilyOptionKeys = setOf(
    "write_buffer_size", "max_write_buffer_number", "arena_block_size", "memtable_prefix_bloom_size_ratio",
    "memtable_whole_key_filtering", "memtable_huge_page_size", "max_successive_merges", "inplace_update_num_locks",
    "disable_auto_compactions", "soft_pending_compaction_bytes_limit", "hard_pending_compaction_bytes_limit",
    "level0_file_num_compaction_trigger", "level0_slowdown_writes_trigger", "level0_stop_writes_trigger",
    "max_compaction_bytes", "target_file_size_base", "target_file_size_multiplier", "max_bytes_for_level_base",
    "max_bytes_for_level_multiplier", "ttl", "periodic_compaction_seconds", "max_sequential_skip_in_iterations",
    "report_bg_io_stats", "paranoid_file_checks", "experimental_mempurge_threshold",
    "min_tombstones_for_range_conversion", "read_triggered_compaction_threshold", "enable_blob_files",
    "min_blob_size", "blob_file_size", "enable_blob_garbage_collection", "blob_garbage_collection_age_cutoff",
    "blob_garbage_collection_force_threshold", "blob_compaction_readahead_size", "blob_file_starting_level",
)

/** Skip nested/escaped values while selecting only known flat options from native serialization. */
internal fun effectiveOptionSnapshot(source: String, keys: Set<String>): Map<String, String> {
    val result = linkedMapOf<String, String>()
    var start = 0
    var depth = 0
    var escaped = false
    fun add(end: Int) {
        val entry = source.substring(start, end)
        val equals = entry.indexOf('=')
        if (equals < 0) return
        val key = entry.substring(0, equals).trim()
        if (key in keys) {
            val value = entry.substring(equals + 1).trim()
            result[key] = if ((key == "ttl" || key == "periodic_compaction_seconds") &&
                value == "18446744073709551614"
            ) "-2" else value
        }
    }
    source.forEachIndexed { index, c ->
        when {
            escaped -> escaped = false
            c == '\\' -> escaped = true
            c == '{' -> depth++
            c == '}' -> depth--
            c == ';' && depth == 0 -> {
                add(index)
                start = index + 1
            }
        }
    }
    add(source.length)
    return result.toMap()
}
