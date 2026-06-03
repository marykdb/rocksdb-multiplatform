package maryk.rocksdb

/**
 * Iterator over entries in an SST file opened by [SstFileReader].
 */
expect class SstFileReaderIterator : RocksObject, RocksIteratorInterface {
    fun key(): ByteArray

    fun value(): ByteArray
}
