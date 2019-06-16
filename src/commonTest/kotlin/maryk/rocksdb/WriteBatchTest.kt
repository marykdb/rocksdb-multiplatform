package maryk.rocksdb

import maryk.assertContentEquals
import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.rocksdb.util.CapturingWriteBatchHandler
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.DELETE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.DELETE_RANGE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.LOG
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.MERGE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.PUT
import maryk.rocksdb.util.CapturingWriteBatchHandler.Action.SINGLE_DELETE
import maryk.rocksdb.util.CapturingWriteBatchHandler.Event
import maryk.rocksdb.util.WriteBatchGetter
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * This class mimics the db/write_batch_test.cc
 * in the c++ rocksdb library.
 */
class WriteBatchTest {
    init {
        loadRocksDBLibrary()
    }

    private fun createTestFolder() = createTestDBFolder("WriteBatchTest")

    @Test
    fun emptyWriteBatch() {
        WriteBatch().use { batch ->
            assertEquals(0, batch.count())
        }
    }

    @Test
    fun multipleBatchOperations() {
        val foo = "foo".encodeToByteArray()
        val bar = "bar".encodeToByteArray()
        val box = "box".encodeToByteArray()
        val baz = "baz".encodeToByteArray()
        val boo = "boo".encodeToByteArray()
        val hoo = "hoo".encodeToByteArray()
        val hello = "hello".encodeToByteArray()

        WriteBatch().use { batch ->
            batch.put(foo, bar)
            batch.delete(box)
            batch.put(baz, boo)
            batch.merge(baz, hoo)
            batch.singleDelete(foo)
            batch.deleteRange(baz, foo)
            batch.putLogData(hello)

            CapturingWriteBatchHandler().use { handler ->
                batch.iterate(handler)

                assertEquals(7, handler.getEvents().size)

                assertEquals(Event(PUT, foo, bar), handler.getEvents()[0])
                assertEquals(Event(DELETE, box, null), handler.getEvents()[1])
                assertEquals(Event(PUT, baz, boo), handler.getEvents()[2])
                assertEquals(Event(MERGE, baz, hoo), handler.getEvents()[3])
                assertEquals(Event(SINGLE_DELETE, foo, null), handler.getEvents()[4])
                assertEquals(Event(DELETE_RANGE, baz, foo), handler.getEvents()[5])
                assertEquals(Event(LOG, null, hello), handler.getEvents()[6])
            }
        }
    }

//    @Test
//    fun testAppendOperation() {
//        WriteBatch().use { b1 ->
//            WriteBatch().use { b2 ->
//                WriteBatchTestInternalHelper.setSequence(b1, 200)
//                WriteBatchTestInternalHelper.setSequence(b2, 300)
//                WriteBatchTestInternalHelper.append(b1, b2)
//                assertEquals(0, getWriteBatchContents(b1).size)
//                assertEquals(0, b1.count())
//                b2.put("a".encodeToByteArray(), "va".encodeToByteArray())
//                WriteBatchTestInternalHelper.append(b1, b2)
//                assertEquals(
//                    "Put(a, va)@200",
//                    getWriteBatchContents(b1).decodeToString()
//                )
//                assertEquals(1, b1.count())
//                b2.clear()
//                b2.put("b".encodeToByteArray(), "vb".encodeToByteArray())
//                WriteBatchTestInternalHelper.append(b1, b2)
//                assertEquals("Put(a, va)@200" + "Put(b, vb)@201", getWriteBatchContents(b1).decodeToString())
//                assertEquals(2, b1.count())
//                b2.delete("foo".encodeToByteArray())
//                WriteBatchTestInternalHelper.append(b1, b2)
//                assertEquals(
//                    "Put(a, va)@200" +
//                        "Put(b, vb)@202" +
//                        "Put(b, vb)@201" +
//                        "Delete(foo)@203",
//                    getWriteBatchContents(b1).decodeToString()
//                )
//                assertEquals(4, b1.count())
//            }
//        }
//    }
//
//    @Test
//    fun blobOperation() {
//        WriteBatch().use { batch ->
//            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
//            batch.put("k2".encodeToByteArray(), "v2".encodeToByteArray())
//            batch.put("k3".encodeToByteArray(), "v3".encodeToByteArray())
//            batch.putLogData("blob1".encodeToByteArray())
//            batch.delete("k2".encodeToByteArray())
//            batch.putLogData("blob2".encodeToByteArray())
//            batch.merge("foo".encodeToByteArray(), "bar".encodeToByteArray())
//            assertEquals(5, batch.count())
//            assertEquals(
//                ("Merge(foo, bar)@4" +
//                    "Put(k1, v1)@0" +
//                    "Delete(k2)@3" +
//                    "Put(k2, v2)@1" +
//                    "Put(k3, v3)@2"),
//                String(getContents(batch), UTF_8)
//            )
//        }
//    }

