package maryk.rocksdb.util

import maryk.rocksdb.WriteBatchHandler
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.DELETE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.DELETE_RANGE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.LOG
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MARK_BEGIN_PREPARE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MARK_COMMIT
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MARK_END_PREPARE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MARK_NOOP
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MARK_ROLLBACK
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MERGE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.PUT
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.PUT_BLOB_INDEX
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.SINGLE_DELETE

/**
 * A simple WriteBatch Handler which adds a record
 * of each event that it receives to a list
 */
class CapturingWriteBatchHandler : WriteBatchHandler() {
    private val events = mutableListOf<Event>()

    /**
     * Returns a copy of the current events list
     *
     * @return a list of the events which have happened upto now
     */
    fun getEvents() = events.toList()

    override fun put(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    ) {
        events.add(Event(PUT, columnFamilyId, key, value))
    }

    override fun put(key: ByteArray, value: ByteArray) {
        events.add(Event(PUT, key, value))
    }

    override fun merge(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    ) {
        events.add(Event(MERGE, columnFamilyId, key, value))
    }

    override fun merge(key: ByteArray, value: ByteArray) {
        events.add(Event(MERGE, key, value))
    }

    override fun delete(columnFamilyId: Int, key: ByteArray) {
        events.add(Event(DELETE, columnFamilyId, key, null))
    }

    override fun delete(key: ByteArray) {
        events.add(Event(DELETE, key, null))
    }

    override fun singleDelete(columnFamilyId: Int, key: ByteArray) {
        events.add(
            Event(
                SINGLE_DELETE,
                columnFamilyId, key, null
            )
        )
    }

    override fun singleDelete(key: ByteArray) {
        events.add(Event(SINGLE_DELETE, key, null))
    }

    override fun deleteRange(
        columnFamilyId: Int, beginKey: ByteArray,
        endKey: ByteArray
    ) {
        events.add(
            Event(
                DELETE_RANGE, columnFamilyId, beginKey,
                endKey
            )
        )
    }

    override fun deleteRange(beginKey: ByteArray, endKey: ByteArray) {
        events.add(Event(DELETE_RANGE, beginKey, endKey))
    }

    override fun logData(blob: ByteArray) {
        events.add(Event(LOG, null, blob))
    }

    override fun putBlobIndex(
        columnFamilyId: Int, key: ByteArray,
        value: ByteArray
    ) {
        events.add(Event(PUT_BLOB_INDEX, key, value))
    }

    // @Throws(RocksDBException::class)
    override fun markBeginPrepare() {
        events.add(
            Event(
                MARK_BEGIN_PREPARE, null,
                null
            )
        )
    }

    // @Throws(RocksDBException::class)
    override fun markEndPrepare(xid: ByteArray) {
        events.add(
            Event(
                MARK_END_PREPARE, null,
                null
            )
        )
    }

    // @Throws(RocksDBException::class)
    override fun markNoop(emptyBatch: Boolean) {
        events.add(Event(MARK_NOOP, null, null))
    }

    // @Throws(RocksDBException::class)
    override fun markRollback(xid: ByteArray) {
        events.add(Event(MARK_ROLLBACK, null, null))
    }

    // @Throws(RocksDBException::class)
    override fun markCommit(xid: ByteArray) {
        events.add(Event(MARK_COMMIT, null, null))
    }

    data class Event(
        val action: Action,
        val columnFamilyId: Int,
        val key: ByteArray?,
        val value: ByteArray?
    ) {
        constructor(action: Action, key: ByteArray?, value: ByteArray?) : this(action, 0, key, value)

        override fun equals(other: Any?) = when {
            this === other -> true
            other !is Event -> false
            action != other.action -> false
            columnFamilyId != other.columnFamilyId -> false
            key != null -> when {
                other.key == null -> false
                !key.contentEquals(other.key) -> false
                else -> true
            }
            other.key != null -> false
            value != null -> when {
                other.value == null -> false
                !value.contentEquals(other.value) -> false
                else -> true
            }
            other.value != null -> false
            else -> true
        }

        override fun hashCode(): Int {
            var result = action.hashCode()
            result = 31 * result + columnFamilyId
            result = 31 * result + (key?.contentHashCode() ?: 0)
            result = 31 * result + (value?.contentHashCode() ?: 0)
            return result
        }
    }

    /**
     * Enumeration of Write Batch
     * event actions
     */
    enum class Action {
        PUT, MERGE, DELETE, SINGLE_DELETE, DELETE_RANGE, LOG, PUT_BLOB_INDEX,
        MARK_BEGIN_PREPARE, MARK_END_PREPARE, MARK_NOOP, MARK_COMMIT,
        MARK_ROLLBACK
    }
}
