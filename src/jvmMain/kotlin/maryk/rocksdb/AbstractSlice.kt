package maryk.rocksdb

/**
 * Regards the lifecycle of Java Slices in RocksDB:
 * At present when you configure a Comparator from Java, it creates an
 * instance of a C++ BaseComparatorJniCallback subclass and
 * passes that to RocksDB as the comparator. That subclass of
 * BaseComparatorJniCallback creates the Java
 *
 * @see AbstractSlice subclass Objects. When you dispose
 * the Java @see AbstractComparator subclass, it disposes the
 * C++ BaseComparatorJniCallback subclass, which in turn destroys the
 * Java @see maryk.rocksdb.AbstractSlice subclass Objects.
 */
actual typealias AbstractSlice<T> = org.rocksdb.AbstractSlice<T>
