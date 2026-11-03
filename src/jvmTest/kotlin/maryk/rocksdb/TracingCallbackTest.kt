package maryk.rocksdb

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import kotlin.test.assertNull
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TracingCallbackTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun sessionRetainsWriterAndClosesOutputExactlyOnce() {
        val path = createTestDBFolder("TracingCallbackTest_lifetime")
        val output = object : ByteArrayOutputStream() {
            var closeCount = 0
            override fun close() {
                closeCount++
                super.close()
            }
        }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).use { db ->
                val session = db.startFileTrace(TraceOptions(), output)
                db.put(byteArrayOf(1), byteArrayOf(2))
                assertTrue(output.size() > 0)
                assertEquals(0, output.closeCount)
                session.close()
                session.close()
                assertEquals(1, output.closeCount)
            }
        }
    }

    @Test
    fun callbackWriteFailurePropagatesAndClosesOutput() {
        val path = createTestDBFolder("TracingCallbackTest_writeFailure")
        var closed = false
        val output = object : OutputStream() {
            override fun write(value: Int) {
                throw IOException("trace output failed")
            }
            override fun close() {
                closed = true
            }
        }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).use { db ->
                assertFailsWith<RocksDBException> { db.startFileTrace(TraceOptions(), output) }
                assertTrue(closed)
                db.startFileTrace(TraceOptions(), ByteArrayOutputStream()).use {
                    db.put(byteArrayOf(1), byteArrayOf(2))
                }
            }
        }
    }
    @Test
    fun droppedSessionKeepsTraceRegisteredUntilExplicitEnd() {
        val path = createTestDBFolder("TracingCallbackTest_droppedSession")
        val output = object : ByteArrayOutputStream() {
            var closeCount = 0
            override fun close() {
                closeCount++
                super.close()
            }
        }
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, path).use { db ->
                fun startAndForget() = WeakReference(db.startFileTrace(TraceOptions(), output))
                val reference = startAndForget()
                repeat(100) {
                    if (reference.get() == null) return@repeat
                    System.gc()
                    Thread.sleep(10)
                }
                assertNull(reference.get(), "The registry must retain the writer, not the session or database")
                assertFailsWith<IllegalStateException> {
                    db.startFileTrace(TraceOptions(), ByteArrayOutputStream())
                }
                db.put(byteArrayOf(1), byteArrayOf(2))
                db.endFileTrace()
                assertEquals(1, output.closeCount)
                db.startFileTrace(TraceOptions(), ByteArrayOutputStream()).use { }
                db.endFileTrace()
            }
        }
    }

}
