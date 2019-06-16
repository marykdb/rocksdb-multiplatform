package maryk.rocksdb

/**
 * Base class implementation for Rocks Iterators
 * in the Java API
 *
 * Multiple threads can invoke const methods on an RocksIterator without
 * external synchronization, but if any of the threads may call a
 * non-const method, all threads accessing the same RocksIterator must use
 * external synchronization.
 *
 * @param <P> The type of the Parent Object from which the Rocks Iterator was
 * created. This is used by disposeInternal to avoid double-free
 * issues with the underlying C++ object.
 * @see RocksObject
 */
expect abstract class AbstractRocksIterator<P : RocksObject> : RocksObject, RocksIteratorInterface
