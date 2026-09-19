package maryk.rocksdb

import maryk.rocksdb.util.ThreadSafeCounter
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ReadOptionsNativeTest {
    init {
        loadRocksDBLibrary()
    }

    @Test
    fun iterateBoundsRejectClosedDirectSlice() {
        ReadOptions().use { options ->
            val bound = DirectSlice("z")
            bound.close()

            assertFailsWith<IllegalStateException> {
                options.setIterateUpperBound(bound)
            }
        }
    }

    @Test
    fun dbReadRejectsClosedSnapshotInReadOptions() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_closed_snapshot_db")
        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                val snapshot = requireNotNull(db.getSnapshot())
                ReadOptions().use { readOptions ->
                    readOptions.setSnapshot(snapshot)
                    snapshot.close()

                    assertFailsWith<IllegalStateException> {
                        db.get(readOptions, "key".encodeToByteArray())
                    }
                }
            }
        }
    }

    @Test
    fun transactionReadRejectsClosedSnapshotInReadOptions() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_closed_snapshot_txn")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    val snapshot = requireNotNull(db.getSnapshot())
                    ReadOptions().use { readOptions ->
                        readOptions.setSnapshot(snapshot)
                        snapshot.close()

                        WriteOptions().use { writeOptions ->
                            db.beginTransaction(writeOptions).use { transaction ->
                                assertFailsWith<IllegalStateException> {
                                    transaction.get(readOptions, "key".encodeToByteArray())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionReadRejectsClearedTransactionSnapshotInReadOptions() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_cleared_txn_snapshot")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            transaction.setSnapshot()
                            val snapshot = requireNotNull(transaction.getSnapshot())
                            ReadOptions().use { readOptions ->
                                readOptions.setSnapshot(snapshot)
                                transaction.clearSnapshot()

                                assertFailsWith<IllegalStateException> {
                                    transaction.get(readOptions, "key".encodeToByteArray())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionReadRejectsClosedTransactionSnapshotOwnerInReadOptions() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_closed_txn_snapshot_owner")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        val transaction = db.beginTransaction(writeOptions)
                        transaction.setSnapshot()
                        val snapshot = requireNotNull(transaction.getSnapshot())
                        ReadOptions().use { readOptions ->
                            readOptions.setSnapshot(snapshot)
                            transaction.close()

                            assertFailsWith<IllegalStateException> {
                                db.get(readOptions, "key".encodeToByteArray())
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionReadRejectsNotifierSnapshotAfterTransactionClose() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_notifier_txn_snapshot")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        RecordingTransactionNotifier().use { notifier ->
                            val transaction = db.beginTransaction(writeOptions)
                            transaction.setSnapshotOnNextOperation(notifier)
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                            val snapshot = assertNotNull(notifier.snapshot)

                            ReadOptions().use { readOptions ->
                                readOptions.setSnapshot(snapshot)
                                transaction.close()

                                assertFailsWith<IllegalStateException> {
                                    db.get(readOptions, "key".encodeToByteArray())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionSnapshotNotifierCanBeReplacedAndClosedRepeatedly() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_notifier_replaced")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        repeat(50) { index ->
                            CountingTransactionNotifier().use { notifier ->
                                db.beginTransaction(writeOptions).use { transaction ->
                                    transaction.setSnapshotOnNextOperation(notifier)
                                    transaction.setSnapshotOnNextOperation(notifier)
                                    transaction.close()
                                }
                                assertEquals(0, notifier.createdSnapshots)
                            }

                            CountingTransactionNotifier().use { notifier ->
                                db.beginTransaction(writeOptions).use { transaction ->
                                    transaction.setSnapshotOnNextOperation(notifier)
                                    transaction.put("notifier-key-$index".encodeToByteArray(), "value".encodeToByteArray())
                                }
                                assertEquals(1, notifier.createdSnapshots)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun tableFilterCallbackExceptionDoesNotEscapeIteratorRead() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_table_filter_exception")
        val callbackCount = ThreadSafeCounter()

        Options().setCreateIfMissing(true).use { options ->
            openRocksDB(options, dbPath).use { db ->
                repeat(50) { index ->
                    db.put("table-filter-key-$index".encodeToByteArray(), "value-$index".encodeToByteArray())
                }
                db.compactRange()

                ReadOptions().use { readOptions ->
                    readOptions.setTableFilter(ThrowingTableFilter(callbackCount))

                    db.newIterator(readOptions).use { iterator ->
                        iterator.seekToFirst()
                        assertEquals(false, iterator.isValid())
                        iterator.status()
                    }
                }
            }
        }

        assertTrue(callbackCount.value() > 0, "Expected RocksDB to invoke the table filter during iteration")
    }

    @Test
    fun transactionIteratorRejectsUseAfterCommit() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_txn_iterator_commit")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                            val iterator = transaction.getIterator()
                            transaction.commit()

                            assertFailsWith<IllegalStateException> {
                                iterator.isValid()
                            }
                            iterator.close()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionIteratorRejectsUseAfterRollback() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_txn_iterator_rollback")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                            val iterator = transaction.getIterator()
                            transaction.rollback()

                            assertFailsWith<IllegalStateException> {
                                iterator.isValid()
                            }
                            iterator.close()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transactionIteratorRejectsUseAfterRollbackToSavePoint() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_txn_iterator_savepoint")
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionOptions ->
                openTransactionDB(options, transactionOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            transaction.setSavePoint()
                            transaction.put("key".encodeToByteArray(), "value".encodeToByteArray())
                            val iterator = transaction.getIterator()
                            transaction.rollbackToSavePoint()

                            assertFailsWith<IllegalStateException> {
                                iterator.isValid()
                            }
                            iterator.close()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun dbIteratorRejectsUseAfterDbClose() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_db_iterator_close")
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            val iterator = db.newIterator()
            db.close()

            assertFailsWith<IllegalStateException> {
                iterator.isValid()
            }
            iterator.close()
        }
    }

    @Test
    fun columnFamilyHandleRejectsUseAfterDbClose() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_cf_handle_db_close")
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            val columnFamilyHandle = db.createColumnFamily(ColumnFamilyDescriptor("cf".encodeToByteArray()))
            db.close()

            assertFailsWith<IllegalStateException> {
                columnFamilyHandle.getID()
            }
            columnFamilyHandle.close()
        }
    }

    @Test
    fun dbColumnFamilyIteratorRejectsUseAfterDbClose() {
        val dbPath = createTestDBFolder("ReadOptionsNativeTest_db_cf_iterator_close")
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            val columnFamilyHandle = db.createColumnFamily(ColumnFamilyDescriptor("cf".encodeToByteArray()))
            val iterator = db.newIterator(columnFamilyHandle)
            db.close()

            assertFailsWith<IllegalStateException> {
                iterator.isValid()
            }
            iterator.close()
            columnFamilyHandle.close()
        }
    }

}

private class RecordingTransactionNotifier : AbstractTransactionNotifier() {
    var snapshot: Snapshot? = null

    override fun snapshotCreated(newSnapshot: Snapshot) {
        snapshot = newSnapshot
    }
}

private class CountingTransactionNotifier : AbstractTransactionNotifier() {
    var createdSnapshots = 0
        private set

    override fun snapshotCreated(newSnapshot: Snapshot) {
        createdSnapshots++
    }
}

private class ThrowingTableFilter(
    private val callbackCount: ThreadSafeCounter
) : AbstractTableFilter() {
    override fun filter(tableProperties: TableProperties): Boolean {
        tableProperties.numEntries()
        callbackCount.increment()
        throw IllegalStateException("table filter failure")
    }
}
