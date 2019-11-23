package maryk.rocksdb

import rocksdb.RocksDBComparator

actual abstract class AbstractComparator<T : AbstractSlice<*>> : RocksCallbackObject() {
    internal abstract val native: RocksDBComparator

    actual abstract fun name(): String

    actual abstract fun compare(a: T, b: T): Int

    actual open fun findShortestSeparator(start: String, limit: T): String? {
        return null
    }

    actual open fun findShortSuccessor(key: String): String? {
        return null
    }
}
