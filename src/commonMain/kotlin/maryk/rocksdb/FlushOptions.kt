package maryk.rocksdb

/**
 * FlushOptions to be passed to flush operations of
 * [RocksDB].
 */
expect class FlushOptions() : RocksObject {
    /**
     * Set if the flush operation shall block until it terminates.
     *
     * @param waitForFlush boolean value indicating if the flush
     * operations waits for termination of the flush process.
     *
     * @return instance of current FlushOptions.
     */
    fun setWaitForFlush(waitForFlush: Boolean): FlushOptions

    /**
     * Wait for flush to finished.
     *
     * @return boolean value indicating if the flush operation
     * waits for termination of the flush process.
     */
    fun waitForFlush(): Boolean

    /**
     * Set to true so that flush would proceeds immediately even it it means
     * writes will stall for the duration of the flush.
     *
     * Set to false so that the operation will wait until it's possible to do
     * the flush without causing stall or until required flush is performed by
     * someone else (foreground call or background thread).
     *
     * Default: false
     *
     * @param allowWriteStall true to allow writes to stall for flush, false
     * otherwise.
     *
     * @return instance of current FlushOptions.
     */
    fun setAllowWriteStall(allowWriteStall: Boolean): FlushOptions

    /**
     * Returns true if writes are allowed to stall for flushes to complete, false
     * otherwise.
     *
     * @return true if writes are allowed to stall for flushes
     */
    fun allowWriteStall(): Boolean
}
