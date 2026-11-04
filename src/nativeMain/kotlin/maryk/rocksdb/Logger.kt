@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class, kotlinx.cinterop.UnsafeNumber::class)

package maryk.rocksdb

import cnames.structs.rocksdb_logger_t
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.staticCFunction
import maryk.sizeTToInt
import platform.posix.size_t
import rocksdb.rocksdb_logger_create_callback_logger
import rocksdb.rocksdb_logger_destroy
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicReference

actual abstract class Logger protected constructor(
    private val logLevel: InfoLogLevel,
) : RocksCallbackObject() {
    protected constructor() : this(InfoLogLevel.INFO_LEVEL)
    private val callbackState = LoggerCallbacks.register(this)

    internal val native: CPointer<rocksdb_logger_t> = try {
        rocksdb_logger_create_callback_logger(
            logLevel.value.toInt(),
            staticCFunction(::loggerLogCallback),
            callbackState,
        ) ?: error("Unable to allocate RocksDB logger")
    } catch (throwable: Throwable) {
        LoggerCallbacks.unregister(callbackState)
        throw throwable
    }

    protected open fun log(level: InfoLogLevel, message: String) {}

    internal fun dispatchLog(level: InfoLogLevel, message: String) {
        if (isOwningHandle() && level.value >= logLevel.value) log(level, message)
    }

    override fun close() {
        if (tryClose()) {
            LoggerCallbacks.unregister(callbackState)
            rocksdb_logger_destroy(native)
            super.close()
        }
    }
}

actual fun createLogger(logLevel: InfoLogLevel, callback: (InfoLogLevel, String) -> Unit): Logger =
    object : Logger(logLevel) {
        override fun log(level: InfoLogLevel, message: String) = callback(level, message)
    }

/**
 * The C callback factory has no state destructor, and options/databases share
 * its C++ logger. Use never-reused integer tokens rather than pointers to Kotlin
 * objects: callbacks arriving after close then find no registered recipient.
 */
private object LoggerCallbacks {
    private val lastId = AtomicInt(0)
    private val recipients = AtomicReference<Map<Int, Logger>>(emptyMap())

    fun register(logger: Logger): COpaquePointer {
        val id = nextId()
        while (true) {
            val current = recipients.load()
            if (recipients.compareAndSet(current, current + (id to logger))) break
        }
        // Positive Int tokens fit pointers on both 32-bit and 64-bit targets.
        // C only stores/returns this token; neither side dereferences it.
        return requireNotNull(id.toLong().toCPointer<ByteVar>())
    }

    fun find(state: COpaquePointer?): Logger? {
        val id = state?.rawValue?.toLong() ?: return null
        if (id !in 1L..Int.MAX_VALUE.toLong()) return null
        return recipients.load()[id.toInt()]
    }

    fun unregister(state: COpaquePointer) {
        val id = state.rawValue.toLong().toInt()
        while (true) {
            val current = recipients.load()
            if (id !in current || recipients.compareAndSet(current, current - id)) return
        }
    }

    private fun nextId(): Int {
        while (true) {
            val current = lastId.load()
            check(current < Int.MAX_VALUE) { "Logger callback token space exhausted" }
            if (lastId.compareAndSet(current, current + 1)) return current + 1
        }
    }
}

private fun loggerLogCallback(
    state: COpaquePointer?, level: UInt, message: CPointer<ByteVar>?, length: size_t,
) {
    try {
        val logger = LoggerCallbacks.find(state) ?: return
        val kotlinMessage = message?.readBytes(sizeTToInt(length, "log message"))?.decodeToString().orEmpty()
        val infoLogLevel = try {
            getInfoLogLevel(level.toUByte())
        } catch (_: IllegalArgumentException) {
            InfoLogLevel.INFO_LEVEL
        }
        logger.dispatchLog(infoLogLevel, kotlinMessage)
    } catch (_: Throwable) {
    }
}
