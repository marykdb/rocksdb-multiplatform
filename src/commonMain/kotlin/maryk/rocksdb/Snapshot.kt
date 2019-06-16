package maryk.rocksdb

/** Snapshot of database */
expect class Snapshot : RocksObject {
    /** Return the associated sequence number */
    fun getSequenceNumber(): Long
}
