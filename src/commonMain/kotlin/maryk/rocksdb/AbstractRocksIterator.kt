package maryk.rocksdb

/**
 * Base class implementation for Rocks Iterators
 * in the Java API
 *
 * Multiple threads can invoke const methods on an RocksIterator without
 * external synchronization, but if any of the threads may call a
 * non-const method, all threads accessing the same RocksIterator must use
 * external synchronization.
 *
 * @param <P> The type of the Parent Object from which the Rocks Iterator was
 * created. This is used by disposeInternal to avoid double-free
 * issues with the underlying C++ object.
 * @see RocksObject
 */
expect abstract class AbstractRocksIterator<P : RocksObject> : RocksObject, RocksIteratorInterface {
    /**
     * An iterator is either positioned at an entry, or
     * not valid.  This method returns true if the iterator is valid.
     *
     * @return true if iterator is valid.
     */
    override fun isValid(): Boolean

    /**
     * Position at the first entry in the source.  The iterator is Valid()
     * after this call if the source is not empty.
     */
    override fun seekToFirst()

    /**
     * Position at the last entry in the source.  The iterator is
     * valid after this call if the source is not empty.
     */
    override fun seekToLast()

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
    override fun seek(target: ByteArray)

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
    override fun seekForPrev(target: ByteArray)

    /**
     * Moves to the next entry in the source.  After this call, Valid() is
     * true if the iterator was not positioned at the last entry in the source.
     *
     * REQUIRES: [.isValid]
     */
    override fun next()

    /**
     * Moves to the previous entry in the source.  After this call, Valid() is
     * true if the iterator was not positioned at the first entry in source.
     *
     * REQUIRES: [.isValid]
     */
    override fun prev()

    /**
     * If an error has occurred, return it.  Else return an ok status.
     * If non-blocking IO is requested and this operation cannot be
     * satisfied without doing some IO, then this returns Status::Incomplete().
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    override fun status()
}
