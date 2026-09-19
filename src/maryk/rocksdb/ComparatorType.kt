package maryk.rocksdb

/** Types of comparators supported by the RocksDB JNI layer. */
expect enum class ComparatorType {
    JAVA_COMPARATOR,
    JAVA_NATIVE_COMPARATOR_WRAPPER,
}
