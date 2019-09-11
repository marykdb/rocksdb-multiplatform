package maryk.rocksdb

enum class ComparatorType(
    private val value: Byte
) {
    JAVA_COMPARATOR(0x0),
    JAVA_DIRECT_COMPARATOR(0x1),
    JAVA_NATIVE_COMPARATOR_WRAPPER(0x2);

    /** Returns the byte value of the enumerations value. */
    fun getValue() = value
}
