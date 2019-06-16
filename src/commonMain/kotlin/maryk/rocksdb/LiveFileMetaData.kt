package maryk.rocksdb

/** The full set of metadata associated with each SST file. */
expect class LiveFileMetaData {
    /** Get the name of the column family. */
    fun columnFamilyName(): ByteArray

    /** Get the level at which this file resides. */
    fun level(): Int
}
