package maryk.rocksdb

/**
 * Options that configure manual flush behaviour when calling [RocksDB.flush].
 */
expect class FlushOptions() : RocksObject {
    fun setWaitForFlush(wait: Boolean): FlushOptions
    fun waitForFlush(): Boolean
    fun setAllowWriteStall(allow: Boolean): FlushOptions
    fun allowWriteStall(): Boolean
}
