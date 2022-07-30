package maryk.rocksdb

import maryk.assertContentEquals
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
