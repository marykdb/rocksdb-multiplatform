package maryk.rocksdb

/**
 * Persistent cache for caching IO pages on persistent media.
 * Keep its environment and logger open until all databases, options and caches
 * using them have closed, including after closing the caller's cache handle.
 */
expect class PersistentCache(
    env: Env,
    path: String,
    size: Long,
    logger: Logger,
    optimizedForNvm: Boolean
) : RocksObject
