package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import cnames.structs.rocksdb_options_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import maryk.wrapWithNullErrorThrower
import rocksdb.maryk_rocksdb_open_as_secondary_column_families_with_lengths
import rocksdb.maryk_rocksdb_open_column_families_with_lengths
import rocksdb.maryk_rocksdb_open_for_read_only_column_families_with_lengths
import rocksdb.rocksdb_close
import rocksdb.rocksdb_open
import rocksdb.rocksdb_open_as_secondary
import rocksdb.rocksdb_open_for_read_only

actual fun openRocksDB(path: String): RocksDB {
    return Options().use { options ->
        options.setCreateIfMissing(true)

        openRocksDB(options, path)
    }
}

actual fun openRocksDB(options: Options, path: String): RocksDB {
    options.checkOwningHandle()
    return Unit.wrapWithNullErrorThrower { error ->
        val retainedReferences = options.retainedNativeReferences()
        rocksdb_open(options.native, path, error)?.let { native ->
            wrapOpenedDb(
                closeNativeDb = { rocksdb_close(native) },
                releaseComparator = { options.releaseOwnedComparator() },
            ) { ownedComparators ->
                RocksDB(native, ownedComparators, retainedReferences)
            }
        }
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

actual fun openRocksDB(
    options: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB =
    Unit.wrapWithNullErrorThrower { error ->
        require(columnFamilyDescriptors.isNotEmpty()) { "columnFamilyDescriptors must not be empty" }
        options.checkOwningHandle()
        columnFamilyDescriptors.forEach { it.getOptions().checkOwningHandle() }
        memScoped {
            val retainedReferences = options.retainedNativeReferences() +
                columnFamilyDescriptors.flatMap { it.getOptions().retainedNativeReferences() }
            val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(columnFamilyDescriptors.size)
            val namesArray = allocArray<CPointerVar<ByteVar>>(columnFamilyDescriptors.size)
            val nameLengths = allocArray<ULongVar>(columnFamilyDescriptors.size)

            columnFamilyDescriptors.forEachIndexed { index, cfDesc ->
                val name = cfDesc.getName()
                namesArray[index] = columnFamilyNameToCString(name)
                nameLengths[index] = name.size.toULong()
                optionsArray[index] = cfDesc.getOptions().native
            }

            val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyDescriptors.size)
            val native = maryk_rocksdb_open_column_families_with_lengths(
                options.native,
                path.cstr.getPointer(this),
                columnFamilyDescriptors.size,
                namesArray,
                nameLengths,
                optionsArray,
                handles,
                error,
            ) ?: return@memScoped null

            wrapOpenedColumnFamilies(
                handles = handles,
                count = columnFamilyDescriptors.size,
                columnFamilyDescriptors = columnFamilyDescriptors,
                columnFamilyHandles = columnFamilyHandles,
                closeNativeDb = { rocksdb_close(native) },
            ) { ownedComparators ->
                RocksDB(native, ownedComparators, retainedReferences)
            }
        }
    } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")

actual fun openAsSecondaryRocksDB(
    options: Options,
    path: String,
    secondaryPath: String
): RocksDB = Unit.wrapWithNullErrorThrower { error ->
    options.checkOwningHandle()
    val retainedReferences = options.retainedNativeReferences()
    rocksdb_open_as_secondary(options.native, path, secondaryPath, error)?.let { native ->
        wrapOpenedDb(
            closeNativeDb = { rocksdb_close(native) },
            releaseComparator = { options.releaseOwnedComparator() },
        ) { ownedComparators ->
            RocksDB(native, ownedComparators, retainedReferences)
        }
    }
} ?: throw RocksDBException("No secondary Database could be opened at $path")

actual fun openAsSecondaryRocksDB(
    options: DBOptions,
    path: String,
    secondaryPath: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): RocksDB =
    Unit.wrapWithNullErrorThrower { error ->
        require(columnFamilyDescriptors.isNotEmpty()) { "columnFamilyDescriptors must not be empty" }
        options.checkOwningHandle()
        columnFamilyDescriptors.forEach { it.getOptions().checkOwningHandle() }
        memScoped {
            val retainedReferences = options.retainedNativeReferences() +
                columnFamilyDescriptors.flatMap { it.getOptions().retainedNativeReferences() }
            val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(columnFamilyDescriptors.size)
            val namesArray = allocArray<CPointerVar<ByteVar>>(columnFamilyDescriptors.size)
            val nameLengths = allocArray<ULongVar>(columnFamilyDescriptors.size)

            columnFamilyDescriptors.forEachIndexed { index, cfDesc ->
                val name = cfDesc.getName()
                namesArray[index] = columnFamilyNameToCString(name)
                nameLengths[index] = name.size.toULong()
                optionsArray[index] = cfDesc.getOptions().native
            }

            val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyDescriptors.size)
            val native = maryk_rocksdb_open_as_secondary_column_families_with_lengths(
                options.native,
                path.cstr.getPointer(this),
                secondaryPath.cstr.getPointer(this),
                columnFamilyDescriptors.size,
                namesArray,
                nameLengths,
                optionsArray,
                handles,
                error,
            ) ?: return@memScoped null

            wrapOpenedColumnFamilies(
                handles = handles,
                count = columnFamilyDescriptors.size,
                columnFamilyDescriptors = columnFamilyDescriptors,
                columnFamilyHandles = columnFamilyHandles,
                closeNativeDb = { rocksdb_close(native) },
            ) { ownedComparators ->
                RocksDB(native, ownedComparators, retainedReferences)
            }
        }
    } ?: throw RocksDBException("No secondary Database could be opened at $path with given descriptors and handles for column families")

actual fun openReadOnlyRocksDB(path: String): RocksDB =
    Options().use {
        openReadOnlyRocksDB(it, path)
    }

actual fun openReadOnlyRocksDB(options: Options, path: String): RocksDB = Unit.wrapWithNullErrorThrower { error ->
    options.checkOwningHandle()
    val retainedReferences = options.retainedNativeReferences()
    rocksdb_open_for_read_only(options.native, path, 0u, error)?.let { native ->
        wrapOpenedDb(
            closeNativeDb = { rocksdb_close(native) },
            releaseComparator = { options.releaseOwnedComparator() },
        ) { ownedComparators ->
            RocksDB(native, ownedComparators, retainedReferences)
        }
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
    Unit.wrapWithNullErrorThrower { error ->
        require(columnFamilyDescriptors.isNotEmpty()) { "columnFamilyDescriptors must not be empty" }
        options.checkOwningHandle()
        columnFamilyDescriptors.forEach { it.getOptions().checkOwningHandle() }
        memScoped {
            val retainedReferences = options.retainedNativeReferences() +
                columnFamilyDescriptors.flatMap { it.getOptions().retainedNativeReferences() }
            val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(columnFamilyDescriptors.size)
            val namesArray = allocArray<CPointerVar<ByteVar>>(columnFamilyDescriptors.size)
            val nameLengths = allocArray<ULongVar>(columnFamilyDescriptors.size)

            columnFamilyDescriptors.forEachIndexed { index, cfDesc ->
                val name = cfDesc.getName()
                namesArray[index] = columnFamilyNameToCString(name)
                nameLengths[index] = name.size.toULong()
                optionsArray[index] = cfDesc.getOptions().native
            }

            val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(columnFamilyDescriptors.size)
            val native = maryk_rocksdb_open_for_read_only_column_families_with_lengths(
                options.native,
                path.cstr.getPointer(this),
                columnFamilyDescriptors.size,
                namesArray,
                nameLengths,
                optionsArray,
                handles,
                0u,
                error,
            ) ?: return@memScoped null

            wrapOpenedColumnFamilies(
                handles = handles,
                count = columnFamilyDescriptors.size,
                columnFamilyDescriptors = columnFamilyDescriptors,
                columnFamilyHandles = columnFamilyHandles,
                closeNativeDb = { rocksdb_close(native) },
            ) { ownedComparators ->
                RocksDB(native, ownedComparators, retainedReferences)
            }
        }
    } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")
