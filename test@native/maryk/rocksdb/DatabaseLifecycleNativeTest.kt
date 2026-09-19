package maryk.rocksdb

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import maryk.rocksdb.util.createTestDBFolder
import kotlin.test.Test
import kotlin.test.assertFalse

class DatabaseLifecycleNativeTest {
    @Test
    fun iteratorCloseCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, createTestDBFolder("DatabaseLifecycleNativeTest_iterator"))
            val iterators = List(1_024) { db.newIterator() }

            race(iterators.first()::close, db::close)
        }
    }

    @Test
    fun transactionLogIteratorCloseCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, createTestDBFolder("DatabaseLifecycleNativeTest_wal"))
            db.put("key".encodeToByteArray(), "value".encodeToByteArray())
            val iterators = List(1_024) { db.getUpdatesSince(0) }

            race(iterators.first()::close, db::close)
        }
    }

    @Test
    fun columnFamilyCloseCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, createTestDBFolder("DatabaseLifecycleNativeTest_column_family"))
            val handles = ColumnFamilyOptions().use { columnFamilyOptions ->
                db.createColumnFamilies(
                    columnFamilyOptions,
                    List(128) { "cf-$it".encodeToByteArray() },
                )
            }

            race(handles.first()::close, db::close)
        }
    }

    @Test
    fun transactionCloseCanRaceWithTransactionDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                WriteOptions().use { writeOptions ->
                    val db = openTransactionDB(
                        options,
                        transactionDbOptions,
                        createTestDBFolder("DatabaseLifecycleNativeTest_transaction"),
                    )
                    val transactions = List(1_024) { db.beginTransaction(writeOptions) }

                    race(transactions.first()::close, db::close)
                }
            }
        }
    }

    @Test
    fun optimisticTransactionCloseCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            WriteOptions().use { writeOptions ->
                val db = openOptimisticTransactionDB(
                    options,
                    createTestDBFolder("DatabaseLifecycleNativeTest_optimistic_transaction"),
                )
                val transactions = List(1_024) { db.beginTransaction(writeOptions) }

                race(transactions.first()::close, db::close)
            }
        }
    }

    @Test
    fun borrowedBaseDatabaseCloseCanRaceWithOwnerClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            val db = openOptimisticTransactionDB(
                options,
                createTestDBFolder("DatabaseLifecycleNativeTest_borrowed_base"),
            )
            val borrowedBaseDbs = List(1_024) { db.getBaseDB() }

            race(borrowedBaseDbs.first()::close, db::close)
        }
    }

    @Test
    fun databaseChildRegistrationCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            repeat(8) { iteration ->
                val db = openRocksDB(
                    options,
                    createTestDBFolder("DatabaseLifecycleNativeTest_register_iterator_$iteration"),
                )
                raceCreation(db::newIterator, db::close)
            }
            repeat(8) { iteration ->
                val db = openRocksDB(
                    options,
                    createTestDBFolder("DatabaseLifecycleNativeTest_register_wal_$iteration"),
                )
                db.put("key".encodeToByteArray(), "value".encodeToByteArray())
                raceCreation({ db.getUpdatesSince(0) }, db::close)
            }
            repeat(8) { iteration ->
                val db = openRocksDB(
                    options,
                    createTestDBFolder("DatabaseLifecycleNativeTest_register_snapshot_$iteration"),
                )
                raceCreation({ requireNotNull(db.getSnapshot()) }, db::close)
            }
            repeat(8) { iteration ->
                val db = openRocksDB(
                    options,
                    createTestDBFolder("DatabaseLifecycleNativeTest_register_column_family_$iteration"),
                )
                raceCreation(
                    { db.createColumnFamily(ColumnFamilyDescriptor("cf".encodeToByteArray())) },
                    db::close,
                )
            }
            repeat(8) { iteration ->
                val db = openRocksDB(
                    options,
                    createTestDBFolder("DatabaseLifecycleNativeTest_register_indexed_iterator_$iteration"),
                )
                WriteBatchWithIndex().use { batch ->
                    val baseIterator = db.newIterator()
                    raceCreation({ batch.newIteratorWithBase(baseIterator) }, db::close)
                }
            }
        }
    }

    @Test
    fun transactionRegistrationCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                WriteOptions().use { writeOptions ->
                    repeat(8) { iteration ->
                        val db = openTransactionDB(
                            options,
                            transactionDbOptions,
                            createTestDBFolder("DatabaseLifecycleNativeTest_register_transaction_$iteration"),
                        )
                        raceCreation({ db.beginTransaction(writeOptions) }, db::close)
                    }
                }
            }
        }
    }

    @Test
    fun optimisticChildrenRegistrationCanRaceWithDatabaseClose() = runBlocking {
        Options().setCreateIfMissing(true).use { options ->
            WriteOptions().use { writeOptions ->
                repeat(8) { iteration ->
                    val db = openOptimisticTransactionDB(
                        options,
                        createTestDBFolder("DatabaseLifecycleNativeTest_register_optimistic_transaction_$iteration"),
                    )
                    raceCreation({ db.beginTransaction(writeOptions) }, db::close)
                }
                repeat(8) { iteration ->
                    val db = openOptimisticTransactionDB(
                        options,
                        createTestDBFolder("DatabaseLifecycleNativeTest_register_base_db_$iteration"),
                    )
                    raceCreation(db::getBaseDB, db::close)
                }
            }
        }
    }

    private suspend fun race(closeChild: () -> Unit, closeOwner: () -> Unit) = coroutineScope {
        val start = CompletableDeferred<Unit>()
        val childClose = launch(Dispatchers.Default) {
            start.await()
            closeChild()
        }

        start.complete(Unit)
        closeOwner()
        childClose.join()
    }

    private suspend fun <T : RocksObject> raceCreation(
        createChild: () -> T,
        closeOwner: () -> Unit,
    ) = coroutineScope {
        val start = CompletableDeferred<Unit>()
        val result = CompletableDeferred<Result<T>>()
        launch(Dispatchers.Default) {
            start.await()
            result.complete(runCatching(createChild))
        }

        start.complete(Unit)
        closeOwner()
        val creation = result.await()
        creation.exceptionOrNull()?.let { throwable ->
            if (throwable !is IllegalStateException) throw throwable
        }
        creation.getOrNull()?.let { child ->
            assertFalse(child.isOwningHandle())
            child.close()
        }
    }
}
