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
import rocksdb.maryk_rocksdb_optimistictransactiondb_open_column_families_with_lengths as openOptimisticTransactionColumnFamiliesWithLengths

@Throws(RocksDBException::class)
actual fun openOptimisticTransactionDB(
    dbOptions: DBOptions,
    path: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>
): OptimisticTransactionDB {
    require(columnFamilyDescriptors.isNotEmpty()) { "columnFamilyDescriptors must not be empty" }
    dbOptions.checkOwningHandle()
    columnFamilyDescriptors.forEach { it.getOptions().checkOwningHandle() }
    if (columnFamilyDescriptors.find { it.getName().contentEquals(defaultColumnFamily) } == null) {
        throw IllegalArgumentException("Default column family descriptor should always be included")
    }

    return Unit.wrapWithNullErrorThrower { error ->
        memScoped {
            val retainedReferences = dbOptions.retainedNativeReferences() +
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

            val native = openOptimisticTransactionColumnFamiliesWithLengths(
                dbOptions.native,
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
                closeNativeDb = { rocksdb.rocksdb_optimistictransactiondb_close(native) },
            ) { ownedComparators ->
                OptimisticTransactionDB(native, ownedComparators, retainedReferences)
            }
        }
    } ?: throw RocksDBException("No Database could be opened at $path with given descriptors and handles for column families")
}

@Throws(RocksDBException::class)
actual fun openOptimisticTransactionDB(
    options: Options,
    path: String
): OptimisticTransactionDB =
    Unit.wrapWithNullErrorThrower { error ->
        options.checkOwningHandle()
        val retainedReferences = options.retainedNativeReferences()
        rocksdb.rocksdb_optimistictransactiondb_open(options.native, path, error)?.let { native ->
            wrapOpenedDb(
                closeNativeDb = { rocksdb.rocksdb_optimistictransactiondb_close(native) },
                releaseComparator = { options.releaseOwnedComparator() },
            ) { ownedComparators ->
                OptimisticTransactionDB(native, ownedComparators, retainedReferences)
            }
        }
    } ?: throw RocksDBException("No Database could be opened at $path")
