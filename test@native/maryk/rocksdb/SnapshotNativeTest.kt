package maryk.rocksdb

import maryk.rocksdb.util.createTestDBFolder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SnapshotNativeTest {
    @Test
    fun snapshotReleaseCanRaceWithDatabaseClose() = runBlocking {
        val dbPath = createTestDBFolder("SnapshotNativeTest_concurrent_close")

        Options().setCreateIfMissing(true).use { options ->
            repeat(32) {
                val db = openOptimisticTransactionDB(options, dbPath)
                val snapshots = List(1_024) { requireNotNull(db.getSnapshot()) }
                val start = CompletableDeferred<Unit>()
                val release = launch(Dispatchers.Default) {
                    start.await()
                    snapshots.first().close()
                }

                start.complete(Unit)
                db.close()
                release.join()
            }
        }
    }

    @Test
    fun databaseCloseInvalidatesSnapshot() {
        val dbPath = createTestDBFolder("SnapshotNativeTest_owner")

        Options().setCreateIfMissing(true).use { options ->
            val db = openRocksDB(options, dbPath)
            val snapshot = requireNotNull(db.getSnapshot())

            db.close()

            assertFailsWith<IllegalStateException> {
                snapshot.getSequenceNumber()
            }
            snapshot.close()
        }
    }

    @Test
    fun transactionSnapshotWrapperCanBeClosed() {
        val dbPath = createTestDBFolder("SnapshotNativeTest_transaction_wrapper")

        Options().setCreateIfMissing(true).use { options ->
            TransactionDBOptions().use { transactionDbOptions ->
                openTransactionDB(options, transactionDbOptions, dbPath).use { db ->
                    WriteOptions().use { writeOptions ->
                        db.beginTransaction(writeOptions).use { transaction ->
                            transaction.setSnapshot()

                            val snapshot = requireNotNull(transaction.getSnapshot())
                            snapshot.close()
                        }
                    }
                }
            }
        }
    }
}
