package maryk.rocksdb.util

import maryk.rocksdb.Comparator
import maryk.rocksdb.ComparatorOptions
import maryk.rocksdb.Slice

actual open class BytewiseComparator actual constructor(copt: ComparatorOptions) : Comparator(copt) {
    override fun name() = "maryk.rocksdb.kotlin.BytewiseComparator"

    override fun compare(a: Slice, b: Slice) = a.compare(b)
}
