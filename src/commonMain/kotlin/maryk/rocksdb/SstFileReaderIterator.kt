package maryk.rocksdb

/**
 * Iterator over entries in an SST file opened by [SstFileReader].
 */
expect class SstFileReaderIterator : RocksObject, RocksIteratorInterface {
    override fun isValid(): Boolean

    override fun seekToFirst()

    override fun seekToLast()

    override fun seek(target: ByteArray)

    override fun seekForPrev(target: ByteArray)

    override fun next()

    override fun prev()

    override fun status()

    fun key(): ByteArray

    fun value(): ByteArray
}
