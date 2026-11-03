package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class WriteBatchIterationTest {
    init { loadRocksDBLibrary() }

    @Test
    fun visitsRecordsInOrderAndRetainsTheirBytes() {
        val seen = mutableListOf<String>()
        val keys = mutableListOf<ByteArray>()
        WriteBatch().use { batch ->
            batch.put(byteArrayOf(0, -1), byteArrayOf(1, 0))
            batch.merge(byteArrayOf(3), byteArrayOf(4))
            batch.delete(byteArrayOf(5))
            batch.singleDelete(byteArrayOf(6))
            batch.deleteRange(byteArrayOf(7), byteArrayOf(8))
            batch.putLogData(byteArrayOf(9, 0))
            val handler = object : WriteBatchHandler() {
                override fun put(columnFamilyId: Int, key: ByteArray, value: ByteArray) {
                    assertEquals(0, columnFamilyId)
                    keys += key
                    assertContentEquals(byteArrayOf(1, 0), value)
                    seen += "put"
                }
                override fun merge(columnFamilyId: Int, key: ByteArray, value: ByteArray) { seen += "merge" }
                override fun delete(columnFamilyId: Int, key: ByteArray) { seen += "delete" }
                override fun singleDelete(columnFamilyId: Int, key: ByteArray) { seen += "singleDelete" }
                override fun deleteRange(columnFamilyId: Int, beginKey: ByteArray, endKey: ByteArray) {
                    assertContentEquals(byteArrayOf(7), beginKey)
                    assertContentEquals(byteArrayOf(8), endKey)
                    seen += "deleteRange"
                }
                override fun logData(blob: ByteArray) {
                    assertContentEquals(byteArrayOf(9, 0), blob)
                    seen += "logData"
                }
            }
            writeBatchFromData(batch.data()).use { copy -> copy.iterate(handler) }
        }
        assertEquals(listOf("put", "merge", "delete", "singleDelete", "deleteRange", "logData"), seen)
        assertContentEquals(byteArrayOf(0, -1), keys.single())
    }

    @Test
    fun retainsColumnFamilyIds() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestDBFolder("WriteBatchIterationTest_cf")).use { db ->
                ColumnFamilyOptions().use { cfOptions ->
                    db.createColumnFamily(ColumnFamilyDescriptor("other".encodeToByteArray(), cfOptions)).use { cf ->
                        WriteBatch().use { batch ->
                            batch.put(cf, byteArrayOf(1), byteArrayOf(2))
                            var id: Int? = null
                            batch.iterate(object : WriteBatchHandler() {
                                override fun put(columnFamilyId: Int, key: ByteArray, value: ByteArray) { id = columnFamilyId }
                            })
                            assertEquals(cf.getID(), id)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun stopsAndRethrowsCallbackErrors() {
        WriteBatch().use { batch ->
            repeat(3) { batch.put(byteArrayOf(it.toByte()), byteArrayOf(1)) }
            var visited = 0
            batch.iterate(object : WriteBatchHandler() {
                override fun put(key: ByteArray, value: ByteArray) { visited++ }
                override fun shouldContinue(): Boolean = visited < 1
            })
            assertEquals(1, visited)
            val failure = IllegalStateException("callback failure")
            assertSame(failure, assertFailsWith<IllegalStateException> {
                batch.iterate(object : WriteBatchHandler() {
                    override fun put(key: ByteArray, value: ByteArray) { throw failure }
                })
            })
            assertSame(failure, assertFailsWith<IllegalStateException> {
                batch.iterate(object : WriteBatchHandler() {
                    override fun shouldContinue(): Boolean = throw failure
                })
            })
        }
    }

    @Test
    fun visitsWalMarkersAndBlobIndexes() {
        // RocksDB's stable batch encoding: sequence (8), write count (4), tagged records.
        // Transaction markers do not contribute to the write count; the blob record does.
        val bytes = ByteArray(12) + byteArrayOf(
            0x09, // begin prepare
            0x0a, 1, 'x'.code.toByte(), // end prepare with XID
            0x0b, 1, 'x'.code.toByte(), // commit
            0x0c, 1, 'x'.code.toByte(), // rollback
            0x0d, // noop, empty batch
            0x15, 1, 't'.code.toByte(), 1, 'x'.code.toByte(), // timestamp then XID
            0x11, 1, 'k'.code.toByte(), 1, 'v'.code.toByte(), // blob index
        )
        bytes[8] = 1
        val seen = mutableListOf<String>()
        writeBatchFromData(bytes).use { batch ->
            batch.iterate(object : WriteBatchHandler() {
                override fun markBeginPrepare() { seen += "begin" }
                override fun markEndPrepare(xid: ByteArray) { seen += "end:${xid.decodeToString()}" }
                override fun markCommit(xid: ByteArray) { seen += "commit:${xid.decodeToString()}" }
                override fun markRollback(xid: ByteArray) { seen += "rollback:${xid.decodeToString()}" }
                override fun markNoop(emptyBatch: Boolean) { seen += "noop:$emptyBatch" }
                override fun markCommitWithTimestamp(xid: ByteArray, timestamp: ByteArray) {
                    seen += "timestamp:${xid.decodeToString()}:${timestamp.decodeToString()}"
                }
                override fun putBlobIndex(columnFamilyId: Int, key: ByteArray, value: ByteArray) {
                    assertEquals(0, columnFamilyId)
                    seen += "blob:${key.decodeToString()}:${value.decodeToString()}"
                }
            })
        }
        assertEquals(listOf("begin", "end:x", "commit:x", "rollback:x", "noop:true", "timestamp:x:t", "blob:k:v"), seen)
    }

    @Test
    fun rejectsTruncatedAndCorruptBatches() {
        assertFailsWith<IllegalArgumentException> { writeBatchFromData(ByteArray(11)) }
        writeBatchFromData(ByteArray(12) + byteArrayOf(1)).use { batch ->
            assertFailsWith<RocksDBException> { batch.iterate(object : WriteBatchHandler() {}) }
        }
    }

    @Test
    fun rejectsClosedBatchBeforeCallingNative() {
        val batch = WriteBatch()
        batch.close()
        assertFailsWith<IllegalStateException> {
            batch.iterate(object : WriteBatchHandler() {})
        }
    }
}
