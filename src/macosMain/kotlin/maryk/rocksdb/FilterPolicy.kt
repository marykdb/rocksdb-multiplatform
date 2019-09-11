package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class FilterPolicy(nativeHandle: CPointer<*>) : RocksObject(nativeHandle)
