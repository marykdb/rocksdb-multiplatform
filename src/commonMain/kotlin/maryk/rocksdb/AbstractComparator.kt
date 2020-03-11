package maryk.rocksdb

import maryk.ByteBuffer

/**
 * Comparators are used by RocksDB to determine
 * the ordering of keys.
 */
expect abstract class AbstractComparator protected constructor() : RocksCallbackObject {
    protected constructor(
        copt: ComparatorOptions?
    )

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
     * Three-way key comparison. Implementations should provide a
     * <a href="https://en.wikipedia.org/wiki/Total_order">total order</a>
     * on keys that might be passed to it.
     *
     * The implementation may modify the {@code ByteBuffer}s passed in, though
     * it would be unconventional to modify the "limit" or any of the
     * underlying bytes. As a callback, RocksJava will ensure that {@code a}
     * is a different instance from {@code b}.
     *
     * @param a buffer containing the first key in its "remaining" elements
     * @param b buffer containing the second key in its "remaining" elements
     *
     * @return Should return either:
     *    1) &lt; 0 if "a" &lt; "b"
     *    2) == 0 if "a" == "b"
     *    3) &gt; 0 if "a" &gt; "b"
     */
    abstract fun compare(a: ByteBuffer, b: ByteBuffer): Int

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
    open fun findShortestSeparator(start: ByteBuffer, limit: ByteBuffer)

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
    open fun findShortSuccessor(key: ByteBuffer)

    override fun close()
}
