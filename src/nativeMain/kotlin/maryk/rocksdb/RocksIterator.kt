package maryk.rocksdb

import maryk.toByteArray
import maryk.toNSData
import maryk.wrapWithErrorThrower
import rocksdb.RocksDBIterator

actual class RocksIterator internal constructor(
    internal val native: RocksDBIterator
) : AbstractRocksIterator<RocksDB>() {
    actual fun key(): ByteArray {
        return native.key().toByteArray()
    }

    actual fun value(): ByteArray {
        return native.value().toByteArray()
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
