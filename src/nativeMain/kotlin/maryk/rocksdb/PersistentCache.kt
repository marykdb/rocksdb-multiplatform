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
    private val loggerRef = logger

    internal val native: CPointer<rocksdb_persistent_cache_t> =
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

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_persistent_cache_destroy(native)
            super.close()
        }
    }
}
