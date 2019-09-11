package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class Slice : AbstractSlice<ByteArray> {
    actual constructor(str: String) : super(createNewSliceFromString(str))

    actual constructor(data: ByteArray, offset: Int) : super(createNewSlice0(data, offset))

    actual constructor(data: ByteArray) : super(createNewSlice1(data))

    override fun removePrefix(n: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clear() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun createNewSlice0(data: ByteArray, offset: Int): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun createNewSlice1(data: ByteArray): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
