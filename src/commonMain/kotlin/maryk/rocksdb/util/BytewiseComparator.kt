package maryk.rocksdb.util

import maryk.rocksdb.ComparatorOptions

/**
 * This is a Java Native implementation of the C++
 * equivalent BytewiseComparatorImpl using [Slice]
 *
 * The performance of Comparators implemented in Java is always
 * less than their C++ counterparts due to the bridging overhead,
 * as such you likely don't want to use this apart from benchmarking
 * and you most likely instead wanted
 * [org.rocksdb.BuiltinComparator.BYTEWISE_COMPARATOR]
 */
expect open class BytewiseComparator(copt: ComparatorOptions) : maryk.rocksdb.Comparator
