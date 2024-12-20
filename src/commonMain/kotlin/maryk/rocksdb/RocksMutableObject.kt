package maryk.rocksdb

/**
 * RocksMutableObject is an implementation of [AbstractNativeReference]
 * whose reference to the underlying native C++ object can change.
 *
 * The use of `RocksMutableObject` should be kept to a minimum, as it
 * has synchronization overheads and introduces complexity. Instead it is
 * recommended to use [RocksObject] where possible.
 */
expect abstract class RocksMutableObject : AbstractNativeReference {
    override final fun close()
}
