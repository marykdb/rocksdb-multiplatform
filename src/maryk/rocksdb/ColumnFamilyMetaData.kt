package maryk.rocksdb

/**
 * The metadata that describes a column family.
 */
expect class ColumnFamilyMetaData {
    /**
     * The size of this column family in bytes, which is equal to the sum of
     * the file size of its [.levels].
     */
    fun size(): Long

    /** The number of files in this column family. */
    fun fileCount(): Long

    /** The name of the column family. */
    fun name(): ByteArray

    /** The metadata of all levels in this column family. */
    fun levels(): List<LevelMetaData>
}
