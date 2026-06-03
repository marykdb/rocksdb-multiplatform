package maryk.rocksdb

actual class SstFileReader actual constructor(
    options: Options
) : RocksObject(0L) {
    internal val delegate = org.rocksdb.SstFileReader(options)

    actual fun open(filePath: String) {
        delegate.open(filePath)
    }

    actual fun newIterator(readOptions: ReadOptions): SstFileReaderIterator =
        SstFileReaderIterator(delegate.newIterator(readOptions))

    actual fun getTableProperties(): TableProperties =
        TableProperties(delegate.tableProperties)

    actual fun verifyChecksum() {
        delegate.verifyChecksum()
    }

    override fun close() {
        delegate.close()
        super.close()
    }

    override fun disposeInternal(handle: Long) = Unit
}
