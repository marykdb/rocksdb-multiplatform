package maryk.rocksdb

/** Persistent cache for caching IO pages on persistent media. */
expect class PersistentCache(
    env: Env,
    path: String,
    size: Long,
    logger: Logger,
    optimizedForNvm: Boolean
) : RocksObject
