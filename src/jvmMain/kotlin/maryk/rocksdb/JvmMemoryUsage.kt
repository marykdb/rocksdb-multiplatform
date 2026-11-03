@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package maryk.rocksdb

actual fun Options.setWriteBufferManager(manager: WriteBufferManager): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(manager.isOwningHandle()) { "Write buffer manager handle is closed" }
    return setWriteBufferManager(manager)
}

actual fun DBOptions.setWriteBufferManager(manager: WriteBufferManager): DBOptions {
    check(isOwningHandle()) { "DB options handle is closed" }
    check(manager.isOwningHandle()) { "Write buffer manager handle is closed" }
    return setWriteBufferManager(manager)
}

internal actual fun collectMemoryUsage(
    databases: List<RocksDB>,
    caches: List<Cache>,
): Map<MemoryUsageType, Long> {
    val values = org.rocksdb.MemoryUtil.getApproximateMemoryUsageByType(
        databases.distinctBy { it.nativeHandle },
        caches.distinctBy { it.nativeHandle }.toSet(),
    )
    return mapOf(
        MemoryUsageType.kMemTableTotal to (values[MemoryUsageType.kMemTableTotal] ?: 0L),
        MemoryUsageType.kMemTableUnFlushed to (values[MemoryUsageType.kMemTableUnFlushed] ?: 0L),
        MemoryUsageType.kTableReadersTotal to (values[MemoryUsageType.kTableReadersTotal] ?: 0L),
        MemoryUsageType.kCacheTotal to (values[MemoryUsageType.kCacheTotal] ?: 0L),
    )
}
