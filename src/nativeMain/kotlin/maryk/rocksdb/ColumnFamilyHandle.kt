package maryk.rocksdb

import cnames.structs.rocksdb_column_family_handle_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import maryk.toByteArray
import maryk.toCheckedInt
import platform.posix.size_tVar
import rocksdb.rocksdb_column_family_handle_destroy
import rocksdb.rocksdb_column_family_handle_get_id
import rocksdb.rocksdb_column_family_handle_get_name

actual class ColumnFamilyHandle
internal constructor(
    val native: CPointer<rocksdb_column_family_handle_t>,
    private var dbOwner: RocksDB? = null,
)
    : RocksObject() {
    override fun close() {
        val owner = dbOwner
        dbOwner = null
        if (tryClose()) {
            owner?.unregisterColumnFamilyHandle(this)
            rocksdb_column_family_handle_destroy(native)
            super.close()
        }
    }

    internal fun attachTo(db: RocksDB): ColumnFamilyHandle {
        dbOwner = db
        return this
    }

    internal fun invalidateFromOwner() {
        dbOwner = null
        if (tryClose()) {
            rocksdb_column_family_handle_destroy(native)
            super.close()
        }
    }

    @OptIn(UnsafeNumber::class)
    actual fun getName(): ByteArray = memScoped {
        checkOwningHandle()
        val length = alloc<size_tVar>()
        rocksdb_column_family_handle_get_name(native, length.ptr)?.let { name ->
            try {
                name.toByteArray(length.value)
            } finally {
                rocksdb.rocksdb_free(name)
            }
        } ?: throw RocksDBException("Missing Column Family Name")
    }

    actual fun getID(): Int {
        checkOwningHandle()
        return rocksdb_column_family_handle_get_id(native).toCheckedInt("column family id")
    }
}
