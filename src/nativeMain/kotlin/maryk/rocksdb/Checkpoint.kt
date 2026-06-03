package maryk.rocksdb

import cnames.structs.rocksdb_checkpoint_t
import kotlinx.cinterop.CPointer
import maryk.wrapWithErrorThrower
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_checkpoint_export_column_family
import rocksdb.rocksdb_checkpoint_create
import rocksdb.rocksdb_checkpoint_object_create
import rocksdb.rocksdb_checkpoint_object_destroy

actual class Checkpoint
internal constructor(
    private val native: CPointer<rocksdb_checkpoint_t>,
    private val owner: RocksDB,
)
    : RocksObject() {
    actual fun createCheckpoint(checkpointPath: String) {
        checkOwningHandle()
        owner.checkOwningHandle()
        wrapWithErrorThrower { error ->
            rocksdb_checkpoint_create(native, checkpointPath, 1024u, error)
        }
    }

    actual fun exportColumnFamily(
        columnFamilyHandle: ColumnFamilyHandle,
        exportPath: String
    ): ExportImportFilesMetaData {
        checkOwningHandle()
        owner.checkOwningHandle()
        columnFamilyHandle.checkOwningHandle()
        val metadata = Unit.wrapWithNullErrorThrower { error ->
            rocksdb_checkpoint_export_column_family(native, columnFamilyHandle.native, exportPath, error)
        } ?: throw RocksDBException("Unable to export column family to $exportPath")
        return ExportImportFilesMetaData(metadata)
    }

    override fun close() {
        if (tryClose()) {
            rocksdb_checkpoint_object_destroy(native)
            super.close()
        }
    }
}

actual fun createCheckpoint(db: RocksDB): Checkpoint {
    db.checkOwningHandle()
    val native = Unit.wrapWithNullErrorThrower { error ->
        rocksdb_checkpoint_object_create(db.native, error)
    } ?: throw RocksDBException("Unable to create RocksDB checkpoint object")
    return Checkpoint(native, db)
}
