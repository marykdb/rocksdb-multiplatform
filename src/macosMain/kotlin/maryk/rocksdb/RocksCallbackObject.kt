package maryk.rocksdb

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.nativeHeap

actual abstract class RocksCallbackObject
    protected constructor(
        vararg nativeParameterHandles: CPointer<*>
    )
: AbstractImmutableNativeReference(true) {
    @Suppress("LeakingThis")
    internal var nativeHandle = initializeNative(*nativeParameterHandles)

    /**
     * Construct the Native C++ object which will callback
     * to our object methods
     *
     * @param nativeParameterHandles An array of native handles for any parameter
     * objects that are needed during construction
     *
     * @return The native handle of the C++ object which will callback to us
     */
    protected abstract fun initializeNative(
        vararg nativeParameterHandles: CPointer<*>
    ): CPointer<*>

    /** Deletes underlying C++ native callback object pointer */
    override fun disposeInternal() {
        nativeHandle?.let {
            disposeInternal(nativeHandle)
        }
    }

    private fun disposeInternal(handle: CPointer<*>) {
        nativeHeap.free(handle.rawValue)
    }
}
