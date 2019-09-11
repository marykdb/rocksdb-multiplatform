package maryk.rocksdb

import kotlinx.cinterop.CPointer

actual class PersistentCache actual constructor(
    env: Env,
    path: String,
    size: Long,
    logger: Logger,
    optimizedForNvm: Boolean
) : RocksObject(
    newPersistentCache(
        env.nativeHandle,
        path, size,
        logger.nativeHandle,
        optimizedForNvm
    )
)

fun newPersistentCache(
    env: CPointer<*>,
    path: String,
    size: Long,
    logger: CPointer<*>,
    optimizedForNvm: Boolean
): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
