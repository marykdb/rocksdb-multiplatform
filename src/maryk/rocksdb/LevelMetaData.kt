package maryk.rocksdb

/**
 * The metadata that describes a level.
 */
expect class LevelMetaData {
    /** The level which this meta data describes. */
    fun level(): Int

    /**
     * The size of this level in bytes, which is equal to the sum of
     * the file size of its [.files].
     */
    fun size(): Long

    /** The metadata of all sst files in this level. */
    fun files(): List<SstFileMetaData>
}
