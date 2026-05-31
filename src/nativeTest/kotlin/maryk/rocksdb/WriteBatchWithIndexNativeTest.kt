package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFailsWith

class WriteBatchWithIndexNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun deleteRangeIsRejectedBeforeUnsupportedNativeCall() {
        WriteBatchWithIndex().use { batch ->
            assertFailsWith<UnsupportedOperationException> {
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
