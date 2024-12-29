package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class LevelMetaData(
    val native: CPointer<cnames.structs.rocksdb_level_metadata_t>
) {
    actual fun level() = rocksdb.rocksdb_level_metadata_get_level(native).toInt()

    actual fun size() = rocksdb.rocksdb_level_metadata_get_size(native).toLong()

    actual fun files(): List<SstFileMetaData> {
        val count = rocksdb.rocksdb_level_metadata_get_file_count(native)

        return buildList<SstFileMetaData> {
            for (i in 0uL until count) {
                add(
                    SstFileMetaData(
                        rocksdb.rocksdb_level_metadata_get_sst_file_metadata(native, i)!!
                    )
                )
            }
        }
    }
}
