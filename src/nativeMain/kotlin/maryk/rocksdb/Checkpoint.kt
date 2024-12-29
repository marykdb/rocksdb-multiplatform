package maryk.rocksdb

import cnames.structs.rocksdb_checkpoint_t
import kotlinx.cinterop.CPointer
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_checkpoint_create
import rocksdb.rocksdb_checkpoint_object_create

actual class Checkpoint
internal constructor(private val native: CPointer<rocksdb_checkpoint_t>?)
    : RocksObject() {
    actual fun createCheckpoint(checkpointPath: String) {
        wrapWithErrorThrower { error ->
            rocksdb_checkpoint_create(native, checkpointPath, 1024u, error)
        }
    }
}

actual fun createCheckpoint(db: RocksDB): Checkpoint {
    check(db.isOwningHandle()) { "RocksDB instance must be initialized." }
    return Unit.wrapWithErrorThrower { error ->
        Checkpoint(
            rocksdb_checkpoint_object_create(db.native, error)
        )
    }
}
