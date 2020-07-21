package maryk.rocksdb

import maryk.ByteBuffer
import maryk.DirectByteBuffer
import rocksdb.RocksDBComparator
import rocksdb.RocksDBSlice

actual abstract class AbstractComparator
    protected actual constructor(val copt: ComparatorOptions?)
: RocksCallbackObject() {
    protected actual constructor() : this(null)

    private val sliceToByteArrayComparator: (RocksDBSlice?, RocksDBSlice?) -> Int = { a, b ->
        val bufferA = DirectByteBuffer(a!!.data()!!, a.size().toInt())
        val bufferB = DirectByteBuffer(b!!.data()!!, b.size().toInt())

        compare(bufferA, bufferB)
    }

    @Suppress("LeakingThis")
    val native = RocksDBComparator(name(), sliceToByteArrayComparator)

    actual abstract fun name(): String

    actual abstract fun compare(a: ByteBuffer, b: ByteBuffer): Int

    actual open fun findShortestSeparator(start: ByteBuffer, limit: ByteBuffer) {
        // no opp
    }

    actual open fun findShortSuccessor(key: ByteBuffer) {
        // no opp
    }
}
