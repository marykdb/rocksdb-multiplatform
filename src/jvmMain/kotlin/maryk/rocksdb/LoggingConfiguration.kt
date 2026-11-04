@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package maryk.rocksdb

actual fun Options.setLogger(logger: Logger): Options {
    check(isOwningHandle()) { "Options handle is closed" }
    check(logger.isOwningHandle()) { "Logger handle is closed" }
    return setLogger(logger as org.rocksdb.LoggerInterface)
}

actual fun DBOptions.setLogger(logger: Logger): DBOptions {
    check(isOwningHandle()) { "Options handle is closed" }
    check(logger.isOwningHandle()) { "Logger handle is closed" }
    return setLogger(logger as org.rocksdb.LoggerInterface)
}
