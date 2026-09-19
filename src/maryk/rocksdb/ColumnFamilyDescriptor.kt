package maryk.rocksdb

expect class ColumnFamilyDescriptor(
    columnFamilyName: ByteArray,
    columnFamilyOptions: ColumnFamilyOptions
) {
    /**
     * Creates a new Column Family using a name and default
     * options,
     * @param columnFamilyName name of column family.
     */
    constructor(columnFamilyName: ByteArray)

    /** Retrieve name of column family. */
    fun getName(): ByteArray

    /** Retrieve assigned options instance. */
    fun getOptions(): ColumnFamilyOptions
}
