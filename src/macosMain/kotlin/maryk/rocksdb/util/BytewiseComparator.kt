package maryk.rocksdb.util

import maryk.rocksdb.Comparator
import maryk.rocksdb.ComparatorOptions
import maryk.rocksdb.Slice

actual open class BytewiseComparator actual constructor(copt: ComparatorOptions) :
    Comparator(copt) {
    override fun name() = "rocksdb.java.BytewiseComparator"

    override fun compare(a: Slice, b: Slice): Int {
        return compare(a.data(), b.data())
    }
}

private fun compare(a: ByteArray, b: ByteArray): Int {
    TODO("implement")
}
