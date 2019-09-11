package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class MergeOperator
    protected constructor(nativeHandle: CPointer<*>)
: RocksObject(nativeHandle)
