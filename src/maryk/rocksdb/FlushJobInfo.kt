package maryk.rocksdb

/** Metadata describing a flush job. */
expect class FlushJobInfo {
    /** Identifier of the column family that produced the SST file. */
    fun columnFamilyId(): Long

    /** Name of the column family that produced the SST file. */
    fun columnFamilyName(): String

    /** Absolute path of the flushed SST file. */
    fun filePath(): String

    /** Thread identifier that executed the flush. */
    fun threadId(): Long

    /** Job identifier that is unique within the thread. */
    fun jobId(): Int

    /** Whether the flush was triggered because writes were slowed down. */
    fun triggeredWritesSlowdown(): Boolean

    /** Whether writes were stopped while waiting for this flush. */
    fun triggeredWritesStop(): Boolean

    /** Smallest sequence number contained in the flushed file. */
    fun smallestSeqno(): Long

    /** Largest sequence number contained in the flushed file. */
    fun largestSeqno(): Long

    /** Table properties associated with the flushed SST file. */
    fun tableProperties(): TableProperties

    /** Reason why the flush was initiated. */
    fun flushReason(): FlushReason
}
