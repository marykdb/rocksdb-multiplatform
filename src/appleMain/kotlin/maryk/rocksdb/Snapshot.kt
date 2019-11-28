package maryk.rocksdb

import rocksdb.RocksDBSnapshot

actual class Snapshot internal constructor(
    internal val native: RocksDBSnapshot
) : RocksObject() {
    actual fun getSequenceNumber() = native.sequenceNumber().toLong()
}
