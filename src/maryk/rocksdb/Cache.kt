package maryk.rocksdb

expect abstract class Cache : RocksObject {
    /**
     * Returns the memory size for the entries
     * residing in cache.
     *
     * @return cache usage size.
     *
     */
    fun getUsage(): Long

    /**
     * Returns the memory size for the entries
     * being pinned in cache.
     *
     * @return cache pinned usage size.
     *
     */
    fun getPinnedUsage(): Long
}
