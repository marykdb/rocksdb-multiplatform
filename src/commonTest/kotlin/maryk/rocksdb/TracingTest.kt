package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import maryk.createFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TracingTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun traceSessionWritesFileStopsAndCanBeReused() {
        val path = createTestDBFolder("TracingTest_session").also { createFolder(it) }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, "$path/db").use { db ->
                val tracePath = "$path/operations.trace"
                val session = db.startTrace(TraceOptions(1_048_576L), tracePath)
                repeat(3) {
                    db.put("key-$it".encodeToByteArray(), "value-$it".encodeToByteArray())
                }
                session.close()
                session.close()
                val size = traceFileSize(tracePath)
                assertTrue(size > 0, "Closing a trace session must leave a readable trace file")
                db.put("after".encodeToByteArray(), "after".encodeToByteArray())
                assertEquals(size, traceFileSize(tracePath), "Tracing must stop when the session closes")

                db.startTrace(TraceOptions(), "$path/second.trace").use {
                    db.get("key-0".encodeToByteArray())
                }
                assertTrue(traceFileSize("$path/second.trace") > 0)
            }
        }
    }

    @Test
    fun traceSessionCanCloseAfterDatabase() {
        val path = createTestDBFolder("TracingTest_dbClose").also { createFolder(it) }
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, "$path/db")
            val session = db.startTrace(TraceOptions(), "$path/operations.trace")
            try {
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
            } finally {
                db.close()
                session.close()
            }
            assertTrue(traceFileSize("$path/operations.trace") > 0)
        }
    }

    @Test
    fun explicitEndAndStaleSessionCloseDoNotStopTheNextTrace() {
        val path = createTestDBFolder("TracingTest_explicitEnd").also { createFolder(it) }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, "$path/db").use { db ->
                val first = db.startTrace(TraceOptions(), "$path/first.trace")
                db.endFileTrace()
                db.startTrace(TraceOptions(), "$path/second.trace").use {
                    first.close()
                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                }
                db.endFileTrace()
                assertTrue(traceFileSize("$path/second.trace") > 0)
            }
        }
    }

    @Test
    fun failedTraceStartLeavesDatabaseUsable() {
        val path = createTestDBFolder("TracingTest_failedStart").also { createFolder(it) }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, "$path/db").use { db ->
                assertFailsWith<RocksDBException> {
                    db.startTrace(TraceOptions(), "$path/missing/operations.trace")
                }
                db.startTrace(TraceOptions(), "$path/operations.trace").use {
                    db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                }
                assertTrue(traceFileSize("$path/operations.trace") > 0)
            }
        }
    }
}

internal expect fun traceFileSize(path: String): Long
