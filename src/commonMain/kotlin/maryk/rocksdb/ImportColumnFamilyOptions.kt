package maryk.rocksdb

/**
 * Options controlling how external SST files are imported when creating column families.
 */
expect class ImportColumnFamilyOptions() : RocksObject {
    /**
     * Whether the importer should move files instead of copying them.
     */
    fun setMoveFiles(moveFiles: Boolean): ImportColumnFamilyOptions

    /** Returns whether files will be moved rather than copied during import. */
    fun moveFiles(): Boolean
}
