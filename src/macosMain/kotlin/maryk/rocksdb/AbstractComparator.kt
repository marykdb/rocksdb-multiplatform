package maryk.rocksdb

actual abstract class AbstractComparator<T : AbstractSlice<*>> : RocksCallbackObject() {
    actual abstract fun name(): String

    actual abstract fun compare(a: T, b: T): Int

    actual open fun findShortestSeparator(start: String, limit: T): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual open fun findShortSuccessor(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Get the type of this comparator.
     * Used for determining the correct C++ cast in native code.
     *
     * @return The type of the comparator.
     */
    internal abstract fun getComparatorType(): ComparatorType
}
