package maryk.rocksdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.nativeHeap

actual abstract class RocksObject
    protected constructor(
        /**
         * An immutable reference to the value of the C++ pointer pointing to some
         * underlying native RocksDB C++ object.
         */
        internal val nativeHandle: CPointer<*>
    )
: AbstractImmutableNativeReference(true) {
    /** Deletes underlying C++ object pointer. */
    override fun disposeInternal() {
        disposeInternal(nativeHandle)
    }

    private fun disposeInternal(handle: CPointer<*>) {
        nativeHeap.free(handle.rawValue)
    }
}
