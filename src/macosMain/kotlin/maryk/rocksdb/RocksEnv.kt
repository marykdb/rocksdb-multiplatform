package maryk.rocksdb

import rocksdb.RocksDBEnv

/**
 * A RocksEnv is an interface used by the rocksdb implementation to access
 * operating system functionality like the filesystem etc.
 *
 * All Env implementations are safe for concurrent access from
 * multiple threads without any external synchronization.
 */
class RocksEnv
    /**
     * Internal constructor that uses the specified native handle
     * to construct a RocksEnv.
     *
     * Note that the ownership of the input handle
     * belongs to the caller, and the newly created RocksEnv will not take
     * the ownership of the input handle.  As a result, calling
     * `dispose()` of the created RocksEnv will be no-op.
     */
    internal constructor(native: RocksDBEnv) : Env(native) {

    internal constructor() : this(RocksDBEnv())

    override fun equals(other: Any?): Boolean {
        if (other !is RocksEnv) return false
        if (this.native === other.native) return true
        return true
    }

    override fun hashCode(): Int {
        return this.native.hashCode()
    }
}