    @Test
    fun savePoints() {
        WriteBatch().use { batch ->
            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            batch.put("k2".encodeToByteArray(), "v2".encodeToByteArray())
            batch.put("k3".encodeToByteArray(), "v3".encodeToByteArray())

            assertEquals("v1", getFromWriteBatch(batch, "k1"))
            assertEquals("v2", getFromWriteBatch(batch, "k2"))
            assertEquals("v3", getFromWriteBatch(batch, "k3"))

            batch.setSavePoint()

            batch.delete("k2".encodeToByteArray())
            batch.put("k3".encodeToByteArray(), "v3-2".encodeToByteArray())

            assertNull(getFromWriteBatch(batch, "k2"))
            assertEquals("v3-2", getFromWriteBatch(batch, "k3"))

            batch.setSavePoint()

            batch.put("k3".encodeToByteArray(), "v3-3".encodeToByteArray())
            batch.put("k4".encodeToByteArray(), "v4".encodeToByteArray())

            assertEquals("v3-3", getFromWriteBatch(batch, "k3"))
            assertEquals("v4", getFromWriteBatch(batch, "k4"))

            batch.rollbackToSavePoint()

            assertNull(getFromWriteBatch(batch, "k2"))
            assertEquals("v3-2", getFromWriteBatch(batch, "k3"))
            assertNull(getFromWriteBatch(batch, "k4"))


            batch.rollbackToSavePoint()

            assertEquals("v1", getFromWriteBatch(batch, "k1"))
            assertEquals("v2", getFromWriteBatch(batch, "k2"))
            assertEquals("v3", getFromWriteBatch(batch, "k3"))
            assertNull(getFromWriteBatch(batch, "k4"))
        }
    }

    @Test
    fun deleteRange() {
        openRocksDB(createTestFolder()).use { db ->
            WriteBatch().use { batch ->
                WriteOptions().use { wOpt ->
                    db.put("key1".encodeToByteArray(), "value".encodeToByteArray())
                    db.put("key2".encodeToByteArray(), "12345678".encodeToByteArray())
                    db.put("key3".encodeToByteArray(), "abcdefg".encodeToByteArray())
                    db.put("key4".encodeToByteArray(), "xyz".encodeToByteArray())
                    assertContentEquals("value".encodeToByteArray(), db.get("key1".encodeToByteArray()))
                    assertContentEquals("12345678".encodeToByteArray(), db.get("key2".encodeToByteArray()))
                    assertContentEquals("abcdefg".encodeToByteArray(), db.get("key3".encodeToByteArray()))
                    assertContentEquals("xyz".encodeToByteArray(), db.get("key4".encodeToByteArray()))

                    batch.deleteRange("key2".encodeToByteArray(), "key4".encodeToByteArray())
                    db.write(wOpt, batch)

                    assertContentEquals("value".encodeToByteArray(), db.get("key1".encodeToByteArray()))
                    assertNull(db.get("key2".encodeToByteArray()))
                    assertNull(db.get("key3".encodeToByteArray()))
                    assertContentEquals("xyz".encodeToByteArray(), db.get("key4".encodeToByteArray()))
                }
            }
        }
    }

    @Test
    fun restorePoints() {
        WriteBatch().use { batch ->
            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            batch.put("k2".encodeToByteArray(), "v2".encodeToByteArray())

            batch.setSavePoint()

            batch.put("k1".encodeToByteArray(), "123456789".encodeToByteArray())
            batch.delete("k2".encodeToByteArray())

            batch.rollbackToSavePoint()

            CapturingWriteBatchHandler().use { handler ->
                batch.iterate(handler)

                assertEquals(2, handler.getEvents().size)
                assertEquals(Event(PUT, "k1".encodeToByteArray(), "v1".encodeToByteArray()), handler.getEvents()[0])
                assertEquals(Event(PUT, "k2".encodeToByteArray(), "v2".encodeToByteArray()), handler.getEvents()[1])
            }
        }
    }

    @Test
    fun restorePoints_withoutSavePoints() {
        WriteBatch().use { batch ->
            assertFailsWith<RocksDBException> {
                batch.rollbackToSavePoint()
            }
        }
    }

    @Test
    fun restorePoints_withoutSavePoints_nested() {
        WriteBatch().use { batch ->
            batch.setSavePoint()
            batch.rollbackToSavePoint()

            assertFailsWith<RocksDBException> {
                // without previous corresponding setSavePoint
                batch.rollbackToSavePoint()
            }
        }
    }

