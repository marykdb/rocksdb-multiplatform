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
}
