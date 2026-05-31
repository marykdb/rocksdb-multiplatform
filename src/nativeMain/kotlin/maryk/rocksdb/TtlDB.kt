package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import cnames.structs.rocksdb_options_t
import cnames.structs.rocksdb_t
import cnames.structs.rocksdb_ttl_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import maryk.toUByte
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_ttl_close
import rocksdb.rocksdb_ttl_create_column_family
import rocksdb.rocksdb_ttl_get_base_db
import rocksdb.rocksdb_ttl_open
import rocksdb.rocksdb_ttl_open_column_families
import rocksdb.rocksdb_transactiondb_close_base_db

actual class TtlDB internal constructor(
    internal val nativeTtl: CPointer<rocksdb_ttl_t>,
    ownedComparators: List<AbstractComparator> = emptyList(),
    retainedReferences: List<Any> = emptyList(),
) : RocksDB(requireNotNull(rocksdb_ttl_get_base_db(nativeTtl)) {
    "Unable to obtain base DB from TTL handle"
}, ownedComparators, retainedReferences) {
    actual fun createColumnFamilyWithTtl(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        ttl: Int,
    ): ColumnFamilyHandle =
        memScoped {
            checkOwningHandle()
            columnFamilyDescriptor.getOptions().checkOwningHandle()
            val handle = createColumnFamilyHandle { error ->
                rocksdb_ttl_create_column_family(
                    nativeTtl,
                    columnFamilyDescriptor.getOptions().native,
                    columnFamilyNameToCString(columnFamilyDescriptor.getName()),
                    ttl,
                    error,
                )
            }
            try {
                columnFamilyDescriptor.getOptions().releaseOwnedComparator()?.let {
                    retainOwnedComparator(it)
                }
            } catch (throwable: Throwable) {
                handle.close()
                throw throwable
            }
            registerColumnFamilyHandle(handle)
        }

    override fun close() {
        if (tryClose()) {
            invalidateBorrowedIterators()
            invalidateBorrowedTransactionLogIterators()
            releaseBorrowedSnapshots()
            invalidateColumnFamilyHandles()
            closeDefaultReferences()
            // RocksDB exposes no TTL-specific close_base_db; this helper deletes only the rocksdb_t wrapper.
            rocksdb_transactiondb_close_base_db(native)
            rocksdb_ttl_close(nativeTtl)
            closeOwnedComparators()
            clearRetainedReferences()
            super.close()
        }
    }
}

actual fun openTtlDB(options: Options, dbPath: String): TtlDB =
    openTtlDB(options, dbPath, 0, false)

actual fun openTtlDB(options: Options, dbPath: String, ttl: Int, readOnly: Boolean): TtlDB =
    Unit.wrapWithNullErrorThrower { error ->
        options.checkOwningHandle()
        val retainedReferences = options.retainedNativeReferences()
        rocksdb_ttl_open(options.native, dbPath, ttl, readOnly.toUByte(), error)?.let { native ->
            wrapOpenedDb(
                closeNativeDb = { rocksdb_ttl_close(native) },
                releaseComparator = { options.releaseOwnedComparator() },
            ) { ownedComparators ->
                TtlDB(native, ownedComparators, retainedReferences)
            }
        }
    } ?: throw RocksDBException("Unable to open TTL DB at $dbPath")

actual fun openTtlDB(
    options: DBOptions,
    dbPath: String,
    columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
    columnFamilyHandles: MutableList<ColumnFamilyHandle>,
    ttlValues: List<Int>,
    readOnly: Boolean,
): TtlDB = Unit.wrapWithNullErrorThrower { error ->
    require(columnFamilyDescriptors.size == ttlValues.size) {
        "ttlValues size (${ttlValues.size}) must match descriptors size (${columnFamilyDescriptors.size})"
    }
    require(columnFamilyDescriptors.isNotEmpty()) { "columnFamilyDescriptors must not be empty" }
    options.checkOwningHandle()
    columnFamilyDescriptors.forEach { it.getOptions().checkOwningHandle() }
    memScoped {
        val retainedReferences = options.retainedNativeReferences()
        val count = columnFamilyDescriptors.size
        val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(count)
        val namesArray = allocArray<CPointerVar<ByteVar>>(count)
        val ttlArray = allocArray<IntVar>(count)
        columnFamilyDescriptors.forEachIndexed { index, descriptor ->
            val name = descriptor.getName()
            namesArray[index] = columnFamilyNameToCString(name)
            optionsArray[index] = descriptor.getOptions().native
            ttlArray[index] = ttlValues[index]
        }
        val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(count)
        val native = rocksdb_ttl_open_column_families(
            options.native,
            dbPath,
            count,
            namesArray,
            optionsArray,
            ttlArray,
            handles,
            readOnly.toUByte(),
            error,
        ) ?: return@memScoped null

        wrapOpenedColumnFamilies(
            handles = handles,
            count = count,
            columnFamilyDescriptors = columnFamilyDescriptors,
            columnFamilyHandles = columnFamilyHandles,
            closeNativeDb = { rocksdb_ttl_close(native) },
        ) { ownedComparators ->
            TtlDB(native, ownedComparators, retainedReferences)
        }
    }
} ?: throw RocksDBException("Unable to open TTL DB at $dbPath with provided column families")
