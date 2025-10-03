package maryk.rocksdb

import cnames.structs.rocksdb_envoptions_t
import kotlinx.cinterop.CPointer
import rocksdb.rocksdb_envoptions_create
import rocksdb.rocksdb_envoptions_destroy

actual class EnvOptions actual constructor() : RocksObject() {
    internal val native: CPointer<rocksdb_envoptions_t> = rocksdb_envoptions_create()!!

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_envoptions_destroy(native)
            super.close()
        }
    }
}
