package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class AbstractRocksIterator<P : RocksObject>
    protected constructor(
        val parent: P,
        nativeHandle: CPointer<*>
    )
: RocksObject(nativeHandle), RocksIteratorInterface
