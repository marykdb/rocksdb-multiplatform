package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class ColumnFamilyHandle
    internal constructor(
        private val rocksDB: RocksDB,
        nativeHandle: CPointer<*>
    )
: RocksObject(nativeHandle) {
    actual fun getName(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getID(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getDescriptor(): ColumnFamilyDescriptor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
