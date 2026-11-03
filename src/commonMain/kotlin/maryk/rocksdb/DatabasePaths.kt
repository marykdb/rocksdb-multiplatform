package maryk.rocksdb

/** A directory and the target number of SST bytes to place there. */
data class DatabasePath(val path: String, val targetSize: Long) {
    init {
        require(path.isNotEmpty() && '\u0000' !in path) { "A database path must be nonempty and contain no NUL" }
        require(targetSize >= 0) { "A database path target size must be nonnegative" }
    }
}

/** Copy SST placement paths. On Android this requires API 26, as required by RocksDB's Java Path API. */
expect fun Options.configureDbPaths(paths: List<DatabasePath>): Options
expect fun DBOptions.configureDbPaths(paths: List<DatabasePath>): DBOptions

/** Copy column-family SST placement paths. On Android this requires API 26. */
expect fun Options.configureCfPaths(paths: List<DatabasePath>): Options
expect fun ColumnFamilyOptions.configureCfPaths(paths: List<DatabasePath>): ColumnFamilyOptions
