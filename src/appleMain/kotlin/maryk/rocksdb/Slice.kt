package maryk.rocksdb

import kotlinx.cinterop.AutofreeScope
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import rocksdb.RocksDBSlice

actual class Slice internal constructor(
    nativeCreator: (scope: AutofreeScope) -> RocksDBSlice = { RocksDBSlice() }
) : AbstractSlice<ByteArray>(nativeCreator) {
    actual constructor(str: String) : this({ RocksDBSlice(str) })

    actual constructor(data: ByteArray, offset: Int) : this({ scope -> RocksDBSlice(data.toCValues().getPointer(scope), offset.toULong(), (data.size - offset).toULong()) })

    actual constructor(data: ByteArray) : this({ scope -> RocksDBSlice(data.toCValues().getPointer(scope)) })

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
