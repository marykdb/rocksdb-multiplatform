package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import platform.posix.size_tVar
import rocksdb.rocksdb_column_family_handle_destroy
import rocksdb.rocksdb_column_family_handle_get_id
import rocksdb.rocksdb_column_family_handle_get_name

actual class ColumnFamilyHandle
internal constructor(
    val native: CPointer<rocksdb_column_family_handle_t>
)
    : RocksObject() {
    override fun close() {
        if (isOwningHandle()) {
            rocksdb_column_family_handle_destroy(native)
            super.close()
        }
    }

    @OptIn(UnsafeNumber::class)
    actual fun getName(): ByteArray = memScoped {
        val length = alloc<size_tVar>()
        rocksdb_column_family_handle_get_name(native, length.ptr)?.let { name ->
            name.toByteArray(length.value).also {
                rocksdb.rocksdb_free(name)
            }
        } ?: throw RocksDBException("Missing Column Family Name")
    }

    actual fun getID(): Int =
        rocksdb_column_family_handle_get_id(native).toInt()
}
