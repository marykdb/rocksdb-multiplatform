package maryk.rocksdb

/**
 * Offers functionality for implementations of
 * {@link AbstractNativeReference} which have an immutable reference to the
 * underlying native C++ object
 */
expect abstract class AbstractImmutableNativeReference : AbstractNativeReference {
    open fun isOwningHandle(): Boolean

    override fun close()
}
