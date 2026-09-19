package maryk.rocksdb

/**
 * TraceOptions is used for
 * [RocksDB.startTrace].
 */
expect class TraceOptions() {
    constructor(maxTraceFileSize: Long)

    /**
     * To avoid the trace file size grows large than the storage space,
     * user can set the max trace file size in Bytes. Default is 64GB
     *
     * @return the max trace size
     */
    fun getMaxTraceFileSize(): Long
}
