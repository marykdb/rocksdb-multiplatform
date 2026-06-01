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

    @Test
    fun getWriteBatchWithIndex() {
        WriteBatchWithIndex().use { batch ->
            batch.put("k1".encodeToByteArray(), "v1".encodeToByteArray())

            val writeBatch = batch.getWriteBatch()
            assertFalse(writeBatch.isOwningHandle())
            assertEquals(1, writeBatch.count())

            batch.put("k2".encodeToByteArray(), "v2".encodeToByteArray())

            assertEquals(2, writeBatch.count())
            writeBatch.close()
        }
    }

    @Test
    fun writeBatchWithIndexGetFromBatchSupportsEmptyValue() {
        DBOptions().use { options ->
            WriteBatchWithIndex().use { batch ->
                val key = "empty-value".encodeToByteArray()
                batch.put(key, ByteArray(0))

                assertContentEquals(ByteArray(0), batch.getFromBatch(options, key))

                batch.delete(key)
                assertNull(batch.getFromBatch(options, key))
            }
        }
    }

    @Test
    fun writeBatchWithIndexGetFromBatchDoesNotResurrectEmptyValueAfterSingleDelete() {
        DBOptions().use { options ->
            WriteBatchWithIndex().use { batch ->
                val key = "empty-value".encodeToByteArray()
                batch.put(key, ByteArray(0))
                batch.singleDelete(key)

                assertNull(batch.getFromBatch(options, key))
            }
        }
    }

    @Test
    fun writeBatchWithIndexGetFromBatchColumnFamilySupportsEmptyValue() {
        Options().setCreateIfMissing(true).use { options ->
            DBOptions().use { dbOptions ->
                openRocksDB(options, createTestFolder()).use { db ->
                    db.createColumnFamily(ColumnFamilyDescriptor("new_cf".encodeToByteArray())).use { columnFamily ->
                        WriteBatchWithIndex().use { batch ->
                            val key = "empty-value".encodeToByteArray()
                            batch.put(columnFamily, key, ByteArray(0))

                            assertContentEquals(ByteArray(0), batch.getFromBatch(columnFamily, dbOptions, key))

                            batch.delete(columnFamily, key)
                            assertNull(batch.getFromBatch(columnFamily, dbOptions, key))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun writeBatchWithIndexGetFromBatchAndDBReadsDbAndBatchValues() {
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, createTestFolder()).use { db ->
                ReadOptions().use { readOptions ->
                    WriteBatchWithIndex().use { batch ->
                        val dbOnlyKey = "db-only".encodeToByteArray()
                        val batchKey = "batch-key".encodeToByteArray()
                        val emptyKey = "empty-key".encodeToByteArray()

                        db.put(dbOnlyKey, "db-value".encodeToByteArray())
                        db.put(batchKey, "old-value".encodeToByteArray())
                        db.put(emptyKey, "old-empty-value".encodeToByteArray())

                        assertContentEquals(
                            "db-value".encodeToByteArray(),
                            batch.getFromBatchAndDB(db, readOptions, dbOnlyKey)
                        )

                        batch.put(batchKey, "batch-value".encodeToByteArray())
                        batch.put(emptyKey, ByteArray(0))
                        batch.delete(dbOnlyKey)

                        assertContentEquals(
                            "batch-value".encodeToByteArray(),
                            batch.getFromBatchAndDB(db, readOptions, batchKey)
                        )
                        assertContentEquals(ByteArray(0), batch.getFromBatchAndDB(db, readOptions, emptyKey))
                        assertNull(batch.getFromBatchAndDB(db, readOptions, dbOnlyKey))
                    }
                }
            }
        }
    }

    @Test
    fun writeBatchWithIndexGetFromBatchAndDBColumnFamilyReadsDbAndBatchValues() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { dbOptions ->
            val columnFamilies = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor("new_cf".encodeToByteArray())
            )
            val handles = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(dbOptions, createTestFolder(), columnFamilies, handles).use { db ->
                try {
                    ReadOptions().use { readOptions ->
                        WriteBatchWithIndex().use { batch ->
                            val columnFamily = handles[1]
                            val dbOnlyKey = "cf-db-only".encodeToByteArray()
                            val batchKey = "cf-batch-key".encodeToByteArray()

                            db.put(columnFamily, dbOnlyKey, "cf-db-value".encodeToByteArray())
                            db.put(columnFamily, batchKey, "cf-old-value".encodeToByteArray())

                            assertContentEquals(
                                "cf-db-value".encodeToByteArray(),
                                batch.getFromBatchAndDB(db, columnFamily, readOptions, dbOnlyKey)
                            )

                            batch.put(columnFamily, batchKey, "cf-batch-value".encodeToByteArray())
                            batch.delete(columnFamily, dbOnlyKey)

                            assertContentEquals(
                                "cf-batch-value".encodeToByteArray(),
                                batch.getFromBatchAndDB(db, columnFamily, readOptions, batchKey)
                            )
                            assertNull(batch.getFromBatchAndDB(db, columnFamily, readOptions, dbOnlyKey))
                        }
                    }
                } finally {
                    handles.forEach { it.close() }
                }
            }
        }
    }

    @Test
    fun writeBatchWithIndexDirectIteratorReturnsEntries() {
        WriteBatchWithIndex().use { batch ->
            val key = "iter-key".encodeToByteArray()
            val value = "iter-value".encodeToByteArray()
            batch.put(key, value)

            batch.newIterator().use { iterator ->
                iterator.seekToFirst()
                assertTrue(iterator.isValid())
                iterator.entry().use { entry ->
                    assertEquals(WriteType.PUT, entry.getType())
                    assertEquals("iter-key", entry.getKey().toString(false))
                    assertEquals("iter-value", entry.getValue()?.toString(false))
                }
            }
        }
    }

    @Test
    fun writeBatchWithIndexDirectIteratorDeleteEntryHasNoValue() {
        WriteBatchWithIndex().use { batch ->
            batch.delete("iter-delete-key".encodeToByteArray())

            batch.newIterator().use { iterator ->
                iterator.seekToFirst()
                assertTrue(iterator.isValid())
                iterator.entry().use { entry ->
                    assertEquals(WriteType.DELETE, entry.getType())
                    assertEquals("iter-delete-key", entry.getKey().toString(false))
                    assertNull(entry.getValue())
                }
            }
        }
    }

    @Test
    fun writeBatchWithIndexDirectIteratorColumnFamilyReturnsEntries() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { dbOptions ->
            val columnFamilies = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor("iter_cf".encodeToByteArray())
            )
            val handles = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(dbOptions, createTestFolder(), columnFamilies, handles).use {
                try {
                    WriteBatchWithIndex().use { batch ->
                        val columnFamily = handles[1]
                        batch.put("default-key".encodeToByteArray(), "default-value".encodeToByteArray())
                        batch.put(columnFamily, "cf-key".encodeToByteArray(), "cf-value".encodeToByteArray())

                        batch.newIterator(columnFamily).use { iterator ->
                            iterator.seekToFirst()
                            assertTrue(iterator.isValid())
                            iterator.entry().use { entry ->
                                assertEquals(WriteType.PUT, entry.getType())
                                assertEquals("cf-key", entry.getKey().toString(false))
                                assertEquals("cf-value", entry.getValue()?.toString(false))
                            }
                            iterator.next()
                            assertFalse(iterator.isValid())
                        }
                    }
                } finally {
                    handles.forEach { it.close() }
                }
            }
        }
    }

    @Test
    fun newIteratorWithBaseTransfersBaseIteratorOwnership() {
        openRocksDB(createTestFolder()).use { db ->
            db.put("a".encodeToByteArray(), "db".encodeToByteArray())

            WriteBatchWithIndex().use { batch ->
                batch.put("b".encodeToByteArray(), "batch".encodeToByteArray())

                val baseIterator = db.newIterator()
                batch.newIteratorWithBase(baseIterator).use { iterator ->
                    baseIterator.close()

                    iterator.seekToFirst()
                    assertTrue(iterator.isValid())
                    assertContentEquals("a".encodeToByteArray(), iterator.key())
                    assertContentEquals("db".encodeToByteArray(), iterator.value())

                    iterator.next()
                    assertTrue(iterator.isValid())
                    assertContentEquals("b".encodeToByteArray(), iterator.key())
                    assertContentEquals("batch".encodeToByteArray(), iterator.value())
                }
            }
        }
    }

    @Test
    fun newIteratorWithBaseReadOptionsReadsDbAndBatchValues() {
        openRocksDB(createTestFolder()).use { db ->
            db.put("a".encodeToByteArray(), "db".encodeToByteArray())

            ReadOptions().use { readOptions ->
                WriteBatchWithIndex().use { batch ->
                    batch.put("b".encodeToByteArray(), "batch".encodeToByteArray())

                    batch.newIteratorWithBase(db.newIterator(), readOptions).use { iterator ->
                        iterator.seekToFirst()
                        assertTrue(iterator.isValid())
                        assertContentEquals("a".encodeToByteArray(), iterator.key())
                        assertContentEquals("db".encodeToByteArray(), iterator.value())

                        iterator.next()
                        assertTrue(iterator.isValid())
                        assertContentEquals("b".encodeToByteArray(), iterator.key())
                        assertContentEquals("batch".encodeToByteArray(), iterator.value())
                    }
                }
            }
        }
    }

    @Test
    fun newIteratorWithBaseColumnFamilyReadOptionsReadsDbAndBatchValues() {
        DBOptions().apply {
            setCreateIfMissing(true)
            setCreateMissingColumnFamilies(true)
        }.use { dbOptions ->
            val columnFamilies = listOf(
                ColumnFamilyDescriptor(defaultColumnFamily),
                ColumnFamilyDescriptor("base_cf".encodeToByteArray())
            )
            val handles = mutableListOf<ColumnFamilyHandle>()
            openRocksDB(dbOptions, createTestFolder(), columnFamilies, handles).use { db ->
                try {
                    val columnFamily = handles[1]
                    db.put(columnFamily, "a".encodeToByteArray(), "cf-db".encodeToByteArray())

                    ReadOptions().use { readOptions ->
                        WriteBatchWithIndex().use { batch ->
                            batch.put(columnFamily, "b".encodeToByteArray(), "cf-batch".encodeToByteArray())

                            batch.newIteratorWithBase(columnFamily, db.newIterator(columnFamily), readOptions).use { iterator ->
                                iterator.seekToFirst()
                                assertTrue(iterator.isValid())
                                assertContentEquals("a".encodeToByteArray(), iterator.key())
                                assertContentEquals("cf-db".encodeToByteArray(), iterator.value())

                                iterator.next()
                                assertTrue(iterator.isValid())
                                assertContentEquals("b".encodeToByteArray(), iterator.key())
                                assertContentEquals("cf-batch".encodeToByteArray(), iterator.value())
                            }
                        }
                    }
                } finally {
                    handles.forEach { it.close() }
                }
            }
        }
    }
}
