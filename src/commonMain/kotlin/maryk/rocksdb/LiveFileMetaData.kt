package maryk.rocksdb

/**
 * Metadata describing an SST file that is currently live in the database.
 */
expect class LiveFileMetaData : SstFileMetaData {
    /**
     * Returns the name of the column family this file belongs to.
     */
    fun columnFamilyName(): ByteArray

    /**
     * Returns the level at which the file resides in the LSM tree.
     */
    fun level(): Int
}
