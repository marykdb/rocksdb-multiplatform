package maryk.rocksdb

import kotlinx.cinterop.readBytes
import maryk.toCPointer
import rocksdb.RocksDBSlice

actual class Slice internal constructor(
    native: RocksDBSlice = RocksDBSlice()
) : AbstractSlice<ByteArray>(native) {
    actual constructor(str: String) : this(RocksDBSlice(str))

    actual constructor(data: ByteArray, offset: Int) : this(RocksDBSlice(data.toCPointer(), offset.toULong(), (data.size - offset).toULong()))

    actual constructor(data: ByteArray) : this(RocksDBSlice(data.toCPointer()))

    override fun getData(): ByteArray {
        return native.data()!!.readBytes(this.size())
    }

    override fun removePrefix(n: Int) {
        native.removePrefix(n.toULong())
    }

    override fun clear() {
        native.clear()
    }
}
