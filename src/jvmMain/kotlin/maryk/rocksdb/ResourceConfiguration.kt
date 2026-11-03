@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package maryk.rocksdb

actual fun Options.setRateLimiter(rateLimiter: RateLimiter): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(rateLimiter.isOwningHandle()) { "Resource handle is closed" }
    return setRateLimiter(rateLimiter)
}

actual fun DBOptions.setRateLimiter(rateLimiter: RateLimiter): DBOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    check(rateLimiter.isOwningHandle()) { "Resource handle is closed" }
    return setRateLimiter(rateLimiter)
}

actual fun Options.setSstPartitionerFactory(factory: SstPartitionerFactory): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(factory.isOwningHandle()) { "Resource handle is closed" }
    return setSstPartitionerFactory(factory)
}

actual fun ColumnFamilyOptions.setSstPartitionerFactory(factory: SstPartitionerFactory): ColumnFamilyOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    check(factory.isOwningHandle()) { "Resource handle is closed" }
    return setSstPartitionerFactory(factory)
}

actual fun Options.setCompactionThreadLimiter(limiter: ConcurrentTaskLimiter): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(limiter.isOwningHandle()) { "Resource handle is closed" }
    return setCompactionThreadLimiter(limiter)
}

actual fun ColumnFamilyOptions.setCompactionThreadLimiter(limiter: ConcurrentTaskLimiter): ColumnFamilyOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    check(limiter.isOwningHandle()) { "Resource handle is closed" }
    return setCompactionThreadLimiter(limiter)
}
