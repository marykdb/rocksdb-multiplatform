package maryk.rocksdb

/**
 * Defines the interface for an Iterator which provides
 * access to data one entry at a time. Multiple implementations
 * are provided by this library.  In particular, iterators are provided
 * to access the contents of a DB and Write Batch.
 *
 * Multiple threads can invoke const methods on an RocksIterator without
 * external synchronization, but if any of the threads may call a
 * non-const method, all threads accessing the same RocksIterator must use
 * external synchronization.
 *
 * @see maryk.rocksdb.RocksObject
 */
expect interface RocksIteratorInterface {
    /**
     * An iterator is either positioned at an entry, or
     * not valid.  This method returns true if the iterator is valid.
     *
     * @return true if iterator is valid.
     */
    fun isValid(): Boolean

    /**
     * Position at the first entry in the source.  The iterator is Valid()
     * after this call if the source is not empty.
     */
    fun seekToFirst()

    /**
     * Position at the last entry in the source.  The iterator is
     * valid after this call if the source is not empty.
     */
    fun seekToLast()

    /**
     * Position at the first entry in the source whose key is that or
     * past target.
     *
     * The iterator is valid after this call if the source contains
     * a key that comes at or past target.
     *
     * @param target byte array describing a key or a
     * key prefix to seek for.
     */
    fun seek(target: ByteArray)

    /**
     * Position at the first entry in the source whose key is that or
     * before target.
     *
     * The iterator is valid after this call if the source contains
     * a key that comes at or before target.
     *
     * @param target byte array describing a key or a
     * key prefix to seek for.
     */
    fun seekForPrev(target: ByteArray)

    /**
     * Moves to the next entry in the source.  After this call, Valid() is
     * true if the iterator was not positioned at the last entry in the source.
     *
     * REQUIRES: [.isValid]
     */
    fun next()

    /**
     * Moves to the previous entry in the source.  After this call, Valid() is
     * true if the iterator was not positioned at the first entry in source.
     *
     * REQUIRES: [.isValid]
     */
    fun prev()

    /**
     * If an error has occurred, return it.  Else return an ok status.
     * If non-blocking IO is requested and this operation cannot be
     * satisfied without doing some IO, then this returns Status::Incomplete().
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    fun status()
}
