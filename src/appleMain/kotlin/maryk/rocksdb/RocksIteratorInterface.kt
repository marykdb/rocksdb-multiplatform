package maryk.rocksdb

actual interface RocksIteratorInterface {
    actual fun isValid(): Boolean

    actual fun seekToFirst()

    actual fun seekToLast()

    actual fun seek(target: ByteArray)

    actual fun seekForPrev(target: ByteArray)

    actual fun next()

    actual fun prev()

    actual fun status()
}
