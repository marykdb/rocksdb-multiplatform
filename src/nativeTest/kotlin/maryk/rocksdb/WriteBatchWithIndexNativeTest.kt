package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class WriteBatchWithIndexNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun nestedIteratorRetainsEveryBatchOwner() {
        for (variant in 0..3) {
            for (rollback in listOf(false, true)) {
                val dbPath = createTestDBFolder("WriteBatchWithIndexNativeTest_nested_${variant}_$rollback")
                Options().setCreateIfMissing(true).use { options ->
                    openRocksDB(options, dbPath).use { db ->
                        db.getDefaultColumnFamily().use { cf ->
                            ReadOptions().use { readOptions ->
                                WriteBatchWithIndex().use { inner ->
                                    WriteBatchWithIndex().use { outer ->
                                        inner.setSavePoint()
                                        inner.put("a".encodeToByteArray(), "inner".encodeToByteArray())
                                        outer.put("b".encodeToByteArray(), "outer".encodeToByteArray())
                                        val base = inner.newIteratorWithBase(db.newIterator())
                                        val combined = when (variant) {
                                            0 -> outer.newIteratorWithBase(base)
                                            1 -> outer.newIteratorWithBase(base, readOptions)
                                            2 -> outer.newIteratorWithBase(cf, base)
                                            else -> outer.newIteratorWithBase(cf, base, readOptions)
                                        }
                                        combined.use {
                                            it.seekToFirst()
                                            assertEquals("inner", it.value().decodeToString())
                                            it.next()
                                            assertEquals("outer", it.value().decodeToString())
                                            if (rollback) inner.rollbackToSavePoint() else inner.close()
                                            // Check the wrapper without dereferencing freed native state.
                                            assertFalse(it.isOwningHandle(), "Invalidating any contributing batch must invalidate the iterator")
                                            assertFailsWith<IllegalStateException> { it.seekToFirst() }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun deleteRangeReportsUpstreamUnsupportedOperation() {
        WriteBatchWithIndex().use { batch ->
            assertFailsWith<RocksDBException> {
                batch.deleteRange("a".encodeToByteArray(), "z".encodeToByteArray())
            }
        }
    }

    @Test
    fun iteratorWithBaseRejectsUseAfterRollbackToSavePoint() {
        val dbPath = createTestDBFolder("WriteBatchWithIndexNativeTest_iterator_rollback")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                WriteBatchWithIndex().use { batch ->
                    batch.setSavePoint()
                    batch.put("key".encodeToByteArray(), "value".encodeToByteArray())
                    val iterator = batch.newIteratorWithBase(db.newIterator())

                    batch.rollbackToSavePoint()

                    assertFailsWith<IllegalStateException> {
                        iterator.seekToFirst()
                    }
                }
            }
        }
    }

    @Test
    fun iteratorWithBaseRejectsUseAfterBatchClose() {
        val dbPath = createTestDBFolder("WriteBatchWithIndexNativeTest_iterator_close")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                val batch = WriteBatchWithIndex()
                batch.put("key".encodeToByteArray(), "value".encodeToByteArray())
                val iterator = batch.newIteratorWithBase(db.newIterator())

                batch.close()

                assertFailsWith<IllegalStateException> {
                    iterator.seekToFirst()
                }
            }
        }
    }

    @Test
    fun iteratorWithBaseRejectsUseAfterDbClose() {
        val dbPath = createTestDBFolder("WriteBatchWithIndexNativeTest_iterator_db_close")
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            WriteBatchWithIndex().use { batch ->
                batch.put("key".encodeToByteArray(), "value".encodeToByteArray())
                val iterator = batch.newIteratorWithBase(db.newIterator())

                db.close()

                assertFailsWith<IllegalStateException> {
                    iterator.seekToFirst()
                }
            }
        }
    }
}
