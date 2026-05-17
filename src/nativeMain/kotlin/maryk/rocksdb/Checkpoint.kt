package maryk.rocksdb

import cnames.structs.rocksdb_checkpoint_t
import kotlinx.cinterop.CPointer
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_checkpoint_create
import rocksdb.rocksdb_checkpoint_object_create
import rocksdb.rocksdb_checkpoint_object_destroy

actual class Checkpoint
internal constructor(private val native: CPointer<rocksdb_checkpoint_t>)
    : RocksObject() {
    actual fun createCheckpoint(checkpointPath: String) {
        wrapWithErrorThrower { error ->
            rocksdb_checkpoint_create(native, checkpointPath, 1024u, error)
        }
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_checkpoint_object_destroy(native)
            super.close()
        }
    }
}

actual fun createCheckpoint(db: RocksDB): Checkpoint {
    check(db.isOwningHandle()) { "RocksDB instance must be initialized." }
    val native = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_checkpoint_object_create(db.native, error)
    } ?: throw RocksDBException("Unable to create RocksDB checkpoint object")
    return Checkpoint(native)
}
