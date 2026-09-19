package maryk.rocksdb

/** Metadata describing a sealed memtable. */
expect class MemTableInfo {
    /** Column family that owns the memtable. */
    fun columnFamilyName(): String

    /** Sequence number of the first entry that was inserted. */
    fun firstSeqno(): Long

    /** Earliest sequence number that may appear in this memtable. */
    fun earliestSeqno(): Long

    /** Total number of key/value pairs contained in the memtable. */
    fun numEntries(): Long

    /** Total number of delete markers contained in the memtable. */
    fun numDeletes(): Long
}
