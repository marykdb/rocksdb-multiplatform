package maryk.rocksdb

/**
 * A file trace started by [RocksDB.startTrace]. Closing the session stops tracing
 * and closes its file. Close the session before starting another trace on the DB.
 */
expect class TraceSession : AutoCloseable {
    override fun close()
}

/**
 * Trace operations to [tracePath], replacing any existing file. The parent
 * directory must exist. The returned session must be closed to stop tracing and
 * release the file, preferably before closing the database. Do not call other
 * trace-start or trace-end APIs while this session is active.
 *
 * This supports the size limit in [TraceOptions]. Filter, sampling and replay
 * controls are not exposed by RocksDB's Java/JNI 11.8.1 API.
 */
expect fun RocksDB.startTrace(traceOptions: TraceOptions, tracePath: String): TraceSession

/** Stop and close this database's active file trace, if any, even if its session was lost. */
expect fun RocksDB.endFileTrace()
