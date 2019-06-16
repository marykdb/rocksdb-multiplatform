package maryk.rocksdb

/**
 * AbstractNativeReference is the base-class of all RocksDB classes that have
 * a pointer to a native C++ {@code rocksdb} object.
 *
 * AbstractNativeReference has the {@link AbstractNativeReference#dispose()}
 * method, which frees its associated C++ object.</p>
 *
 * This function should be called manually, however, if required it will be
 * called automatically during the regular Java GC process via
 * {@link AbstractNativeReference#finalize()}.</p>
 *
 * Note - Java can only see the long member variable (which is the C++ pointer
 * value to the native object), as such it does not know the real size of the
 * object and therefore may assign a low GC priority for it; So it is strongly
 * suggested that you manually dispose of objects when you are finished with
 * them.
 */
expect abstract class AbstractNativeReference : AutoCloseable {
    /**
     * Returns true if we are responsible for freeing the underlying C++ object
     *
     * @return true if we are responsible to free the C++ object
     * @see #dispose()
     */
    protected abstract fun isOwningHandle(): Boolean
}
