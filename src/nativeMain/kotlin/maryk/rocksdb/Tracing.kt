package maryk.rocksdb

import maryk.asUInt64
import maryk.wrapWithErrorThrower
import rocksdb.rocksdb_end_trace
import rocksdb.rocksdb_start_trace
import rocksdb.rocksdb_trace_options_create
import rocksdb.rocksdb_trace_options_destroy
import rocksdb.rocksdb_trace_options_set_max_trace_file_size
import kotlin.concurrent.AtomicInt

actual class TraceSession internal constructor(private val db: RocksDB) : AutoCloseable {
    private val closed = AtomicInt(0)

    actual override fun close() = db.withLifecycleLock { closeFromOwner() }

    internal fun closeFromOwner() {
        if (closed.compareAndSet(0, 1)) {
            db.fileTraceSession = null
            if (db.isOwningHandle()) {
                wrapWithErrorThrower { error -> rocksdb_end_trace(db.native, error) }
            }
        }
    }
}

actual fun RocksDB.startTrace(traceOptions: TraceOptions, tracePath: String): TraceSession = withLifecycleLock {
    checkOwningHandle()
    check(fileTraceSession == null) { "Close the active trace session before starting another trace" }
    require('\u0000' !in tracePath) { "Trace path must not contain NUL characters" }
    val limit = traceOptions.getMaxTraceFileSize().asUInt64()
    val options = requireNotNull(rocksdb_trace_options_create()) { "Unable to allocate trace options" }
    try {
        rocksdb_trace_options_set_max_trace_file_size(options, limit)
        wrapWithErrorThrower { error ->
            rocksdb_start_trace(native, null, null, options, tracePath, error)
        }
    } finally {
        rocksdb_trace_options_destroy(options)
    }
    TraceSession(this).also { fileTraceSession = it }
}

actual fun RocksDB.endFileTrace() = withLifecycleLock {
    fileTraceSession?.closeFromOwner()
    Unit
}
