package maryk.rocksdb

import maryk.toNSData
import maryk.wrapWithErrorThrower
import rocksdb.RocksDBWriteBatchIterator

actual class WBWIRocksIterator
    internal constructor(
        val native: RocksDBWriteBatchIterator
    )
: AbstractRocksIterator<WriteBatchWithIndex>() {
    actual fun entry(): WriteEntry {
        val entry = native.entry()
        return WriteEntry(
            getWriteTypeByValue(entry.type),
            DirectSlice(entry.key),
            DirectSlice(entry.value)
        )
    }

    override fun isValid(): Boolean {
        return native.isValid()
    }

    override fun seekToFirst() {
        native.seekToFirst()
    }

    override fun seekToLast() {
        native.seekToLast()
    }

    override fun seek(target: ByteArray) {
        native.seekToKey(target.toNSData())
    }

    override fun seekForPrev(target: ByteArray) {
        native.seekForPrev(target.toNSData())
    }

    override fun next() {
        native.next()
    }

    override fun prev() {
        native.previous()
    }

    override fun status() {
        wrapWithErrorThrower { error ->
            native.status(error)
        }
    }
}
