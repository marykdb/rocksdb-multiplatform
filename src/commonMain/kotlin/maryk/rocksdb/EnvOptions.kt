package maryk.rocksdb

/**
 * Wrapper for RocksDB EnvOptions controlling I/O behaviour for auxiliary tools.
 */
expect class EnvOptions() : RocksObject {
    /** I/O sizes are non-negative byte counts. */
    fun setUseMmapReads(value: Boolean): EnvOptions
    fun useMmapReads(): Boolean

    fun setUseMmapWrites(value: Boolean): EnvOptions
    fun useMmapWrites(): Boolean

    fun setUseDirectReads(value: Boolean): EnvOptions
    fun useDirectReads(): Boolean

    fun setUseDirectWrites(value: Boolean): EnvOptions
    fun useDirectWrites(): Boolean

    fun setAllowFallocate(value: Boolean): EnvOptions
    fun allowFallocate(): Boolean

    fun setSetFdCloexec(value: Boolean): EnvOptions
    fun setFdCloexec(): Boolean

    fun setBytesPerSync(value: Long): EnvOptions
    fun bytesPerSync(): Long

    fun setFallocateWithKeepSize(value: Boolean): EnvOptions
    fun fallocateWithKeepSize(): Boolean

    fun setCompactionReadaheadSize(value: Long): EnvOptions
    fun compactionReadaheadSize(): Long

    fun setWritableFileMaxBufferSize(value: Long): EnvOptions
    fun writableFileMaxBufferSize(): Long

    /** The rate limiter must stay open while this environment and its tools use it. */
    fun setRateLimiter(rateLimiter: RateLimiter): EnvOptions
}
