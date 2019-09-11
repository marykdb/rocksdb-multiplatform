package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class Checkpoint
    internal constructor(db: RocksDB)
: RocksObject(newCheckpoint(db.nativeHandle)) {
    actual fun createCheckpoint(checkpointPath: String) {
    }
}

fun newCheckpoint(nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

actual fun createCheckpoint(db: RocksDB?): Checkpoint {
    requireNotNull(db) { "RocksDB instance shall not be null." }
    check(db.isOwningHandle()) { "RocksDB instance must be initialized." }
    return Checkpoint(db)
}
