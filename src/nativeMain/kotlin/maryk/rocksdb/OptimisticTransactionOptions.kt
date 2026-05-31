package maryk.rocksdb

import maryk.toBoolean
import maryk.toUByte

actual class OptimisticTransactionOptions actual constructor(): RocksObject() {
    val native = requireNotNull(rocksdb.rocksdb_optimistictransaction_options_create()) {
        "Unable to allocate RocksDB optimistic transaction options"
    }

    actual fun isSetSnapshot(): Boolean {
        checkOwningHandle()
        return rocksdb.rocksdb_optimistictransaction_options_get_set_snapshot(native).toBoolean()
    }

    actual fun setSetSnapshot(setSnapshot: Boolean): OptimisticTransactionOptions {
        checkOwningHandle()
        rocksdb.rocksdb_optimistictransaction_options_set_set_snapshot(native, setSnapshot.toUByte())
        return this
    }

    override fun close() {
        if (tryClose()) {
            rocksdb.rocksdb_optimistictransaction_options_destroy(native)
            super.close()
        }
    }
}
