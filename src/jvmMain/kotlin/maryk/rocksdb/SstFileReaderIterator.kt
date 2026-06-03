package maryk.rocksdb

import maryk.ByteBuffer

actual class SstFileReaderIterator internal constructor(
    private val delegate: org.rocksdb.SstFileReaderIterator
) : RocksObject(0L), RocksIteratorInterface {
    override fun isValid(): Boolean = delegate.isValid

    override fun seekToFirst() {
        delegate.seekToFirst()
    }

    override fun seekToLast() {
        delegate.seekToLast()
    }

    override fun seek(target: ByteArray) {
        delegate.seek(target)
    }

    override fun seekForPrev(target: ByteArray) {
        delegate.seekForPrev(target)
    }

    override fun seek(target: ByteBuffer) {
        delegate.seek(target)
    }

    override fun seekForPrev(target: ByteBuffer) {
        delegate.seekForPrev(target)
    }

    override fun next() {
        delegate.next()
    }

    override fun prev() {
        delegate.prev()
    }

    override fun status() {
        delegate.status()
    }

    override fun refresh() {
        delegate.refresh()
    }

    override fun refresh(snapshot: Snapshot) {
        delegate.refresh(snapshot)
    }

    actual fun key(): ByteArray = delegate.key()

    actual fun value(): ByteArray = delegate.value()

    override fun close() {
        delegate.close()
        super.close()
    }

    override fun disposeInternal(handle: Long) = Unit
}
