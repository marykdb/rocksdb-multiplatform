package maryk.rocksdb

import cnames.structs.rocksdb_envoptions_t
import kotlinx.cinterop.CPointer
import rocksdb.rocksdb_envoptions_create
import rocksdb.rocksdb_envoptions_destroy

actual class EnvOptions actual constructor() : RocksObject() {
    internal val native: CPointer<rocksdb_envoptions_t> =
        requireNotNull(rocksdb_envoptions_create()) { "Unable to allocate RocksDB environment options" }

    override fun close() {
        if (tryClose()) {
            rocksdb_envoptions_destroy(native)
            super.close()
        }
    }
}
