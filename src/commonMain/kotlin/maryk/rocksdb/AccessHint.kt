package maryk.rocksdb

expect enum class AccessHint {
    NONE,
    NORMAL,
    SEQUENTIAL,
    WILLNEED;

    /**
     * Returns the byte value of the enumerations value.
     */
    fun getValue(): Byte
}
