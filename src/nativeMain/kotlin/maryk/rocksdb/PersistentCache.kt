package maryk.rocksdb

import cnames.structs.rocksdb_persistent_cache_t
import kotlinx.cinterop.CPointer
import maryk.toUByte
import maryk.wrapWithNullErrorThrower
import rocksdb.rocksdb_persistent_cache_create
import rocksdb.rocksdb_persistent_cache_destroy

actual class PersistentCache actual constructor(
    env: Env,
    path: String,
    size: Long,
    logger: Logger,
    optimizedForNvm: Boolean,
) : RocksObject() {
    private var envRef: Env? = env

    internal val native: CPointer<rocksdb_persistent_cache_t> =
        createPersistentCache(env, path, size, logger, optimizedForNvm)

    override fun close() {
        if (tryClose()) {
            rocksdb_persistent_cache_destroy(native)
            envRef = null
            super.close()
        }
    }
}

private fun createPersistentCache(
    env: Env,
    path: String,
    size: Long,
    logger: Logger,
    optimizedForNvm: Boolean,
): CPointer<rocksdb_persistent_cache_t> {
    check(logger.disownHandle()) { "Logger is already closed or registered." }
    return try {
        Unit.wrapWithNullErrorThrower { error ->
            rocksdb_persistent_cache_create(
                env.native,
                path,
                size.toULong(),
                logger.native,
                optimizedForNvm.toUByte(),
                error,
            )
        } ?: error("Unable to create persistent cache at $path")
    } finally {
        logger.closeAfterSharedOwnershipTransfer()
    }
}
