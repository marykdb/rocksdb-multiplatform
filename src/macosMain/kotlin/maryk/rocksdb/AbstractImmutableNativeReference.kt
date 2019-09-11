package maryk.rocksdb

import kotlin.native.concurrent.AtomicReference

actual abstract class AbstractImmutableNativeReference
    private constructor(
        /**
         * A flag indicating whether the current {@code AbstractNativeReference} is
         * responsible to free the underlying C++ object
         */
        protected val owningHandle: AtomicReference<Boolean>
    )
: AbstractNativeReference() {
    protected constructor(owningHandle: Boolean) : this(AtomicReference(owningHandle))

    public actual override fun isOwningHandle() = this.owningHandle.value

    actual override fun close() {
        if (owningHandle.compareAndSet(true, false)) {
            disposeInternal()
        }
    }

    /**
     * Releases this `AbstractNativeReference` from  the responsibility of
     * freeing the underlying native C++ object
     *
     * This will prevent the object from attempting to delete the underlying
     * native object in its finalizer. This must be used when another object
     * takes over ownership of the native object or both will attempt to delete
     * the underlying object when garbage collected.
     *
     * When `disOwnNativeHandle()` is called, `dispose()` will
     * subsequently take no action. As a result, incorrect use of this function
     * may cause a memory leak.
     *
     * @see .dispose
     */
    protected fun disOwnNativeHandle() {
        owningHandle.compareAndSet(true, false)
    }

    /**
     * The helper function of [AbstractImmutableNativeReference.dispose]
     * which all subclasses of `AbstractImmutableNativeReference` must
     * implement to release their underlying native C++ objects.
     */
    protected abstract fun disposeInternal()
}
