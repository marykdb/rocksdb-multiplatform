package maryk.rocksdb

actual typealias AbstractComparator = org.rocksdb.AbstractComparator

actual fun AbstractComparator.getComparatorType(): ComparatorType {
    val method = org.rocksdb.AbstractComparator::class.java.getDeclaredMethod("getComparatorType")
    method.isAccessible = true
    val value = method.invoke(this) as Enum<*>
    return when (value.name) {
        "JAVA_COMPARATOR" -> ComparatorType.JAVA_COMPARATOR
        "JAVA_NATIVE_COMPARATOR_WRAPPER" -> ComparatorType.JAVA_NATIVE_COMPARATOR_WRAPPER
        else -> error("Unknown comparator type ${value.name}")
    }
}
