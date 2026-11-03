package maryk.rocksdb

import maryk.toCheckedLong
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_approximate_memory_usage_create
import rocksdb.rocksdb_approximate_memory_usage_destroy
import rocksdb.rocksdb_approximate_memory_usage_get_cache_total
import rocksdb.rocksdb_approximate_memory_usage_get_mem_table_readers_total
import rocksdb.rocksdb_approximate_memory_usage_get_mem_table_total
import rocksdb.rocksdb_approximate_memory_usage_get_mem_table_unflushed
import rocksdb.rocksdb_memory_consumers_add_cache
import rocksdb.rocksdb_memory_consumers_add_db
import rocksdb.rocksdb_memory_consumers_create
import rocksdb.rocksdb_memory_consumers_destroy

actual fun Options.setWriteBufferManager(manager: WriteBufferManager): Options =
    attachWriteBufferManager(manager)

actual fun DBOptions.setWriteBufferManager(manager: WriteBufferManager): DBOptions =
    attachWriteBufferManager(manager)

internal actual fun collectMemoryUsage(
    databases: List<RocksDB>,
    caches: List<Cache>,
): Map<MemoryUsageType, Long> {
    check(databases.none { it is BorrowedOptimisticBaseDB }) { "Database handle is borrowed" }
    val consumers = requireNotNull(rocksdb_memory_consumers_create()) { "Unable to allocate memory consumers" }
    try {
        databases.distinctBy { it.native }.forEach { rocksdb_memory_consumers_add_db(consumers, it.native) }
        caches.distinctBy { it.native }.forEach { rocksdb_memory_consumers_add_cache(consumers, it.native) }
        val usage = requireNotNull(consumers.wrapWithNullErrorThrower { error ->
            rocksdb_approximate_memory_usage_create(consumers, error)
        }) { "Unable to collect memory usage" }
        try {
            return mapOf(
                MemoryUsageType.kMemTableTotal to rocksdb_approximate_memory_usage_get_mem_table_total(usage)
                    .toCheckedLong("total memtable memory"),
                MemoryUsageType.kMemTableUnFlushed to rocksdb_approximate_memory_usage_get_mem_table_unflushed(usage)
                    .toCheckedLong("unflushed memtable memory"),
                MemoryUsageType.kTableReadersTotal to rocksdb_approximate_memory_usage_get_mem_table_readers_total(usage)
                    .toCheckedLong("table reader memory"),
                MemoryUsageType.kCacheTotal to rocksdb_approximate_memory_usage_get_cache_total(usage)
                    .toCheckedLong("cache memory"),
            )
        } finally {
            rocksdb_approximate_memory_usage_destroy(usage)
        }
    } finally {
        rocksdb_memory_consumers_destroy(consumers)
    }
}
