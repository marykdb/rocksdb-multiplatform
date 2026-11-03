package maryk.rocksdb

/**
 * Options that configure manual flush behaviour when calling [RocksDB.flush].
 */
expect class FlushOptions() : RocksObject {
    fun setWaitForFlush(wait: Boolean): FlushOptions
    fun waitForFlush(): Boolean
    fun setAllowWriteStall(allow: Boolean): FlushOptions
    fun allowWriteStall(): Boolean

    /** Wait for completion callbacks as well as the flush when [waitForFlush] is true. */
    fun setListenerWait(listenerWait: Boolean): FlushOptions
    fun listenerWait(): Boolean
}
