package maryk.rocksdb

/**
 * Persistent cache for caching IO pages on a persistent medium. The
 * cache is specifically designed for persistent read cache.
 */
expect class PersistentCache(
    env: Env,
    path: String,
    size: Long,
    logger: Logger,
    optimizedForNvm: Boolean
) : RocksObject
