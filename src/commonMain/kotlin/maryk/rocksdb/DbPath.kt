package maryk.rocksdb

/**
 * Tuple of database path and target size
 */
expect class DbPath

expect fun DbPath(
    path: String,
    targetSize: Long
): DbPath
