package maryk.rocksdb


/**
 * This class controls the behaviour
 * of Java implementations of
 * AbstractComparator
 *
 * Note that dispose() must be called before a ComparatorOptions
 * instance becomes out-of-scope to release the allocated memory in C++.
 */
expect class ComparatorOptions() : RocksObject
