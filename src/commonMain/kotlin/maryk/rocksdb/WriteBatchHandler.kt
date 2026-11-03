package maryk.rocksdb

/**
 * Visits a serialized write batch in record order. Byte arrays are copied and can be retained.
 * Override the records of interest; callbacks for other record types do nothing.
 * Column-family callbacks receive the numeric family ID, including zero for the default family.
 */
abstract class WriteBatchHandler {
    open fun put(columnFamilyId: Int, key: ByteArray, value: ByteArray) { if (columnFamilyId == 0) put(key, value) }
    open fun put(key: ByteArray, value: ByteArray) {}
    open fun merge(columnFamilyId: Int, key: ByteArray, value: ByteArray) { if (columnFamilyId == 0) merge(key, value) }
    open fun merge(key: ByteArray, value: ByteArray) {}
    open fun delete(columnFamilyId: Int, key: ByteArray) { if (columnFamilyId == 0) delete(key) }
    open fun delete(key: ByteArray) {}
    open fun singleDelete(columnFamilyId: Int, key: ByteArray) { if (columnFamilyId == 0) singleDelete(key) }
    open fun singleDelete(key: ByteArray) {}
    open fun deleteRange(columnFamilyId: Int, beginKey: ByteArray, endKey: ByteArray) { if (columnFamilyId == 0) deleteRange(beginKey, endKey) }
    open fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {}
    open fun logData(blob: ByteArray) {}
    open fun putBlobIndex(columnFamilyId: Int, key: ByteArray, value: ByteArray) {}
    open fun markBeginPrepare() {}
    open fun markEndPrepare(xid: ByteArray) {}
    open fun markNoop(emptyBatch: Boolean) {}
    open fun markRollback(xid: ByteArray) {}
    open fun markCommit(xid: ByteArray) {}
    open fun markCommitWithTimestamp(xid: ByteArray, timestamp: ByteArray) {}

    /** Checked before each record. Returning false stops iteration successfully. */
    open fun shouldContinue(): Boolean = true
}

/**
 * Iterates without applying the batch. Exceptions from callbacks stop iteration and are rethrown
 * after the native call returns. The batch must own an open handle. For a borrowed view,
 * copy its [WriteBatch.data] with [writeBatchFromData] while its parent is still open.
 * Do not mutate or close the batch from a callback.
 */
expect fun WriteBatch.iterate(handler: WriteBatchHandler)

/** Reconstruct a batch from [WriteBatch.data]. Iteration validates the record contents. */
expect fun writeBatchFromData(serialized: ByteArray): WriteBatch
