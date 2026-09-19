package maryk.rocksdb

expect interface TraceWriter {
    /**
     * Write the [data].
     * @param data the data
     *
     * @throws RocksDBException if an error occurs whilst writing.
     */
    fun write(data: Slice)

    /**
     * Close the writer.
     * @throws RocksDBException if an error occurs whilst closing the writer.
     */
    fun closeWriter()

    /** Get the size of the file that this writer is writing to. */
    fun getFileSize(): Long
}
