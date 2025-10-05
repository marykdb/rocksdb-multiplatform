package maryk.rocksdb

/**
 * Tracks SST files associated with a database and throttles deletion to avoid IO spikes.
 */
expect class SstFileManager(env: Env): AutoCloseable {
    fun setMaxAllowedSpaceUsage(maxAllowedSpace: Long)

    fun setCompactionBufferSize(compactionBufferSize: Long)

    fun isMaxAllowedSpaceReached(): Boolean

    fun isMaxAllowedSpaceReachedIncludingCompactions(): Boolean

    fun getTotalSize(): Long

    fun getDeleteRateBytesPerSecond(): Long

    fun setDeleteRateBytesPerSecond(deleteRate: Long)

    fun getMaxTrashDBRatio(): Double

    fun setMaxTrashDBRatio(ratio: Double)

    override fun close()
}
