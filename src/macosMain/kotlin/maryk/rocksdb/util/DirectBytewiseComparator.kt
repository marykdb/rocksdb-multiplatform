package maryk.rocksdb.util

import maryk.rocksdb.ComparatorOptions
import maryk.rocksdb.DirectComparator
import maryk.rocksdb.DirectSlice

actual class DirectBytewiseComparator actual constructor(copt: ComparatorOptions) : DirectComparator(copt) {
    actual override fun name(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun compare(a: DirectSlice, b: DirectSlice): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun findShortestSeparator(start: String, limit: DirectSlice): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual override fun findShortSuccessor(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
