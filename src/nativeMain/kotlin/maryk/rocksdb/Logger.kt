package maryk.rocksdb

import cnames.structs.rocksdb_logger_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import rocksdb.rocksdb_logger_create
import rocksdb.rocksdb_logger_destroy

actual abstract class Logger : RocksCallbackObject() {
    private val stableRef = StableRef.create(this)

    internal val native: CPointer<rocksdb_logger_t> = rocksdb_logger_create(
        stableRef.asCPointer(),
        staticCFunction(::loggerDestructor),
        staticCFunction(::loggerLogCallback),
    ) ?: error("Unable to allocate RocksDB logger")

    protected open fun log(level: InfoLogLevel, message: String) {}

    internal fun dispatchLog(level: InfoLogLevel, message: String) = log(level, message)

    override fun close() {
        if (isOwningHandle()) {
            rocksdb_logger_destroy(native)
            super.close()
        }
    }
}

private fun loggerDestructor(state: COpaquePointer?) {
    state?.asStableRef<Logger>()?.dispose()
}

private fun loggerLogCallback(state: COpaquePointer?, level: Int, message: COpaquePointer?) {
    val logger = state?.asStableRef<Logger>()?.get() ?: return
    val kotlinMessage = message
        ?.reinterpret<ByteVar>()
        ?.toKString()
        .orEmpty()
    val infoLogLevel = try {
        getInfoLogLevel(level.toUByte())
    } catch (_: IllegalArgumentException) {
        InfoLogLevel.INFO_LEVEL
    }
    logger.dispatchLog(infoLogLevel, kotlinMessage)
}
