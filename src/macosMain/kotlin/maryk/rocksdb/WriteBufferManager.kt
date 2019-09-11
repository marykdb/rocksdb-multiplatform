package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class WriteBufferManager actual constructor(bufferSizeBytes: Long, cache: Cache) : RocksObject(newWriteBufferManager(bufferSizeBytes, cache.nativeHandle))

fun newWriteBufferManager(bufferSizeBytes: Long, nativeHandle: CPointer<*>): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
