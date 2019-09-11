package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class WBWIRocksIterator
    protected constructor(
        wbwi: WriteBatchWithIndex,
        nativeHandle: CPointer<*>
    )
: AbstractRocksIterator<WriteBatchWithIndex>(wbwi, nativeHandle) {
    actual fun entry(): WriteEntry {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isValid(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekToFirst() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekToLast() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seek(target: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekForPrev(target: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun next() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prev() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun status() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
