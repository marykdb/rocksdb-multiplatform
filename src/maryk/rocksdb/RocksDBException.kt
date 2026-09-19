package maryk.rocksdb

expect class RocksDBException : Exception {
    /**
     * The private construct used by a set of public static factory method.
     * @param msg the specified error message.
     */
    constructor(msg: String)

    constructor(msg: String, status: Status?)

    constructor(status: Status)

    /** Get the status returned from RocksDB, or null if no status is available */
    fun getStatus(): Status?
}
