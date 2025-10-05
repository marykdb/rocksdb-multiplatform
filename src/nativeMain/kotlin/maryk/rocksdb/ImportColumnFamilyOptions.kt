@file:OptIn(ExperimentalForeignApi::class)

package maryk.rocksdb

import cnames.structs.rocksdb_import_column_family_options_t
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import maryk.toUByte
import rocksdb.rocksdb_import_column_family_options_create
import rocksdb.rocksdb_import_column_family_options_destroy
import rocksdb.rocksdb_import_column_family_options_get_move_files
import rocksdb.rocksdb_import_column_family_options_set_move_files

actual class ImportColumnFamilyOptions internal constructor(
    internal val native: CPointer<rocksdb_import_column_family_options_t>,
    private var moveFilesValue: Boolean,
) : RocksObject() {

    actual constructor() : this(rocksdb_import_column_family_options_create()!!, false)

    actual fun setMoveFiles(moveFiles: Boolean): ImportColumnFamilyOptions {
        moveFilesValue = moveFiles
        rocksdb_import_column_family_options_set_move_files(native, moveFiles.toUByte())
        return this
    }

    actual fun moveFiles(): Boolean {
        moveFilesValue = rocksdb_import_column_family_options_get_move_files(native) != 0.toUByte()
        return moveFilesValue
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_import_column_family_options_destroy(native)
            super.close()
        }
    }
}
