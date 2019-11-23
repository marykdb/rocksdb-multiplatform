package maryk.rocksdb

/**
 * Comparators are used by RocksDB to determine
 * the ordering of keys.
 *
 * This class is package private, implementers
 * should extend either of the public abstract classes:
 * @see maryk.rocksdb.Comparator
 *
 * @see maryk.rocksdb.DirectComparator
 */
expect abstract class AbstractComparator<T : AbstractSlice<*>> : RocksCallbackObject {
    /**
     * The name of the comparator.  Used to check for comparator
     * mismatches (i.e., a DB created with one comparator is
     * accessed using a different comparator).
     *
     * A new name should be used whenever
     * the comparator implementation changes in a way that will cause
     * the relative ordering of any two keys to change.
     *
     * Names starting with "rocksdb." are reserved and should not be used.
     *
     * @return The name of this comparator implementation
     */
    abstract fun name(): String

    /**
     * Three-way key comparison
     *
     * @param a Slice access to first key
     * @param b Slice access to second key
     *
     * @return Should return either:
     * 1) < 0 if "a" < "b"
     * 2) == 0 if "a" == "b"
     * 3) > 0 if "a" > "b"
     */
    abstract fun compare(a: T, b: T): Int

    /**
     * Used to reduce the space requirements
     * for internal data structures like index blocks.
     *
     * If start < limit, you may return a new start which is a
     * shorter string in [start, limit).
     *
     * Simple comparator implementations may return null if they
     * wish to use start unchanged. i.e., an implementation of
     * this method that does nothing is correct.
     *
     * @param start String
     * @param limit of type T
     *
     * @return a shorter start, or null
     */
    open fun findShortestSeparator(start: String, limit: T): String?

    /**
     * Used to reduce the space requirements
     * for internal data structures like index blocks.
     *
     * You may return a new short key (key1) where
     * key1  key.
     *
     * Simple comparator implementations may return null if they
     * wish to leave the key unchanged. i.e., an implementation of
     * this method that does nothing is correct.
     *
     * @param key String
     *
     * @return a shorter key, or null
     */
    open fun findShortSuccessor(key: String): String?

    override fun close()
}
