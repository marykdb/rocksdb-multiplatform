package maryk.rocksdb

actual abstract class AbstractRocksIterator<P : RocksObject>
    protected constructor()
: RocksObject(), RocksIteratorInterface {
    /**
     * An iterator is either positioned at an entry, or
     * not valid.  This method returns true if the iterator is valid.
     *
     * @return true if iterator is valid.
     */
    actual override fun isValid(): Boolean {
        throw NotImplementedError()
    }

    /**
     * Position at the first entry in the source.  The iterator is Valid()
     * after this call if the source is not empty.
     */
    actual override fun seekToFirst() {
        throw NotImplementedError()
    }

    /**
     * Position at the last entry in the source.  The iterator is
     * valid after this call if the source is not empty.
     */
    actual override fun seekToLast() {
        throw NotImplementedError()
    }

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
    actual override fun seek(target: ByteArray) {
        throw NotImplementedError()
    }

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
    actual override fun seekForPrev(target: ByteArray) {
        throw NotImplementedError()
    }

    /**
     * Moves to the next entry in the source.  After this call, Valid() is
     * true if the iterator was not positioned at the last entry in the source.
     *
     * REQUIRES: [.isValid]
     */
    actual override fun next() {
        throw NotImplementedError()
    }

    /**
     * Moves to the previous entry in the source.  After this call, Valid() is
     * true if the iterator was not positioned at the first entry in source.
     *
     * REQUIRES: [.isValid]
     */
    actual override fun prev() {
        throw NotImplementedError()
    }

    /**
     * If an error has occurred, return it.  Else return an ok status.
     * If non-blocking IO is requested and this operation cannot be
     * satisfied without doing some IO, then this returns Status::Incomplete().
     *
     * @throws RocksDBException thrown if error happens in underlying
     * native library.
     */
    actual override fun status() {
        throw NotImplementedError()
    }
}
