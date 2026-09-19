package maryk.rocksdb

/**
 * RocksObject is an implementation of [AbstractNativeReference] which
 * has an immutable and therefore thread-safe reference to the underlying
 * native C++ RocksDB object.
 *
 * RocksObject is the base-class of almost all RocksDB classes that have a
 * pointer to some underlying native C++ `rocksdb` object.
 *
 * The use of `RocksObject` should always be preferred over
 * [RocksMutableObject].
 */
expect abstract class RocksObject : AbstractImmutableNativeReference
