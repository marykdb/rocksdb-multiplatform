package maryk.rocksdb

/**
 * Options while opening a file to read/write
 */
expect class EnvOptions() : RocksObject {
    /**
     * Construct from [DBOptions].
     *
     * @param dbOptions the database options.
     */
    constructor(dbOptions: DBOptions)

    /**
     * Enable/Disable memory mapped reads.
     * Default: false
     * @param useMmapReads true to enable memory mapped reads, false to disable.
     *
     * @return the reference to these options.
     */
    fun setUseMmapReads(useMmapReads: Boolean): EnvOptions

    /**
     * Determine if memory mapped reads are in-use.
     * @return true if memory mapped reads are in-use, false otherwise.
     */
    fun useMmapReads(): Boolean

    /**
     * Enable/Disable memory mapped Writes.
     *
     * Default: true
     *
     * @param useMmapWrites true to enable memory mapped writes, false to disable.
     *
     * @return the reference to these options.
     */
    fun setUseMmapWrites(useMmapWrites: Boolean): EnvOptions

    /**
     * Determine if memory mapped writes are in-use.
     *
     * @return true if memory mapped writes are in-use, false otherwise.
     */
    fun useMmapWrites(): Boolean

    /**
     * Enable/Disable direct reads, i.e. `O_DIRECT`.
     *
     * Default: false
     *
     * @param useDirectReads true to enable direct reads, false to disable.
     *
     * @return the reference to these options.
     */
    fun setUseDirectReads(useDirectReads: Boolean): EnvOptions

    /**
     * Determine if direct reads are in-use.
     *
     * @return true if direct reads are in-use, false otherwise.
     */
    fun useDirectReads(): Boolean

    /**
     * Enable/Disable direct writes, i.e. `O_DIRECT`.
     *
     * Default: false
     *
     * @param useDirectWrites true to enable direct writes, false to disable.
     *
     * @return the reference to these options.
     */
    fun setUseDirectWrites(useDirectWrites: Boolean): EnvOptions

    /**
     * Determine if direct writes are in-use.
     *
     * @return true if direct writes are in-use, false otherwise.
     */
    fun useDirectWrites(): Boolean

    /**
     * Enable/Disable fallocate calls.
     *
     * Default: true
     *
     * If false, `fallocate()` calls are bypassed.
     *
     * @param allowFallocate true to enable fallocate calls, false to disable.
     *
     * @return the reference to these options.
     */
    fun setAllowFallocate(allowFallocate: Boolean): EnvOptions

    /**
     * Determine if fallocate calls are used.
     *
     * @return true if fallocate calls are used, false otherwise.
     */
    fun allowFallocate(): Boolean

    /**
     * Enable/Disable the `FD_CLOEXEC` bit when opening file descriptors.
     * Default: true
     *
     * @param setFdCloexec true to enable the `FB_CLOEXEC` bit,
     * false to disable.
     *
     * @return the reference to these options.
     */
    fun setSetFdCloexec(setFdCloexec: Boolean): EnvOptions

    /**
     * Determine i fthe `FD_CLOEXEC` bit is set when opening file
     * descriptors.
     *
     * @return true if the `FB_CLOEXEC` bit is enabled, false otherwise.
     */
    fun setFdCloexec(): Boolean

    /**
     * Allows OS to incrementally sync files to disk while they are being
     * written, in the background. Issue one request for every
     * `bytesPerSync` written.
     *
     * Default: 0
     *
     * @param bytesPerSync 0 to disable, otherwise the number of bytes.
     *
     * @return the reference to these options.
     */
    fun setBytesPerSync(bytesPerSync: Long): EnvOptions

    /**
     * Get the number of incremental bytes per sync written in the background.
     *
     * @return 0 if disabled, otherwise the number of bytes.
     */
    fun bytesPerSync(): Long

    /**
     * If true, we will preallocate the file with `FALLOC_FL_KEEP_SIZE`
     * flag, which means that file size won't change as part of preallocation.
     * If false, preallocation will also change the file size. This option will
     * improve the performance in workloads where you sync the data on every
     * write. By default, we set it to true for MANIFEST writes and false for
     * WAL writes
     *
     * @param fallocateWithKeepSize true to preallocate, false otherwise.
     *
     * @return the reference to these options.
     */
    fun setFallocateWithKeepSize(
        fallocateWithKeepSize: Boolean
    ): EnvOptions

    /**
     * Determine if file is preallocated.
     *
     * @return true if the file is preallocated, false otherwise.
     */
    fun fallocateWithKeepSize(): Boolean

    /**
     * See [DBOptions.setCompactionReadaheadSize].
     *
     * @param compactionReadaheadSize the compaction read-ahead size.
     *
     * @return the reference to these options.
     */
    fun setCompactionReadaheadSize(
        compactionReadaheadSize: Long
    ): EnvOptions

    /**
     * See [DBOptions.compactionReadaheadSize].
     *
     * @return the compaction read-ahead size.
     */
    fun compactionReadaheadSize(): Long

    /**
     * See [DBOptions.setRandomAccessMaxBufferSize].
     *
     * @param randomAccessMaxBufferSize the max buffer size for random access.
     *
     * @return the reference to these options.
     */
    fun setRandomAccessMaxBufferSize(
        randomAccessMaxBufferSize: Long
    ): EnvOptions

    /**
     * See [DBOptions.randomAccessMaxBufferSize].
     *
     * @return the max buffer size for random access.
     */
    fun randomAccessMaxBufferSize(): Long

    /**
     * See [DBOptions.setWritableFileMaxBufferSize].
     *
     * @param writableFileMaxBufferSize the max buffer size.
     *
     * @return the reference to these options.
     */
    fun setWritableFileMaxBufferSize(
        writableFileMaxBufferSize: Long
    ): EnvOptions

    /**
     * See [DBOptions.writableFileMaxBufferSize].
     *
     * @return the max buffer size.
     */
    fun writableFileMaxBufferSize(): Long

    /**
     * Set the write rate limiter for flush and compaction.
     *
     * @param rateLimiter the rate limiter.
     *
     * @return the reference to these options.
     */
    fun setRateLimiter(rateLimiter: RateLimiter): EnvOptions

    /**
     * Get the write rate limiter for flush and compaction.
     *
     * @return the rate limiter.
     */
    fun rateLimiter(): RateLimiter?
}
