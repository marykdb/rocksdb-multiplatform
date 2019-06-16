package maryk.rocksdb

import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlushTest {
    private fun createTestFolder() = createTestDBFolder("FlushTest")

    @Test
    fun flush() {
        Options().apply {
            setCreateIfMissing(true)
            setMaxWriteBufferNumber(10)
            setMinWriteBufferNumberToMerge(10)
        }.use { options ->
            WriteOptions().apply {
                setDisableWAL(true)
            }.use { wOpt ->
                FlushOptions().apply {
                    setWaitForFlush(true)
                }.use { flushOptions ->
                    assertTrue(flushOptions.waitForFlush())

                    openRocksDB(
                        options,
                        createTestFolder()
                    ).use { db ->
                        db.put(wOpt, "key1".encodeToByteArray(), "value1".encodeToByteArray())
                        db.put(wOpt, "key2".encodeToByteArray(), "value2".encodeToByteArray())
                        db.put(wOpt, "key3".encodeToByteArray(), "value3".encodeToByteArray())
                        db.put(wOpt, "key4".encodeToByteArray(), "value4".encodeToByteArray())
                        assertEquals("4", db.getProperty("rocksdb.num-entries-active-mem-table"))
                        db.flush(flushOptions)
                        assertEquals("0", db.getProperty("rocksdb.num-entries-active-mem-table"))
                    }
                }
            }
        }
    }
}
