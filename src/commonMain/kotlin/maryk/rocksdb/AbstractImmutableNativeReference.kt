package maryk.rocksdb

/**
 * Offers functionality for implementations of
 * {@link AbstractNativeReference} which have an immutable reference to the
 * underlying native C++ object
 */
expect abstract class AbstractImmutableNativeReference : AbstractNativeReference {
    public override fun isOwningHandle(): Boolean

    override fun close()
}
