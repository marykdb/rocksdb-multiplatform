package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class TransactionLogIterator
    internal constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle) {
    actual fun isValid(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getBatch(): BatchResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual operator fun next() {
    }

    actual fun status() {
    }
}
