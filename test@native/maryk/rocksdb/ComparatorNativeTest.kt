package maryk.rocksdb

import maryk.ByteBuffer
import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertTrue

class ComparatorNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun comparatorCallbackExceptionDoesNotEscapeWritePath() {
        val dbPath = createTestDBFolder("ComparatorNativeTest_callback_exception")
        val callbackCount = ThreadSafeCounter()

        Options()
            .setCreateIfMissing(true)
            .setComparator(ThrowingComparator(callbackCount))
            .use { options ->
                openRocksDB(options, dbPath).use { db ->
                    db.put("key-a".encodeToByteArray(), "value-a".encodeToByteArray())
                    db.put("key-b".encodeToByteArray(), "value-b".encodeToByteArray())
                    db.compactRange()
                }
            }

        assertTrue(callbackCount.value() > 0, "Expected RocksDB to invoke the comparator callback")
    }
}

private class ThrowingComparator(
    private val callbackCount: ThreadSafeCounter
) : AbstractComparator() {
    override fun name(): String = "ThrowingComparator"

    override fun compare(a: ByteBuffer, b: ByteBuffer): Int {
        callbackCount.increment()
        throw IllegalStateException("comparator failure")
    }
}
