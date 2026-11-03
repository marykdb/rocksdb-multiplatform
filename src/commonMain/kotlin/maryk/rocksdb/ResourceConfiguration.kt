package maryk.rocksdb

/** Attach a caller-owned shared I/O budget. Close using databases before the limiter. */
expect fun Options.setRateLimiter(rateLimiter: RateLimiter): Options
expect fun DBOptions.setRateLimiter(rateLimiter: RateLimiter): DBOptions

/** Attach a caller-owned factory controlling SST boundaries during compaction. */
expect fun Options.setSstPartitionerFactory(factory: SstPartitionerFactory): Options
expect fun ColumnFamilyOptions.setSstPartitionerFactory(factory: SstPartitionerFactory): ColumnFamilyOptions

/** Attach a caller-owned shared compaction concurrency budget. */
expect fun Options.setCompactionThreadLimiter(limiter: ConcurrentTaskLimiter): Options
expect fun ColumnFamilyOptions.setCompactionThreadLimiter(limiter: ConcurrentTaskLimiter): ColumnFamilyOptions
