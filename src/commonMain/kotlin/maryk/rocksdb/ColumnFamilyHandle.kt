package maryk.rocksdb

expect class ColumnFamilyHandle : RocksObject {
    /**
     * Gets the name of the Column Family.
     *
     * @throws RocksDBException if an error occurs whilst retrieving the name.
     */
    fun getName(): ByteArray

    /**  Gets the ID of the Column Family. */
    fun getID(): Int

    /**
     * Gets the up-to-date descriptor of the column family
     * associated with this handle. Since it fills "*desc" with the up-to-date
     * information, this call might internally lock and release DB mutex to
     * access the up-to-date CF options. In addition, all the pointer-typed
     * options cannot be referenced any longer than the original options exist.
     *
     * Note that this function is not supported in RocksDBLite.
     *
     * @throws RocksDBException if an error occurs whilst retrieving the
     * descriptor.
     */
    fun getDescriptor(): ColumnFamilyDescriptor
}
