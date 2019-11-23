package maryk.rocksdb

import maryk.wrapWithErrorThrower
import rocksdb.RocksDBCheckpoint

actual class Checkpoint
    private constructor(private val native: RocksDBCheckpoint)
: RocksObject() {
    internal constructor(db: RocksDB) : this(
        Unit.wrapWithErrorThrower { error ->
            RocksDBCheckpoint(db.native, error)
        }
    )

    actual fun createCheckpoint(checkpointPath: String) {
        wrapWithErrorThrower { error ->
            native.createCheckpointAtPath(checkpointPath, error)
        }
    }
}

actual fun createCheckpoint(db: RocksDB): Checkpoint {
    check(db.isOwningHandle()) { "RocksDB instance must be initialized." }
    return Checkpoint(db)
}
