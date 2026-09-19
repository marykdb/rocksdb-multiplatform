package maryk.rocksdb

/** Snapshot of the write controller's stall state. */
expect class WriteStallInfo {
    /** Column family associated with the stall transition. */
    fun columnFamilyName(): String

    /** New stall condition. */
    fun currentCondition(): WriteStallCondition

    /** Previous stall condition. */
    fun previousCondition(): WriteStallCondition
}
