package maryk.rocksdb

actual interface TraceWriter {
    actual fun write(data: Slice)

    actual fun closeWriter()

    actual fun getFileSize(): Long
}
