package maryk.rocksdb

actual class TraceOptions actual constructor(
    private val maxTraceFileSize: Long
) {
    // 64 GB
    actual constructor() : this(64 * 1024L * 1024L * 1024L)

    actual fun getMaxTraceFileSize() = maxTraceFileSize
}
