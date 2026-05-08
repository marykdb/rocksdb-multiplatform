package maryk.rocksdb

import cnames.structs.rocksdb_snapshot_t
import kotlinx.cinterop.CPointer

actual class Snapshot internal constructor(
    internal val native: CPointer<rocksdb_snapshot_t>,
    private val owner: RocksDB? = null
) : RocksObject() {
    actual fun getSequenceNumber() = rocksdb.rocksdb_snapshot_get_sequence_number(native).toLong()

    override fun close() {
        owner?.let { releaseFrom(it) } ?: super.close()
    }

    internal fun releaseFrom(db: RocksDB) {
        if (isOwningHandle()) {
            rocksdb.rocksdb_release_snapshot(db.native, native)
            super.close()
        }
    }
}
