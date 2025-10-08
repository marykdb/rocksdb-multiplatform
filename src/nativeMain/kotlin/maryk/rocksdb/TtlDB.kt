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
import kotlinx.cinterop.toCValues
import maryk.toUByte
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_ttl_close
import rocksdb.rocksdb_ttl_create_column_family
import rocksdb.rocksdb_ttl_get_base_db
import rocksdb.rocksdb_ttl_open
import rocksdb.rocksdb_ttl_open_column_families

actual class TtlDB internal constructor(
    internal val nativeTtl: CPointer<rocksdb_ttl_t>,
) : RocksDB(requireNotNull(rocksdb_ttl_get_base_db(nativeTtl)) {
    "Unable to obtain base DB from TTL handle"
}) {
    actual fun createColumnFamilyWithTtl(
        columnFamilyDescriptor: ColumnFamilyDescriptor,
        ttl: Int,
    ): ColumnFamilyHandle = wrapWithErrorThrower { error ->
        memScoped {
            val name = columnFamilyDescriptor.getName().decodeToString()
            ColumnFamilyHandle(
                rocksdb_ttl_create_column_family(
                    nativeTtl,
                    columnFamilyDescriptor.getOptions().native,
                    name,
                    ttl,
                    error,
                )!!,
            )
        }
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_ttl_close(nativeTtl)
            super.close()
        }
    }
}

actual fun openTtlDB(options: Options, dbPath: String): TtlDB =
    openTtlDB(options, dbPath, 0, false)

actual fun openTtlDB(options: Options, dbPath: String, ttl: Int, readOnly: Boolean): TtlDB =
    Unit.wrapWithNullErrorThrower { error ->
        rocksdb_ttl_open(options.native, dbPath, ttl, readOnly.toUByte(), error)?.let(::TtlDB)
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
    memScoped {
        val count = columnFamilyDescriptors.size
        val optionsArray = allocArray<CPointerVar<rocksdb_options_t>>(count)
        val namesArray = allocArray<CPointerVar<ByteVar>>(count)
        val ttlArray = allocArray<IntVar>(count)
        columnFamilyDescriptors.forEachIndexed { index, descriptor ->
            namesArray[index] = descriptor.getName().toCValues().ptr
            optionsArray[index] = descriptor.getOptions().native
            ttlArray[index] = ttlValues[index]
        }
        val handles = allocArray<CPointerVar<rocksdb_column_family_handle_t>>(count)
        rocksdb_ttl_open_column_families(
            options.native,
            dbPath,
            count,
            namesArray,
            optionsArray,
            ttlArray,
            handles,
            readOnly.toUByte(),
            error,
        )?.let { native ->
            repeat(count) { index ->
                columnFamilyHandles += ColumnFamilyHandle(handles[index]!!)
            }
            TtlDB(native)
        }
    }
} ?: throw RocksDBException("Unable to open TTL DB at $dbPath with provided column families")
