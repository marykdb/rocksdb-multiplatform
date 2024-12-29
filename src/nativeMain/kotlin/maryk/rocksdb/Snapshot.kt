package maryk.rocksdb

import cnames.structs.rocksdb_snapshot_t
import kotlinx.cinterop.CPointer

actual class Snapshot internal constructor(
    internal val native: CPointer<rocksdb_snapshot_t>
) : RocksObject() {
    actual fun getSequenceNumber() = rocksdb.rocksdb_snapshot_get_sequence_number(native).toLong()
}
