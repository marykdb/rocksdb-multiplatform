package maryk.rocksdb

import kotlinx.cinterop.CPointer

private const val RATE_BYTES_PER_SEC_DEFAULT: Long = 0
private const val DELETE_EXISTING_TRASH_DEFAULT = true
private const val MAX_TRASH_DB_RATION_DEFAULT = 0.25
private const val BYTES_MAX_DELETE_CHUNK_DEFAULT = (64 * 1024 * 1024).toLong()

actual class SstFileManager : RocksObject {
    actual constructor(env: Env) : this(env, null)

    actual constructor(env: Env, logger: Logger?)
        : this(env, logger, RATE_BYTES_PER_SEC_DEFAULT)

    actual constructor(env: Env, logger: Logger?, rateBytesPerSec: Long)
        : this(env, logger, rateBytesPerSec, MAX_TRASH_DB_RATION_DEFAULT)

    actual constructor(
        env: Env,
        logger: Logger?,
        rateBytesPerSec: Long,
        maxTrashDbRatio: Double
    ) : this(env, logger, rateBytesPerSec, maxTrashDbRatio, BYTES_MAX_DELETE_CHUNK_DEFAULT)

    actual constructor(
        env: Env,
        logger: Logger?,
        rateBytesPerSec: Long,
        maxTrashDbRatio: Double,
        bytesMaxDeleteChunk: Long
    ) : super(
        newSstFileManager(
            env.nativeHandle,
            logger?.nativeHandle,
            rateBytesPerSec, maxTrashDbRatio, bytesMaxDeleteChunk
        )
    )

    actual fun setMaxAllowedSpaceUsage(maxAllowedSpace: Long) {
    }

    actual fun setCompactionBufferSize(compactionBufferSize: Long) {
    }

    actual fun isMaxAllowedSpaceReached(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun isMaxAllowedSpaceReachedIncludingCompactions(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTotalSize(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getTrackedFiles(): Map<String, Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getDeleteRateBytesPerSecond(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setDeleteRateBytesPerSecond(deleteRate: Long) {
    }

    actual fun getMaxTrashDBRatio(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setMaxTrashDBRatio(ratio: Double) {
    }
}

private fun newSstFileManager(
    env: CPointer<*>,
    logger: CPointer<*>?,
    rateBytesPerSec: Long,
    maxTrashDbRatio: Double,
    bytesMaxDeleteChunk: Long
): CPointer<*> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
