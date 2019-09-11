package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual abstract class RocksMutableObject
    protected constructor(
        private var nativeHandle: CPointer<*>?,
        private var owningHandle: Boolean
    )
: AbstractNativeReference() {
    protected constructor() : this(null, false)

    protected constructor(nativeHandle: CPointer<*>): this(nativeHandle, true)

    /**
     * Closes the existing handle, and changes the handle to the new handle
     *
     * @param newNativeHandle The C++ pointer to the new native object
     * @param owningNativeHandle true if we own the new native object
     */
    fun resetNativeHandle(
        newNativeHandle: CPointer<*>,
        owningNativeHandle: Boolean
    ) {
        close()
        setNativeHandle(newNativeHandle, owningNativeHandle)
    }

    /**
     * Sets the handle (C++ pointer) of the underlying C++ native object
     *
     * @param nativeHandle The C++ pointer to the native object
     * @param owningNativeHandle true if we own the native object
     */
    fun setNativeHandle(
        nativeHandle: CPointer<*>,
        owningNativeHandle: Boolean
    ) {
        this.nativeHandle = nativeHandle
        this.owningHandle = owningNativeHandle
    }

    override fun isOwningHandle() = this.owningHandle

    /**
     * Gets the value of the C++ pointer pointing to the underlying
     * native C++ object
     *
     * @return the pointer value for the native object
     */
    protected fun getNativeHandle(): CPointer<*> {
        assert(this.nativeHandle != null)
        return this.nativeHandle!!
    }

    override fun close() {
        if (isOwningHandle()) {
            disposeInternal()
            this.owningHandle = false
            this.nativeHandle = null
        }
    }

    protected fun disposeInternal() {
        nativeHandle?.let { disposeInternal(it) }
    }

    protected abstract fun disposeInternal(handle: CPointer<*>)
}
