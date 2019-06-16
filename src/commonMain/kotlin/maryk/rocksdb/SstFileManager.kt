package maryk.rocksdb

expect class SstFileManager : RocksObject {
    /**
     * Create a new SstFileManager that can be shared among multiple RocksDB
     * instances to track SST file and control there deletion rate.
     *
     * @param env the environment.
     *
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    constructor(env: Env)

    /**
     * Create a new SstFileManager that can be shared among multiple RocksDB
     * instances to track SST file and control there deletion rate.
     *
     * @param env the environment.
     * @param logger if not null, the logger will be used to log errors.
     *
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    constructor(env: Env, logger: Logger?)

    /**
     * Create a new SstFileManager that can be shared among multiple RocksDB
     * instances to track SST file and control there deletion rate.
     *
     * @param env the environment.
     * @param logger if not null, the logger will be used to log errors.
     *
     * == Deletion rate limiting specific arguments ==
     * @param rateBytesPerSec how many bytes should be deleted per second, If
     * this value is set to 1024 (1 Kb / sec) and we deleted a file of size
     * 4 Kb in 1 second, we will wait for another 3 seconds before we delete
     * other files, Set to 0 to disable deletion rate limiting.
     *
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    constructor(
        env: Env,
        logger: Logger?,
        rateBytesPerSec: Long
    )

    /**
     * Create a new SstFileManager that can be shared among multiple RocksDB
     * instances to track SST file and control there deletion rate.
     *
     * @param env the environment.
     * @param logger if not null, the logger will be used to log errors.
     *
     * == Deletion rate limiting specific arguments ==
     * @param rateBytesPerSec how many bytes should be deleted per second, If
     * this value is set to 1024 (1 Kb / sec) and we deleted a file of size
     * 4 Kb in 1 second, we will wait for another 3 seconds before we delete
     * other files, Set to 0 to disable deletion rate limiting.
     * @param maxTrashDbRatio if the trash size constitutes for more than this
     * fraction of the total DB size we will start deleting new files passed
     * to DeleteScheduler immediately.
     *
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    constructor(
        env: Env,
        logger: Logger?,
        rateBytesPerSec: Long,
        maxTrashDbRatio: Double
    )

    /**
     * Create a new SstFileManager that can be shared among multiple RocksDB
     * instances to track SST file and control there deletion rate.
     *
     * @param env the environment.
     * @param logger if not null, the logger will be used to log errors.
     *
     * == Deletion rate limiting specific arguments ==
     * @param rateBytesPerSec how many bytes should be deleted per second, If
     * this value is set to 1024 (1 Kb / sec) and we deleted a file of size
     * 4 Kb in 1 second, we will wait for another 3 seconds before we delete
     * other files, Set to 0 to disable deletion rate limiting.
     * @param maxTrashDbRatio if the trash size constitutes for more than this
     * fraction of the total DB size we will start deleting new files passed
     * to DeleteScheduler immediately.
     * @param bytesMaxDeleteChunk if a single file is larger than delete chunk,
     * ftruncate the file by this size each time, rather than dropping the whole
     * file. 0 means to always delete the whole file.
     *
     * @throws RocksDBException thrown if error happens in underlying native library.
     */
    constructor(
        env: Env,
        logger: Logger?,
        rateBytesPerSec: Long,
        maxTrashDbRatio: Double,
        bytesMaxDeleteChunk: Long
    )

    /**
     * Update the maximum allowed space that should be used by RocksDB, if
     * the total size of the SST files exceeds `maxAllowedSpace`, writes to
     * RocksDB will fail.
     *
     * Setting `maxAllowedSpace` to 0 will disable this feature;
     * maximum allowed space will be infinite (Default value).
     *
     * @param maxAllowedSpace the maximum allowed space that should be used by
     * RocksDB.
     */
    fun setMaxAllowedSpaceUsage(maxAllowedSpace: Long)

    /**
     * Set the amount of buffer room each compaction should be able to leave.
     * In other words, at its maximum disk space consumption, the compaction
     * should still leave `compactionBufferSize` available on the disk so
     * that other background functions may continue, such as logging and flushing.
     *
     * @param compactionBufferSize the amount of buffer room each compaction
     * should be able to leave.
     */
    fun setCompactionBufferSize(compactionBufferSize: Long)

    /**
     * Determines if the total size of SST files exceeded the maximum allowed
     * space usage.
     *
     * @return true when the maximum allows space usage has been exceeded.
     */
    fun isMaxAllowedSpaceReached(): Boolean

    /**
     * Determines if the total size of SST files as well as estimated size
     * of ongoing compactions exceeds the maximums allowed space usage.
     *
     * @return true when the total size of SST files as well as estimated size
     * of ongoing compactions exceeds the maximums allowed space usage.
     */
    fun isMaxAllowedSpaceReachedIncludingCompactions(): Boolean

    /**
     * Get the total size of all tracked files.
     *
     * @return the total size of all tracked files.
     */
    fun getTotalSize(): Long

    /**
     * Gets all tracked files and their corresponding sizes.
     *
     * @return a map containing all tracked files and there corresponding sizes.
     */
    fun getTrackedFiles(): Map<String, Long>

    /**
     * Gets the delete rate limit.
     *
     * @return the delete rate limit (in bytes per second).
     */
    fun getDeleteRateBytesPerSecond(): Long

    /**
     * Set the delete rate limit.
     *
     * Zero means disable delete rate limiting and delete files immediately.
     *
     * @param deleteRate the delete rate limit (in bytes per second).
     */
    fun setDeleteRateBytesPerSecond(deleteRate: Long)

    /**
     * Get the trash/DB size ratio where new files will be deleted immediately.
     *
     * @return the trash/DB size ratio.
     */
    fun getMaxTrashDBRatio(): Double

    /**
     * Set the trash/DB size ratio where new files will be deleted immediately.
     *
     * @param ratio the trash/DB size ratio.
     */
    fun setMaxTrashDBRatio(ratio: Double)
}
