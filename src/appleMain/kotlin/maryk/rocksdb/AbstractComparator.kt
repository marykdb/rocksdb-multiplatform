package maryk.rocksdb

import maryk.ByteBuffer
import maryk.DirectByteBuffer
import rocksdb.RocksDBComparator

actual abstract class AbstractComparator
    protected actual constructor(val copt: ComparatorOptions?)
: RocksCallbackObject() {
    protected actual constructor() : this(null)

    @Suppress("LeakingThis")
    val native = RocksDBComparator(name()) { a, b ->
        val bufferA = DirectByteBuffer(a!!.data()!!, a.size().toInt())
        val bufferB = DirectByteBuffer(b!!.data()!!, b.size().toInt())

        this.compare(bufferA, bufferB)
    }

    actual abstract fun name(): String

    actual abstract fun compare(a: ByteBuffer, b: ByteBuffer): Int

    actual open fun findShortestSeparator(start: ByteBuffer, limit: ByteBuffer) {
        // no opp
    }

    actual open fun findShortSuccessor(key: ByteBuffer) {
        // no opp
    }
}
