package maryk.rocksdb

/** Alternative block cache. Keep it alive until all using databases are closed. */
expect class HyperClockCache(
    capacity: Long,
    estimatedEntryCharge: Long,
    numShardBits: Int,
    strictCapacityLimit: Boolean,
) : Cache
