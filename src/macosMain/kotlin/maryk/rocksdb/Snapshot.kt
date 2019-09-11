package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class Snapshot internal constructor(nativeHandle: CPointer<*>) : RocksObject(nativeHandle) {
    actual fun getSequenceNumber(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
