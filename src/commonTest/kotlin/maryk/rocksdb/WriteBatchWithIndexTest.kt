package maryk.rocksdb

import maryk.ByteBuffer
import maryk.allocateDirectByteBuffer
import maryk.assertContentEquals
import maryk.decodeToString
import maryk.encodeToByteArray
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WriteBatchWithIndexTest {
    private fun createTestFolder() = createTestDBFolder("WriteBatchWithIndexTest")

    @Test
    fun readYourOwnWrites() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                val k1 = "key1".encodeToByteArray()
                val v1 = "value1".encodeToByteArray()
                val k2 = "key2".encodeToByteArray()
                val v2 = "value2".encodeToByteArray()

                db.put(k1, v1)
                db.put(k2, v2)

                WriteBatchWithIndex(true).use { wbwi ->
                    db.newIterator().use { base ->
                        wbwi.newIteratorWithBase(base).use {
                            it.seek(k1)
                            assertTrue(it.isValid())
                            assertContentEquals(k1, it.key())
                            assertContentEquals(v1, it.value())

                            it.seek(k2)
                            assertTrue(it.isValid())
                            assertContentEquals(k2, it.key())
                            assertContentEquals(v2, it.value())

                            //put data to the write batch and make sure we can read it.
                            val k3 = "key3".encodeToByteArray()
                            val v3 = "value3".encodeToByteArray()
                            wbwi.put(k3, v3)
                            it.seek(k3)
                            assertTrue(it.isValid())
                            assertContentEquals(k3, it.key())
                            assertContentEquals(v3, it.value())

                            //update k2 in the write batch and check the value
                            val v2Other = "otherValue2".encodeToByteArray()
                            wbwi.put(k2, v2Other)
                            it.seek(k2)
                            assertTrue(it.isValid())
                            assertContentEquals(k2, it.key())
                            assertContentEquals(v2Other, it.value())

                            //delete k1 and make sure we can read back the write
                            wbwi.delete(k1)
                            it.seek(k1)
                            assertFalse(k1.contentEquals(it.key()))

                            //reinsert k1 and make sure we see the new value
                            val v1Other = "otherValue1".encodeToByteArray()
                            wbwi.put(k1, v1Other)
                            it.seek(k1)
                            assertTrue(it.isValid())
                            assertContentEquals(k1, it.key())
                            assertContentEquals(v1Other, it.value())

                            //single remove k3 and make sure we can read back the write
                            wbwi.singleDelete(k3)
                            it.seek(k3)
                            assertEquals(false, it.isValid())

                            //reinsert k3 and make sure we see the new value
                            val v3Other = "otherValue3".encodeToByteArray()
                            wbwi.put(k3, v3Other)
                            it.seek(k3)
                            assertTrue(it.isValid())
                            assertContentEquals(k3, it.key())
                            assertContentEquals(v3Other, it.value())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun writeBatchWithIndex() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->

                val k1 = "key1".encodeToByteArray()
                val v1 = "value1".encodeToByteArray()
                val k2 = "key2".encodeToByteArray()
                val v2 = "value2".encodeToByteArray()

                WriteBatchWithIndex().use { wbwi ->
                    WriteOptions().use { wOpt ->
                        wbwi.put(k1, v1)
                        wbwi.put(k2, v2)

                        db.write(wOpt, wbwi)
                    }
                }

                assertContentEquals(v1, db[k1])
                assertContentEquals(v2, db[k2])
            }
        }
    }

    @Test
    operator fun iterator() {
        WriteBatchWithIndex(true).use { wbwi ->
            val k1 = "key1"
            val v1 = "value1"
            val k2 = "key2"
            val v2 = "value2"
            val k3 = "key3"
            val v3 = "value3"
            val k4 = "key4"
            val k5 = "key5"
            val k6 = "key6"
            val k7 = "key7"
            val v8 = "value8"
            val k1b = k1.encodeToByteArray()
            val v1b = v1.encodeToByteArray()
            val k2b = k2.encodeToByteArray()
            val v2b = v2.encodeToByteArray()
            val k3b = k3.encodeToByteArray()
            val v3b = v3.encodeToByteArray()
            val k4b = k4.encodeToByteArray()
            val k5b = k5.encodeToByteArray()
            val k6b = k6.encodeToByteArray()
            val k7b = k7.encodeToByteArray()
            val v8b = v8.encodeToByteArray()

            // add put records
            wbwi.put(k1b, v1b)
            wbwi.put(k2b, v2b)
            wbwi.put(k3b, v3b)

            // add a deletion record
            wbwi.delete(k4b)

            // add a single deletion record
            wbwi.singleDelete(k5b)

            // add a delete range record
            wbwi.deleteRange(k6b, k7b)

            // add a log record
            wbwi.putLogData(v8b)

            val expected = arrayOf(
                WriteEntry(
                    WriteType.PUT,
                    DirectSlice(k1), DirectSlice(v1)
                ),
                WriteEntry(
                    WriteType.PUT,
                    DirectSlice(k2), DirectSlice(v2)
                ),
                WriteEntry(
                    WriteType.PUT,
                    DirectSlice(k3), DirectSlice(v3)
                ),
                WriteEntry(
                    WriteType.DELETE,
                    DirectSlice(k4), DirectSliceNone
                ),
                WriteEntry(
                    WriteType.SINGLE_DELETE,
                    DirectSlice(k5), DirectSliceNone
                ),
                WriteEntry(
                    WriteType.DELETE_RANGE,
                    DirectSlice(k6), DirectSlice(k7)
                )
            )

            wbwi.newIterator().use {
                // direct access - seek to key offsets
                val testOffsets = intArrayOf(2, 0, 3, 4, 1, 5)

                for (i in testOffsets.indices) {
                    val testOffset = testOffsets[i]
                    val key = toArray(expected[testOffset].getKey().data())

                    it.seek(key)

                    assertTrue(it.isValid())

                    val entry = it.entry()
                    assertEquals(expected[testOffset], entry)
                }

                // forward iterative access
                var i = 0
                it.seekToFirst()
                while (it.isValid()) {
                    assertEquals(expected[i++], it.entry())
                    it.next()
                }

                // reverse iterative access
                i = expected.size - 1
                it.seekToLast()
                while (it.isValid()) {
                    assertEquals(expected[i--], it.entry())
                    it.prev()
                }
            }
        }
    }

    @Test
    fun zeroByteTests() {
        WriteBatchWithIndex(true).use { wbwi ->
            val zeroByteValue = byteArrayOf(0, 0)
            // add zero byte value
            wbwi.put(zeroByteValue, zeroByteValue)

            val buffer = allocateDirectByteBuffer(zeroByteValue.size)
            buffer.put(zeroByteValue)

            val expected = WriteEntry(
                WriteType.PUT,
                DirectSlice(buffer, zeroByteValue.size),
                DirectSlice(buffer, zeroByteValue.size)
            )

            wbwi.newIterator().use {
                it.seekToFirst()
                val actual = it.entry()
                assertEquals(actual, expected)
                assertEquals(it.entry().hashCode(), expected.hashCode())
            }
        }
    }

    @Test
    fun savePoints() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                WriteBatchWithIndex(true).use { wbwi ->
                    ReadOptions().use { readOptions ->
                        wbwi.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
                        wbwi.put("k2".encodeToByteArray(), "v2".encodeToByteArray())
                        wbwi.put("k3".encodeToByteArray(), "v3".encodeToByteArray())

                        assertEquals("v1", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k1"))
                        assertEquals("v2", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k2"))
                        assertEquals("v3", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k3"))

                        wbwi.setSavePoint()

                        wbwi.delete("k2".encodeToByteArray())
                        wbwi.put("k3".encodeToByteArray(), "v3-2".encodeToByteArray())

                        assertNull(getFromWriteBatchWithIndex(db, readOptions, wbwi, "k2"))
                        assertEquals("v3-2", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k3"))

                        wbwi.setSavePoint()

                        wbwi.put("k3".encodeToByteArray(), "v3-3".encodeToByteArray())
                        wbwi.put("k4".encodeToByteArray(), "v4".encodeToByteArray())

                        assertEquals("v3-3", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k3"))
                        assertEquals("v4", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k4"))

                        wbwi.rollbackToSavePoint()

                        assertNull(getFromWriteBatchWithIndex(db, readOptions, wbwi, "k2"))
                        assertEquals("v3-2", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k3"))
                        assertNull(getFromWriteBatchWithIndex(db, readOptions, wbwi, "k4"))

                        wbwi.rollbackToSavePoint()

                        assertEquals("v1", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k1"))
                        assertEquals("v2", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k2"))
                        assertEquals("v3", getFromWriteBatchWithIndex(db, readOptions, wbwi, "k3"))
                        assertNull(getFromWriteBatchWithIndex(db, readOptions, wbwi, "k4"))
                    }
                }
            }
        }
    }

    @Test
    fun restorePoints() {
        WriteBatchWithIndex().use { wbwi ->
            wbwi.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            wbwi.put("k2".encodeToByteArray(), "v2".encodeToByteArray())

            wbwi.setSavePoint()

            wbwi.put("k1".encodeToByteArray(), "123456789".encodeToByteArray())
            wbwi.delete("k2".encodeToByteArray())

            wbwi.rollbackToSavePoint()

            DBOptions().use { options ->
                assertContentEquals("v1".encodeToByteArray(), wbwi.getFromBatch(options, "k1".encodeToByteArray()))
                assertContentEquals("v2".encodeToByteArray(), wbwi.getFromBatch(options, "k2".encodeToByteArray()))
            }
        }
    }

    @Test
    fun restorePoints_withoutSavePoints() {
        WriteBatchWithIndex().use { wbwi ->
            assertFailsWith<RocksDBException> {
                wbwi.rollbackToSavePoint()
            }
        }
    }

    @Test
    fun restorePoints_withoutSavePoints_nested() {
        WriteBatchWithIndex().use { wbwi ->

            wbwi.setSavePoint()
            wbwi.rollbackToSavePoint()

            assertFailsWith<RocksDBException> {
                // without previous corresponding setSavePoint
                wbwi.rollbackToSavePoint()
            }
        }
    }

    @Test
    fun popSavePoint() {
        WriteBatchWithIndex().use { wbwi ->

            wbwi.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            wbwi.put("k2".encodeToByteArray(), "v2".encodeToByteArray())

            wbwi.setSavePoint()

            wbwi.put("k1".encodeToByteArray(), "123456789".encodeToByteArray())
            wbwi.delete("k2".encodeToByteArray())

            wbwi.setSavePoint()

            wbwi.popSavePoint()

            wbwi.rollbackToSavePoint()

            DBOptions().use { options ->
                assertContentEquals("v1".encodeToByteArray(), wbwi.getFromBatch(options, "k1".encodeToByteArray()))
                assertContentEquals("v2".encodeToByteArray(), wbwi.getFromBatch(options, "k2".encodeToByteArray()))
            }
        }
    }

    @Test//(expected = RocksDBException::class)
    fun popSavePoint_withoutSavePoints() {
        WriteBatchWithIndex().use { wbwi ->
            assertFailsWith<RocksDBException> {
                wbwi.popSavePoint()
            }
        }
    }

    @Test
    fun popSavePoint_withoutSavePoints_nested() {
        WriteBatchWithIndex().use { wbwi ->
            wbwi.setSavePoint()
            wbwi.popSavePoint()

            assertFailsWith<RocksDBException> {
                // without previous corresponding setSavePoint
                wbwi.popSavePoint()
            }
        }
    }

    @Test
    fun maxBytes() {
        WriteBatchWithIndex().use { wbwi ->
            wbwi.setMaxBytes(19)
            wbwi.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
        }
    }

    @Test
    fun maxBytes_over() {
        WriteBatchWithIndex().use { wbwi ->
            wbwi.setMaxBytes(1)
            assertFailsWith<RocksDBException> {
                wbwi.put("k1".encodeToByteArray(), "v1".encodeToByteArray())
            }
        }
    }

    @Test
    fun getWriteBatch() {
        WriteBatchWithIndex().use { wbwi ->
            val wb = wbwi.getWriteBatch()
            assertNotNull(wb)
        }
    }

    @Test
    fun getFromBatch() {
        val k1 = "k1".encodeToByteArray()
        val k2 = "k2".encodeToByteArray()
        val k3 = "k3".encodeToByteArray()
        val k4 = "k4".encodeToByteArray()

        val v1 = "v1".encodeToByteArray()
        val v2 = "v2".encodeToByteArray()
        val v3 = "v3".encodeToByteArray()

        WriteBatchWithIndex(true).use { wbwi ->
            DBOptions().use { dbOptions ->
                wbwi.put(k1, v1)
                wbwi.put(k2, v2)
                wbwi.put(k3, v3)

                assertContentEquals(v1, wbwi.getFromBatch(dbOptions, k1))
                assertContentEquals(v2, wbwi.getFromBatch(dbOptions, k2))
                assertContentEquals(v3, wbwi.getFromBatch(dbOptions, k3))
                assertNull(wbwi.getFromBatch(dbOptions, k4))

                wbwi.delete(k2)

                assertNull(wbwi.getFromBatch(dbOptions, k2))
            }
        }
    }

    @Test
    fun getFromBatchAndDB() {
        val k1 = "k1".encodeToByteArray()
        val k2 = "k2".encodeToByteArray()
        val k3 = "k3".encodeToByteArray()
        val k4 = "k4".encodeToByteArray()

        val v1 = "v1".encodeToByteArray()
        val v2 = "v2".encodeToByteArray()
        val v3 = "v3".encodeToByteArray()
        val v4 = "v4".encodeToByteArray()

        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(
                options,
                createTestFolder()
            ).use { db ->
                db.put(k1, v1)
                db.put(k2, v2)
                db.put(k4, v4)

                WriteBatchWithIndex(true).use { wbwi ->
                    DBOptions().use { dbOptions ->
                        ReadOptions().use { readOptions ->
                            assertNull(wbwi.getFromBatch(dbOptions, k1))
                            assertNull(wbwi.getFromBatch(dbOptions, k2))
                            assertNull(wbwi.getFromBatch(dbOptions, k4))

                            wbwi.put(k3, v3)

                            assertContentEquals(v3, wbwi.getFromBatch(dbOptions, k3))

                            assertContentEquals(v1, wbwi.getFromBatchAndDB(db, readOptions, k1))
                            assertContentEquals(v2, wbwi.getFromBatchAndDB(db, readOptions, k2))
                            assertContentEquals(v3, wbwi.getFromBatchAndDB(db, readOptions, k3))
                            assertContentEquals(v4, wbwi.getFromBatchAndDB(db, readOptions, k4))

                            wbwi.delete(k4)

                            assertNull(wbwi.getFromBatchAndDB(db, readOptions, k4))
                        }
                    }
                }
            }
        }
    }

    private fun toArray(buf: ByteBuffer): ByteArray {
        val ary = ByteArray(buf.remaining())
        buf[ary]
        return ary
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
                    assertContentEquals("value".encodeToByteArray(), db["key1".encodeToByteArray()])
                    assertContentEquals("12345678".encodeToByteArray(), db["key2".encodeToByteArray()])
                    assertContentEquals("abcdefg".encodeToByteArray(), db["key3".encodeToByteArray()])
                    assertContentEquals("xyz".encodeToByteArray(), db["key4".encodeToByteArray()])

                    batch.deleteRange("key2".encodeToByteArray(), "key4".encodeToByteArray())
                    db.write(wOpt, batch)

                    assertContentEquals("value".encodeToByteArray(), db["key1".encodeToByteArray()])
                    assertNull(db["key2".encodeToByteArray()])
                    assertNull(db["key3".encodeToByteArray()])
                    assertContentEquals("xyz".encodeToByteArray(), db["key4".encodeToByteArray()])
                }
            }
        }
    }
}

private fun getFromWriteBatchWithIndex(
    db: RocksDB,
    readOptions: ReadOptions, wbwi: WriteBatchWithIndex,
    skey: String
): String? {
    val key = skey.encodeToByteArray()
    db.newIterator(readOptions).use { baseIterator ->
        wbwi.newIteratorWithBase(baseIterator).use { iterator ->
            iterator.seek(key)
            // key.contentEquals(iterator.key()) ensures an exact match in Rocks,
            // instead of a nearest match
            return if (iterator.isValid() && key.contentEquals(iterator.key())){
                iterator.value().decodeToString()
            } else null
        }
    }
}
