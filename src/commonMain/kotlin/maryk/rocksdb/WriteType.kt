package maryk.rocksdb;

/**
 * Enumeration of the Write operation
 * that created the record in the Write Batch
 */
expect enum class WriteType {
    PUT,
    MERGE,
    DELETE,
    SINGLE_DELETE,
    DELETE_RANGE,
    LOG,
    XID;
}
