package maryk.rocksdb

import cnames.structs.rocksdb_export_import_files_metadata_t
import kotlinx.cinterop.CPointer
import rocksdb.rocksdb_export_import_files_metadata_destroy

actual class ExportImportFilesMetaData internal constructor(
    internal val native: CPointer<rocksdb_export_import_files_metadata_t>,
) : RocksObject() {
    override fun close() {
        if (tryClose()) {
            rocksdb_export_import_files_metadata_destroy(native)
            super.close()
        }
    }
}
