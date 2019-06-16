package maryk.rocksdb

/**
 * Represents an entry returned by
 * [WBWIRocksIterator.entry]
 *
 * It is worth noting that a WriteEntry with
 * the type [WriteType.DELETE]
 * or [WriteType.LOG]
 * will not have a value.
 */
expect class WriteEntry(type: WriteType, key: DirectSlice, value: DirectSlice) : AutoCloseable {
    /**
     * Returns the type of the Write Entry
     *
     * @return the WriteType of the WriteEntry
     */
    fun getType(): WriteType

    /**
     * Returns the key of the Write Entry
     *
     * @return The slice containing the key
     * of the WriteEntry
     */
    fun getKey(): DirectSlice

    /**
     * Returns the value of the Write Entry
     *
     * @return The slice containing the value of
     * the WriteEntry or null if the WriteEntry has
     * no value
     */
    fun getValue(): DirectSlice?
}
