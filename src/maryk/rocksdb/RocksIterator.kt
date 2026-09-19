package maryk.rocksdb

/**
 * An iterator that yields a sequence of key/value pairs from a source.
 * Multiple implementations are provided by this library.
 * In particular, iterators are provided
 * to access the contents of a Table or a DB.
 *
 *
 * Multiple threads can invoke const methods on an RocksIterator without
 * external synchronization, but if any of the threads may call a
 * non-const method, all threads accessing the same RocksIterator must use
 * external synchronization.
 *
 * @see maryk.rocksdb.RocksObject
 */
expect class RocksIterator : AbstractRocksIterator<RocksDB> {
    /**
     * Return the key for the current entry.  The underlying storage for
     * the returned slice is valid only until the next modification of
     * the iterator.
     *
     * REQUIRES: [.isValid]
     *
     * @return key for the current entry.
     */
    fun key(): ByteArray

    /**
     * Return the value for the current entry.  The underlying storage for
     * the returned slice is valid only until the next modification of
     * the iterator.
     *
     * REQUIRES: !AtEnd() &amp;&amp; !AtStart()
     * @return value for the current entry.
     */
    fun value(): ByteArray
}
