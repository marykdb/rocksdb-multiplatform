package maryk.rocksdb

/**
 * Options that control write operations.
 *
 * Note that developers should call WriteOptions.dispose() to release the
 * c++ side memory before a WriteOptions instance runs out of scope.
 */
expect class WriteOptions() : RocksObject {
    /**
     * If true, the write will be flushed from the operating system
     * buffer cache (by calling WritableFile::Sync()) before the write
     * is considered complete.  If this flag is true, writes will be
     * slower.
     *
     * If this flag is false, and the machine crashes, some recent
     * writes may be lost.  Note that if it is just the process that
     * crashes (i.e., the machine does not reboot), no writes will be
     * lost even if sync==false.
     *
     * In other words, a DB write with sync==false has similar
     * crash semantics as the "write()" system call.  A DB write
     * with sync==true has similar crash semantics to a "write()"
     * system call followed by "fdatasync()".
     *
     * Default: false
     *
     * @param flag a boolean flag to indicate whether a write
     * should be synchronized.
     * @return the instance of the current WriteOptions.
     */
    fun setSync(flag: Boolean): WriteOptions

    /**
     * If true, the write will be flushed from the operating system
     * buffer cache (by calling WritableFile::Sync()) before the write
     * is considered complete.  If this flag is true, writes will be
     * slower.
     *
     * If this flag is false, and the machine crashes, some recent
     * writes may be lost. Note that if it is just the process that
     * crashes (i.e., the machine does not reboot), no writes will be
     * lost even if sync==false.
     *
     * In other words, a DB write with sync==false has similar
     * crash semantics as the "write()" system call.  A DB write
     * with sync==true has similar crash semantics to a "write()"
     * system call followed by "fdatasync()".
     *
     * @return boolean value indicating if sync is active.
     */
    fun sync(): Boolean

    /**
     * If true, writes will not first go to the write ahead log,
     * and the write may got lost after a crash.
     *
     * @param flag a boolean flag to specify whether to disable
     * write-ahead-log on writes.
     * @return the instance of the current WriteOptions.
     */
    fun setDisableWAL(flag: Boolean): WriteOptions

    /**
     * If true, writes will not first go to the write ahead log,
     * and the write may got lost after a crash.
     *
     * @return boolean value indicating if WAL is disabled.
     */
    fun disableWAL(): Boolean

    /**
     * If true and if user is trying to write to column families that don't exist
     * (they were dropped), ignore the write (don't return an error). If there
     * are multiple writes in a WriteBatch, other writes will succeed.
     *
     * Default: false
     *
     * @param ignoreMissingColumnFamilies true to ignore writes to column families
     * which don't exist
     * @return the instance of the current WriteOptions.
     */
    fun setIgnoreMissingColumnFamilies(
        ignoreMissingColumnFamilies: Boolean
    ): WriteOptions

    /**
     * If true and if user is trying to write to column families that don't exist
     * (they were dropped), ignore the write (don't return an error). If there
     * are multiple writes in a WriteBatch, other writes will succeed.
     *
     * Default: false
     *
     * @return true if writes to column families which don't exist are ignored
     */
    fun ignoreMissingColumnFamilies(): Boolean

    /**
     * If true and we need to wait or sleep for the write request, fails
     * immediately with [Status.Code.Incomplete].
     *
     * @param noSlowdown true to fail write requests if we need to wait or sleep
     * @return the instance of the current WriteOptions.
     */
    fun setNoSlowdown(noSlowdown: Boolean): WriteOptions

    /**
     * If true and we need to wait or sleep for the write request, fails
     * immediately with [Status.Code.Incomplete].
     *
     * @return true when write requests are failed if we need to wait or sleep
     */
    fun noSlowdown(): Boolean

    /**
     * If true, this write request is of lower priority if compaction is
     * behind. In this case that, [.noSlowdown] == true, the request
     * will be cancelled immediately with [Status.Code.Incomplete] returned.
     * Otherwise, it will be slowed down. The slowdown value is determined by
     * RocksDB to guarantee it introduces minimum impacts to high priority writes.
     *
     * Default: false
     *
     * @param lowPri true if the write request should be of lower priority than
     * compactions which are behind.
     *
     * @return the instance of the current WriteOptions.
     */
    fun setLowPri(lowPri: Boolean): WriteOptions

    /**
     * Returns true if this write request is of lower priority if compaction is
     * behind.
     *
     * See [.setLowPri].
     *
     * @return true if this write request is of lower priority, false otherwise.
     */
    fun lowPri(): Boolean
}
