package maryk.rocksdb

import maryk.toBoolean
import maryk.toUByte

actual class OptimisticTransactionOptions actual constructor(): RocksObject() {
    val native = rocksdb.rocksdb_optimistictransaction_options_create()

    actual fun isSetSnapshot(): Boolean {
        return rocksdb.rocksdb_optimistictransaction_options_get_set_snapshot(native).toBoolean()
    }

    actual fun setSetSnapshot(setSnapshot: Boolean): OptimisticTransactionOptions {
        rocksdb.rocksdb_optimistictransaction_options_set_set_snapshot(native, setSnapshot.toUByte())
        return this
    }

    override fun close() {
        if (isOwningHandle()) {
            rocksdb.rocksdb_optimistictransaction_options_destroy(native)
            super.close()
        }
    }
}
