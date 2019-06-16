package maryk.rocksdb

/**
 * Slices are used by RocksDB to provide
 * efficient access to keys and values.
 *
 * This class is package private, implementers
 * should extend either of the public abstract classes:
 * @see Slice
 * @see DirectSlice
 */
expect abstract class AbstractSlice<T> : RocksMutableObject {
    /**
     * Returns the data of the slice.
     *
     * @return The slice data. Note, the type of access is
     * determined by the subclass
     * @see AbstractSlice.data0
     */
    fun data(): T

    /**
     * Drops the specified `n`
     * number of bytes from the start
     * of the backing slice
     *
     * @param n The number of bytes to drop
     */
    abstract fun removePrefix(n: Int)

    /**
     * Clears the backing slice
     */
    abstract fun clear()

    /**
     * Return the length (in bytes) of the data.
     *
     * @return The length in bytes.
     */
    fun size(): Int

    /**
     * Return true if the length of the
     * data is zero.
     *
     * @return true if there is no data, false otherwise.
     */
    fun empty(): Boolean

    /**
     * Creates a string representation of the data
     *
     * @param hex When true, the representation
     * will be encoded in hexadecimal.
     *
     * @return The string representation of the data.
     */
    fun toString(hex: Boolean): String

    override fun toString(): String

    /**
     * Three-way key comparison
     *
     * @param other A slice to compare against
     *
     * @return Should return either:
     * 1) < 0 if this < other
     * 2) == 0 if this == other
     * 3) > 0 if this > other
     */
    fun compare(other: AbstractSlice<*>?): Int

    override fun hashCode(): Int

    /**
     * If other is a slice object, then
     * we defer to [compare][.compare]
     * to check equality, otherwise we return false.
     *
     * @param other Object to test for equality
     *
     * @return true when `this.compare(other) == 0`,
     * false otherwise.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Determines whether this slice starts with
     * another slice
     *
     * @param prefix Another slice which may of may not
     * be a prefix of this slice.
     *
     * @return true when this slice starts with the
     * `prefix` slice
     */
    fun startsWith(prefix: AbstractSlice<*>?): Boolean
}
