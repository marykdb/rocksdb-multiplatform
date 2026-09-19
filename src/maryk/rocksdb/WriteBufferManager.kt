package maryk.rocksdb

/**
 * Coordinates write buffer memory usage across column families and instances.
 */
expect class WriteBufferManager : RocksObject {
    constructor(bufferSize: Long, cache: Cache, allowStall: Boolean)

    constructor(bufferSize: Long, cache: Cache)

    fun allowStall(): Boolean
}
