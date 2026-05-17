package maryk.rocksdb

import cnames.structs.rocksdb_snapshot_t
import kotlinx.cinterop.CPointer
import rocksdb.rocksdb_free

actual class Snapshot internal constructor(
    internal val native: CPointer<rocksdb_snapshot_t>,
    private val owner: RocksDB? = null,
    private val freeWrapperOnClose: Boolean = false,
) : RocksObject() {
    actual fun getSequenceNumber() = rocksdb.rocksdb_snapshot_get_sequence_number(native).toLong()

    override fun close() {
        when {
            owner != null -> releaseFrom(owner)
            freeWrapperOnClose && tryClose() -> {
                rocksdb_free(native)
                super.close()
            }
            else -> super.close()
        }
    }

    internal fun releaseFrom(db: RocksDB) {
        if (tryClose()) {
            rocksdb.rocksdb_release_snapshot(db.native, native)
            super.close()
        }
    }
}
