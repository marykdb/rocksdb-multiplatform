package maryk.rocksdb

import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.WeakHashMap

private val fileTraceSessions = WeakHashMap<RocksDB, FileTraceWriter>()

actual class TraceSession internal constructor(
    private val db: RocksDB,
    private val writer: FileTraceWriter,
) : AutoCloseable {
    private val closed = AtomicBoolean()

    actual override fun close() {
        if (closed.compareAndSet(false, true)) db.endFileTrace(writer)
    }
}

actual fun RocksDB.startTrace(traceOptions: TraceOptions, tracePath: String): TraceSession = synchronized(fileTraceSessions) {
    check(isOwningHandle()) { "Native handle is already closed or transferred." }
    check(fileTraceSessions[this] == null) { "Close the active trace session before starting another trace" }
    require('\u0000' !in tracePath) { "Trace path must not contain NUL characters" }
    require(traceOptions.getMaxTraceFileSize() >= 0) { "Trace size limit must be non-negative" }
    val output = try {
        FileOutputStream(tracePath)
    } catch (error: IOException) {
        throw RocksDBException("Unable to open trace file: ${error.message}")
    }
    startFileTrace(traceOptions, output)
}

actual fun RocksDB.endFileTrace() {
    synchronized(fileTraceSessions) {
        fileTraceSessions[this]?.let { endFileTrace(it) }
    }
}

private fun RocksDB.endFileTrace(writer: FileTraceWriter) {
    synchronized(fileTraceSessions) {
        if (fileTraceSessions[this] !== writer) return
        fileTraceSessions.remove(this)
        var failure: Throwable? = null
        try {
            if (isOwningHandle()) endTrace()
        } catch (error: Throwable) {
            failure = error
        }
        try {
            writer.closeWriter()
            writer.checkFailure()
        } catch (error: Throwable) {
            if (failure == null) failure = error else failure.addSuppressed(error)
        }
        failure?.let { throw it }
    }
}

internal fun RocksDB.startFileTrace(traceOptions: TraceOptions, output: OutputStream): TraceSession =
    synchronized(fileTraceSessions) {
        if (fileTraceSessions[this] != null) {
            output.close()
            throw IllegalStateException("Close the active trace session before starting another trace")
        }
        val writer = try {
            FileTraceWriter(output)
        } catch (error: Throwable) {
            output.close()
            throw error
        }
        var started = false
        try {
            startTrace(traceOptions, writer)
            started = true
            // RocksDB's StartTrace ignores a failure writing the trace header.
            writer.checkFailure()
        } catch (error: Throwable) {
            if (started) {
                try {
                    endTrace()
                } catch (endError: Throwable) {
                    error.addSuppressed(endError)
                }
            }
            // JNI consumes the callback even on a failed StartTrace, but only
            // disowns its Java handle on success. Avoid a later double delete.
            writer.disownAfterFailedStart()
            try {
                writer.closeWriter()
            } catch (closeError: Throwable) {
                error.addSuppressed(closeError)
            }
            throw error
        }
        TraceSession(this, writer).also { fileTraceSessions[this] = writer }
    }

internal class FileTraceWriter(private val output: OutputStream) : AbstractTraceWriter() {
    private var bytesWritten = 0L
    private var closed = false
    private var failure: RocksDBException? = null

    @Synchronized
    override fun write(data: Slice) {
        try {
            val bytes = data.data()
            output.write(bytes)
            bytesWritten += bytes.size
        } catch (error: IOException) {
            // JNI's writeProxy converts RocksDBException into a status. Passing
            // IOException through JNI would leave a pending Java exception.
            val exception = RocksDBException("Unable to write trace file: ${error.message}")
            if (failure == null) failure = exception
            throw exception
        }
    }

    @Synchronized
    override fun closeWriter() {
        if (!closed) {
            closed = true
            try {
                output.close()
            } catch (error: IOException) {
                throw RocksDBException("Unable to close trace file: ${error.message}")
            }
        }
    }

    @Synchronized
    override fun getFileSize(): Long = bytesWritten

    @Synchronized
    fun checkFailure() {
        failure?.let { throw it }
    }

    fun disownAfterFailedStart() = disOwnNativeHandle()
}