    @Test
    fun popSavePoint() {
        WriteBatch().use { batch ->
            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            batch.put("k2".encodeToByteArray(), "v2".encodeToByteArray())

            batch.setSavePoint()

            batch.put("k1".encodeToByteArray(), "123456789".encodeToByteArray())
            batch.delete("k2".encodeToByteArray())

            batch.setSavePoint()

            batch.popSavePoint()

            batch.rollbackToSavePoint()

            CapturingWriteBatchHandler().use { handler ->
                batch.iterate(handler)

                assertEquals(2, handler.getEvents().size)
                assertEquals(Event(PUT, "k1".encodeToByteArray(), "v1".encodeToByteArray()), handler.getEvents()[0])
                assertEquals(Event(PUT, "k2".encodeToByteArray(), "v2".encodeToByteArray()), handler.getEvents()[1])
            }
        }
    }

    @Test
    fun popSavePoint_withoutSavePoints() {
        WriteBatch().use { batch ->
            assertFailsWith<RocksDBException> {
                batch.popSavePoint()
            }
        }
    }

    @Test
    fun popSavePoint_withoutSavePoints_nested() {
        WriteBatch().use { batch ->
            batch.setSavePoint()
            batch.popSavePoint()

            assertFailsWith<RocksDBException> {
                // without previous corresponding setSavePoint
                batch.popSavePoint()
            }
        }
    }

    @Test
    fun maxBytes() {
        WriteBatch().use { batch ->
            batch.setMaxBytes(19)

            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
        }
    }

    @Test
    fun maxBytes_over() {
        WriteBatch().use { batch ->
            batch.setMaxBytes(1)
            assertFailsWith<RocksDBException> {
                batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            }
        }
    }

    @Test
    fun data() {
        WriteBatch().use { batch1 ->
            batch1.delete("k0".encodeToByteArray())
            batch1.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            batch1.put("k2".encodeToByteArray(), "v2".encodeToByteArray())
            batch1.put("k3".encodeToByteArray(), "v3".encodeToByteArray())
            batch1.putLogData("log1".encodeToByteArray())
            batch1.merge("k2".encodeToByteArray(), "v22".encodeToByteArray())
            batch1.delete("k3".encodeToByteArray())

            val serialized = batch1.data()

            WriteBatch(serialized).use { batch2 ->
                assertEquals(batch1.count(), batch2.count())

                CapturingWriteBatchHandler().use { handler1 ->
                    batch1.iterate(handler1)

                    CapturingWriteBatchHandler().use { handler2 ->
                        batch2.iterate(handler2)

                        assertEquals(handler1.getEvents(), handler2.getEvents())
                    }
                }
            }
        }
    }

    @Test
    fun dataSize() {
        WriteBatch().use { batch ->
            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())

            assertEquals(19, batch.getDataSize())
        }
    }

    @Test
    fun hasPut() {
        WriteBatch().use { batch ->
            assertFalse(batch.hasPut())

            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())

            assertTrue(batch.hasPut())
        }
}

    @Test
    fun hasDelete() {
        WriteBatch().use { batch ->
            assertFalse(batch.hasDelete())

            batch.delete("k1".encodeToByteArray())

            assertTrue(batch.hasDelete())
        }
    }

    @Test
    fun hasSingleDelete() {
        WriteBatch().use { batch ->
            assertFalse(batch.hasSingleDelete())

            batch.singleDelete("k1".encodeToByteArray())

            assertTrue(batch.hasSingleDelete())
        }
    }

    @Test
    fun hasDeleteRange() {
        WriteBatch().use { batch ->
            assertFalse(batch.hasDeleteRange())

            batch.deleteRange("k1".encodeToByteArray(), "k2".encodeToByteArray())

            assertTrue(batch.hasDeleteRange())
        }
    }

    @Test
    fun hasBeginPrepareRange() {
        WriteBatch().use { batch -> assertFalse(batch.hasBeginPrepare()) }
    }

    @Test
    fun hasEndPrepareRange() {
        WriteBatch().use { batch -> assertFalse(batch.hasEndPrepare()) }
    }

    @Test
    fun hasCommit() {
        WriteBatch().use { batch -> assertFalse(batch.hasCommit()) }
    }

    @Test
    fun hasRollback() {
        WriteBatch().use { batch -> assertFalse(batch.hasRollback()) }
    }

    @Test
    fun walTerminationPoint() {
        WriteBatch().use { batch ->
            var walTerminationPoint: WriteBatchSavePoint = batch.getWalTerminationPoint()
            assertTrue(walTerminationPoint.isCleared())

            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())

            batch.markWalTerminationPoint()

            walTerminationPoint = batch.getWalTerminationPoint()
            assertEquals(19, walTerminationPoint.getSize())
            assertEquals(1, walTerminationPoint.getCount())
            assertEquals(2, walTerminationPoint.getContentFlags())
        }
}

    @Test
    fun getWriteBatch() {
        WriteBatch().use { batch ->
            assertEquals(batch, batch.getWriteBatch())
        }
    }
}

internal fun getFromWriteBatch(wb: WriteBatch, key: String): String? {
    val getter = WriteBatchGetter(key.encodeToByteArray())
    wb.iterate(getter)
    return when(val value = getter.value) {
        null -> null
        else -> value.decodeToString()
    }
}
