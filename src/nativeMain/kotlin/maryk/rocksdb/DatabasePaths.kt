@file:OptIn(kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_dbpath_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import maryk.asSizeT
import maryk.asUInt64
import rocksdb.rocksdb_dbpath_create
import rocksdb.rocksdb_dbpath_destroy
import rocksdb.rocksdb_options_set_db_paths
import rocksdb.rocksdb_options_set_cf_paths

actual fun Options.configureDbPaths(paths: List<DatabasePath>): Options {
    checkOwningHandle()
    withDatabasePaths(paths) { handles -> rocksdb_options_set_db_paths(native, handles, paths.size.toLong().asSizeT()) }
    return this
}

actual fun DBOptions.configureDbPaths(paths: List<DatabasePath>): DBOptions {
    checkOwningHandle()
    withDatabasePaths(paths) { handles -> rocksdb_options_set_db_paths(native, handles, paths.size.toLong().asSizeT()) }
    return this
}

actual fun Options.configureCfPaths(paths: List<DatabasePath>): Options {
    checkOwningHandle()
    withDatabasePaths(paths) { handles -> rocksdb_options_set_cf_paths(native, handles, paths.size.toLong().asSizeT()) }
    return this
}

actual fun ColumnFamilyOptions.configureCfPaths(paths: List<DatabasePath>): ColumnFamilyOptions {
    checkOwningHandle()
    withDatabasePaths(paths) { handles -> rocksdb_options_set_cf_paths(native, handles, paths.size.toLong().asSizeT()) }
    return this
}

private inline fun withDatabasePaths(
    paths: List<DatabasePath>,
    block: (CPointer<CPointerVar<rocksdb_dbpath_t>>) -> Unit,
) = memScoped {
    val owned = mutableListOf<CPointer<rocksdb_dbpath_t>>()
    try {
        val handles = allocArray<CPointerVar<rocksdb_dbpath_t>>(maxOf(1, paths.size))
        paths.forEachIndexed { index, path ->
            val handle = requireNotNull(rocksdb_dbpath_create(path.path, path.targetSize.asUInt64())) {
                "Unable to allocate database path"
            }
            owned += handle
            handles[index] = handle
        }
        block(handles)
    } finally {
        owned.forEach { rocksdb_dbpath_destroy(it) }
    }
}
