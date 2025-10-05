package maryk.rocksdb

actual class WriteBufferManager : org.rocksdb.WriteBufferManager {
    actual constructor(bufferSize: Long, cache: Cache, allowStall: Boolean) :
        super(bufferSize, cache, allowStall)

    actual constructor(bufferSize: Long, cache: Cache) :
        super(bufferSize, cache)

    actual override fun allowStall(): Boolean = super.allowStall()
}
