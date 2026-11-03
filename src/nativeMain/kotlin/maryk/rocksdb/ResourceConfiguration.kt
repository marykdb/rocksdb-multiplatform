@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package maryk.rocksdb

actual fun Options.setRateLimiter(rateLimiter: RateLimiter): Options {
    checkOwningHandle()
    rateLimiter.checkOwningHandle()
    rocksdb.rocksdb_options_set_ratelimiter(native, rateLimiter.native)
    return this
}

actual fun DBOptions.setRateLimiter(rateLimiter: RateLimiter): DBOptions {
    checkOwningHandle()
    rateLimiter.checkOwningHandle()
    rocksdb.rocksdb_options_set_ratelimiter(native, rateLimiter.native)
    return this
}

actual fun Options.setSstPartitionerFactory(factory: SstPartitionerFactory): Options {
    checkOwningHandle()
    factory.checkOwningHandle()
    rocksdb.rocksdb_options_set_sst_partitioner_factory(native, factory.native)
    return this
}

actual fun ColumnFamilyOptions.setSstPartitionerFactory(factory: SstPartitionerFactory): ColumnFamilyOptions {
    checkOwningHandle()
    factory.checkOwningHandle()
    rocksdb.rocksdb_options_set_sst_partitioner_factory(native, factory.native)
    return this
}

actual fun Options.setCompactionThreadLimiter(limiter: ConcurrentTaskLimiter): Options {
    checkOwningHandle()
    limiter.checkOwningHandle()
    rocksdb.rocksdb_options_set_compaction_thread_limiter(native, limiter.native)
    return this
}

actual fun ColumnFamilyOptions.setCompactionThreadLimiter(limiter: ConcurrentTaskLimiter): ColumnFamilyOptions {
    checkOwningHandle()
    limiter.checkOwningHandle()
    rocksdb.rocksdb_options_set_compaction_thread_limiter(native, limiter.native)
    return this
}
