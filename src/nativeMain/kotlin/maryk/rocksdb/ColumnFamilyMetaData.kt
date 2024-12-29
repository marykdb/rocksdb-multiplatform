package maryk.rocksdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString

actual class ColumnFamilyMetaData internal constructor(
    val native: CPointer<cnames.structs.rocksdb_column_family_metadata_t>
): RocksObject() {
    actual fun size(): Long = rocksdb.rocksdb_column_family_metadata_get_size(native).toLong()

    actual fun fileCount(): Long = rocksdb.rocksdb_column_family_metadata_get_file_count(native).toLong()

    actual fun name(): ByteArray = rocksdb.rocksdb_column_family_metadata_get_name(native)!!.toKString().encodeToByteArray()

    actual fun levels(): List<LevelMetaData> {
        val count = rocksdb.rocksdb_column_family_metadata_get_level_count(native)

        return buildList<LevelMetaData> {
            for (i in 0uL until count) {
                add(
                    LevelMetaData(
                        rocksdb.rocksdb_column_family_metadata_get_level_metadata(native, i)!!
                    )
                )
            }
        }
    }
}
