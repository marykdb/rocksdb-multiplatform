package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class Cache
    protected constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle)
