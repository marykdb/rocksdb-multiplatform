package maryk.rocksdb

/**
 * Attach a shared memtable budget to databases opened with these options.
 * The manager remains caller-owned; close using databases before closing it.
 */
expect fun Options.setWriteBufferManager(manager: WriteBufferManager): Options

/**
 * Attach a shared memtable budget to databases opened with these DB options.
 * The manager remains caller-owned; close using databases before closing it.
 */
expect fun DBOptions.setWriteBufferManager(manager: WriteBufferManager): DBOptions

/**
 * Copy approximate memory usage across [databases] and explicitly supplied [caches].
 * Repeated input references are counted once. Cache usage is reported only for
 * [caches], not caches discovered through databases. The result contains the four
 * usage categories, excluding [MemoryUsageType.kNumUsageTypes], and outlives its inputs.
 * These are not resident-set measurements. Cache accounting can include memtable
 * reservations, so the categories must not be summed as independent allocations.
 *
 * Pass open, owning handles and do not close them during collection. Borrowed
 * transaction base-DB views are not accepted because JNI cannot distinguish a
 * borrowed handle from a closed one through its public ownership API.
 */
fun approximateMemoryUsage(
    databases: Collection<RocksDB> = emptyList(),
    caches: Collection<Cache> = emptyList(),
): Map<MemoryUsageType, Long> {
    val uniqueDatabases = databases.distinct()
    val uniqueCaches = caches.distinct()
    uniqueDatabases.forEach { check(it.isOwningHandle()) { "Database handle is closed or borrowed" } }
    uniqueCaches.forEach { check(it.isOwningHandle()) { "Cache handle is closed or borrowed" } }
    if (uniqueDatabases.isEmpty() && uniqueCaches.isEmpty()) {
        return mapOf(
            MemoryUsageType.kMemTableTotal to 0L,
            MemoryUsageType.kMemTableUnFlushed to 0L,
            MemoryUsageType.kTableReadersTotal to 0L,
            MemoryUsageType.kCacheTotal to 0L,
        )
    }
    return collectMemoryUsage(uniqueDatabases, uniqueCaches)
}

internal expect fun collectMemoryUsage(
    databases: List<RocksDB>,
    caches: List<Cache>,
): Map<MemoryUsageType, Long>
