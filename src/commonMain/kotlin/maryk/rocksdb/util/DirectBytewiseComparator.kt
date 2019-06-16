package maryk.rocksdb.util

import maryk.rocksdb.ComparatorOptions
import maryk.rocksdb.DirectComparator
import maryk.rocksdb.DirectSlice


/**
 * This is a Java Native implementation of the C++
 * equivalent BytewiseComparatorImpl using [DirectSlice]
 *
 * The performance of Comparators implemented in Java is always
 * less than their C++ counterparts due to the bridging overhead,
 * as such you likely don't want to use this apart from benchmarking
 * and you most likely instead wanted
 * [maryk.rocksdb.BuiltinComparator.BYTEWISE_COMPARATOR]
 */
expect class DirectBytewiseComparator(copt: ComparatorOptions) : DirectComparator {
    override fun name(): String

    override fun compare(a: DirectSlice, b: DirectSlice): Int

    override fun findShortestSeparator(
        start: String,
        limit: DirectSlice
    ): String?

    override fun findShortSuccessor(key: String): String?
}
