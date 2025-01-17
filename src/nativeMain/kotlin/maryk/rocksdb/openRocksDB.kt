package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import kotlinx.cinterop.toCValues
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_open
import rocksdb.rocksdb_open_column_families
import rocksdb.rocksdb_open_for_read_only
import rocksdb.rocksdb_open_for_read_only_column_families

actual fun openRocksDB(path: String): RocksDB {
    return Options().use { options ->
        options.setCreateIfMissing(true)

        openRocksDB(options, path)
    }
}

actual fun openRocksDB(options: Options, path: String): RocksDB {
    return Unit.wrapWithNullErrorThrower { error ->
        rocksdb_open(options.native, path, error)?.let(::RocksDB)
    } ?: throw RocksDBException("No Database could be opened at $path")
}

actual fun openRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB =
    DBOptions().use {
        openRocksDB(it, path, columnFamilyDescriptors, columnFamilyHandles)
    }

@OptIn(ExperimentalStdlibApi::class)
actual fun openRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB =
    Unit.wrapWithNullErrorThrower { error ->
        memScoped {
            val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(columnFamilyDescriptors.size)
            val namesArray = allocArray<CPointerVar<ByteVar>>(columnFamilyDescriptors.size)

            columnFamilyDescriptors.forEachIndexed { index, cfDesc ->
                namesArray[index] = cfDesc.getName().toCValues().ptr
                optionsArray[index] = cfDesc.getOptions().native
            }

            val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyDescriptors.size)

            rocksdb_open_column_families(
                options.native,
                path,
                columnFamilyDescriptors.size,
                namesArray,
                optionsArray,
                handles,
                error,
            )?.let(::RocksDB).also {
                for (i in columnFamilyDescriptors.indices) {
                    columnFamilyHandles += ColumnFamilyHandle(handles[i]!!)
                }
            }
        }
    } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")

actual fun openReadOnlyRocksDB(path: String): RocksDB =
    Options().use {
        openReadOnlyRocksDB(it, path)
    }

actual fun openReadOnlyRocksDB(options: Options, path: String): RocksDB = Unit.wrapWithNullErrorThrower { error ->
    Options().use {
        rocksdb_open_for_read_only(options.native, path, 0u, error)?.let(::RocksDB)
    }
} ?: throw RocksDBException("No Database could be opened at $path")

actual fun openReadOnlyRocksDB(
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB =
    DBOptions().use {
        openReadOnlyRocksDB(it, path, columnFamilyDescriptors, columnFamilyHandles)
    }

actual fun openReadOnlyRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB =
    memScoped {
        Unit.wrapWithNullErrorThrower { error ->
            memScoped {
                val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(columnFamilyDescriptors.size)
                val namesArray = allocArray<CPointerVar<ByteVar>>(columnFamilyDescriptors.size)

                columnFamilyDescriptors.forEachIndexed { index, cfDesc ->
                    namesArray[index] = cfDesc.getName().toCValues().ptr
                    optionsArray[index] = cfDesc.getOptions().native
                }

                memScoped {
                    val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyDescriptors.size)
                    rocksdb_open_for_read_only_column_families(
                        options.native,
                        path,
                        columnFamilyDescriptors.size,
                        namesArray,
                        optionsArray,
                        handles,
                        0u,
                        error,
                    )?.let(::RocksDB).also {
                        for (i in columnFamilyDescriptors.indices) {
                            columnFamilyHandles += ColumnFamilyHandle(handles[i]!!)
                        }
                    }
                }
            }
        } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")
    }
